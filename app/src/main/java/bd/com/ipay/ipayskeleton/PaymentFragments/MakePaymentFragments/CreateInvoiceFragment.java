package bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.MakePaymentActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.CreateInvoiceRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.CreateInvoiceResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class CreateInvoiceFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mCreateInvoiceTask = null;
    private CreateInvoiceResponse mCreateInvoiceResponse;

    private final int PICK_CONTACT = 100;

    private Button buttonCreateInvoice;
    private Button buttonSelectFromContacts;
    private EditText mMobileNumberEditText;
    private EditText mDescriptionEditText;
    private EditText mAmountEditText;
    private EditText mVATEditText;
    private ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_create_invoice, container, false);
        mMobileNumberEditText = (EditText) v.findViewById(R.id.mobile_number);
        buttonSelectFromContacts = (Button) v.findViewById(R.id.select_sender_from_contacts);
        buttonCreateInvoice = (Button) v.findViewById(R.id.button_request_money);
        mDescriptionEditText = (EditText) v.findViewById(R.id.description);
        mAmountEditText = (EditText) v.findViewById(R.id.amount);
        mVATEditText = (EditText) v.findViewById(R.id.vat);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.submitting_request_money));

        buttonSelectFromContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
            }
        });

        buttonCreateInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isConnectionAvailable(getActivity())) attemptSendInvoice();
                else if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            }
        });

        return v;
    }

    private void attemptSendInvoice() {
        if (mCreateInvoiceTask != null) {
            return;
        }

        boolean cancel = false;
        View focusView = null;

        String receiver = mMobileNumberEditText.getText().toString();
        String description = mDescriptionEditText.getText().toString();
        String amount = mAmountEditText.getText().toString();
        String vat = mVATEditText.getText().toString();

        // Check for a validation
        if (!(amount.length() > 0 && Double.parseDouble(amount) > 0)) {
            mAmountEditText.setError(getString(R.string.please_enter_amount));
            focusView = mAmountEditText;
            cancel = true;
        }

        if (!(vat.length() > 0 && Double.parseDouble(vat) > 0)) {
            mVATEditText.setError(getString(R.string.please_enter_vat_amount));
            focusView = mVATEditText;
            cancel = true;
        }

        if (description.length() == 0) {
            mDescriptionEditText.setError(getString(R.string.please_add_description));
            focusView = mDescriptionEditText;
            cancel = true;
        }

        if (receiver.length() == 0) {
            mMobileNumberEditText.setError(getString(R.string.enter_mobile_number));
            focusView = mMobileNumberEditText;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            // Format the number
            receiver = ContactEngine.formatMobileNumberBD(receiver);

            mProgressDialog.setMessage(getString(R.string.requesting_money));
            mProgressDialog.show();
            CreateInvoiceRequest mCreateInvoiceRequest = new CreateInvoiceRequest(description,
                    BigDecimal.valueOf(Double.parseDouble(amount)), BigDecimal.valueOf(Double.parseDouble(vat)), receiver);
            Gson gson = new Gson();
            String json = gson.toJson(mCreateInvoiceRequest);
            mCreateInvoiceTask = new HttpRequestPostAsyncTask(Constants.COMMAND_CREATE_INVOICE,
                    Constants.BASE_URL + Constants.URL_PAYMENT_CREATE_INVOICE, json, getActivity());
            mCreateInvoiceTask.mHttpResponseListener = this;
            mCreateInvoiceTask.execute((Void) null);
        }

    }

    @Override
    public void httpResponseReceiver(String result) {
        if (result == null) {
            mProgressDialog.dismiss();
            mCreateInvoiceTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_CREATE_INVOICE)) {

            if (resultList.size() > 2) {
                try {
                    mCreateInvoiceResponse = gson.fromJson(resultList.get(2), CreateInvoiceResponse.class);

                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        ((MakePaymentActivity) getActivity()).switchToInvoicesSentFragment();
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), mCreateInvoiceResponse.getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.failed_invoice_creation, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.failed_invoice_creation, Toast.LENGTH_SHORT).show();
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.failed_invoice_creation, Toast.LENGTH_SHORT).show();

            mProgressDialog.dismiss();
            mCreateInvoiceTask = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == PICK_CONTACT) {

            final CharSequence[] numbers = getNameAndPhoneList(data.getData());
            int size = numbers.length;
            if (size < 1)
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.account_type_business,
                            Toast.LENGTH_LONG).show();
                else if (size == 1) {

                    // Format the number
                    String bdNumberStr = numbers[0].toString();
                    bdNumberStr = ContactEngine.formatMobileNumberBD(bdNumberStr);
                    mMobileNumberEditText.setText(bdNumberStr);

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getString(R.string.pick_a_number));
                    builder.setItems(numbers, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // Format the number
                            String bdNumberStr = numbers[which].toString();
                            bdNumberStr = ContactEngine.formatMobileNumberBD(bdNumberStr);
                            mMobileNumberEditText.setText(bdNumberStr);

                        }
                    });
                    builder.show();
                }
        } else if (resultCode == Activity.RESULT_CANCELED && requestCode == PICK_CONTACT) {
            if (getActivity() != null)
                Toast.makeText(getActivity(), getString(R.string.no_contact_selected),
                        Toast.LENGTH_SHORT).show();
        }
    }

    public CharSequence[] getNameAndPhoneList(Uri data) {
        ArrayList<String> list = new ArrayList<String>();

        Cursor cursor = getActivity().getContentResolver().query(data, null, null,
                null, null);
        if (cursor.moveToFirst()) {
            if (cursor
                    .getString(
                            cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                    .equals("1")) {
                String contactId = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor
                        .getString(cursor
                                .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                Cursor phones = getActivity().getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                + " = " + contactId, null, null);
                int numberIndex = phones
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                while (phones.moveToNext())
                    list.add(phones.getString(numberIndex));

                phones.close();
            }
        }
        cursor.close();

        CharSequence[] numbers = new CharSequence[list.size()];
        for (int i = 0; i < list.size(); i++) {
            numbers[i] = list.get(i);
        }
        return numbers;
    }
}
