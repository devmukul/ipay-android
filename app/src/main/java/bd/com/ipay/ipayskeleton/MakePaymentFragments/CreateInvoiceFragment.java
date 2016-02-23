package bd.com.ipay.ipayskeleton.MakePaymentFragments;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.QRCodeViewerActivity;
import bd.com.ipay.ipayskeleton.Activities.RequestMoneyActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.RequestMoneyRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.RequestMoneyResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class CreateInvoiceFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mRequestMoneyTask = null;
    private RequestMoneyResponse mRequestMoneyResponse;

    private final int PICK_CONTACT = 100;

    private Button buttonRequest;
    private Button buttonSelectFromContacts;
    private Button buttonShowQRCode;
    private EditText mMobileNumberEditText;
    private EditText mDescriptionEditText;
    private EditText mAmountEditText;
    private EditText mTitleEditText;
    private ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_request_money, container, false);
        mMobileNumberEditText = (EditText) v.findViewById(R.id.mobile_number);
        buttonShowQRCode = (Button) v.findViewById(R.id.button_show_qr_code);
        buttonSelectFromContacts = (Button) v.findViewById(R.id.select_sender_from_contacts);
        buttonRequest = (Button) v.findViewById(R.id.button_request_money);
        mDescriptionEditText = (EditText) v.findViewById(R.id.description);
        mAmountEditText = (EditText) v.findViewById(R.id.amount);
        mTitleEditText = (EditText) v.findViewById(R.id.title_request);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.submitting_request_money));

        buttonSelectFromContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
            }
        });

        buttonShowQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), QRCodeViewerActivity.class);
                startActivity(intent);
            }
        });

        buttonRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isConnectionAvailable(getActivity())) attemptRequestMoney();
                else if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            }
        });

        return v;
    }

    private void attemptRequestMoney() {
        if (mRequestMoneyTask != null) {
            return;
        }

        boolean cancel = false;
        View focusView = null;

        String receiver = mMobileNumberEditText.getText().toString();
        String title = mTitleEditText.getText().toString();
        String description = mDescriptionEditText.getText().toString();
        String amount = mAmountEditText.getText().toString();

        // Check for a validation
        if (!(amount.length() > 0 && Double.parseDouble(amount) > 0)) {
            mAmountEditText.setError(getString(R.string.please_enter_amount));
            focusView = mAmountEditText;
            cancel = true;
        }

        if (title.length() == 0) {
            mTitleEditText.setError(getString(R.string.please_add_title));
            focusView = mTitleEditText;
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

            mProgressDialog.setMessage(getString(R.string.requesting_money));
            mProgressDialog.show();
            RequestMoneyRequest mRequestMoneyRequest = new RequestMoneyRequest(receiver, Double.parseDouble(amount)
                    , title, description);
            Gson gson = new Gson();
            String json = gson.toJson(mRequestMoneyRequest);
            mRequestMoneyTask = new HttpRequestPostAsyncTask(Constants.COMMAND_REQUEST_MONEY,
                    Constants.BASE_URL_SM + Constants.URL_REQUEST_MONEY, json, getActivity());
            mRequestMoneyTask.mHttpResponseListener = this;
            mRequestMoneyTask.execute((Void) null);
        }

    }

    @Override
    public void httpResponseReceiver(String result) {
        if (result == null) {
            mProgressDialog.dismiss();
            mRequestMoneyTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_REQUEST_MONEY)) {

            if (resultList.size() > 2) {
                try {
                    mRequestMoneyResponse = gson.fromJson(resultList.get(2), RequestMoneyResponse.class);

                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        ((RequestMoneyActivity) getActivity()).switchToRequestsFragment();
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), mRequestMoneyResponse.getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.failed_request_money, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.failed_request_money, Toast.LENGTH_SHORT).show();
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.failed_request_money, Toast.LENGTH_SHORT).show();

            mProgressDialog.dismiss();
            mRequestMoneyTask = null;
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
                    mMobileNumberEditText.setText(numbers[0].toString().replaceAll("\\D", ""));
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getString(R.string.pick_a_number));
                    builder.setItems(numbers, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mMobileNumberEditText.setText(numbers[which]);
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

                // TODO: Can use name and contact ID
//                list.add(name);
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
