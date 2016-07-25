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
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class CreateInvoiceFragmentStepOne extends Fragment {

    private static final int REQUEST_PICK_CONTACT = 100;

    private Button buttonCreateInvoice;
    private ImageView buttonSelectFromContacts;
    private EditText mMobileNumberEditText;
    private EditText mNameEditText;
    private EditText mDescriptionEditText;
    private EditText mQuantityEditText;
    private ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_create_invoice_step_one, container, false);
        mMobileNumberEditText = (EditText) v.findViewById(R.id.mobile_number);
        buttonSelectFromContacts = (ImageView) v.findViewById(R.id.select_receiver_from_contacts);
        buttonCreateInvoice = (Button) v.findViewById(R.id.button_request_money);
        mNameEditText = (EditText) v.findViewById(R.id.item_name);
        mDescriptionEditText = (EditText) v.findViewById(R.id.description);
        mQuantityEditText = (EditText) v.findViewById(R.id.quantity);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.submitting_request_money));

        buttonSelectFromContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, REQUEST_PICK_CONTACT);
            }
        });

        buttonCreateInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    if (verifyUserInputs()) {
                        goToNextPage();
                    }
                } else if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            }
        });

        return v;
    }

    private boolean verifyUserInputs() {
        boolean cancel = false;
        View focusView = null;

        String receiver = mMobileNumberEditText.getText().toString();
        String name = mNameEditText.getText().toString();
        String description = mDescriptionEditText.getText().toString();
        String quantity = mQuantityEditText.getText().toString();

        // Check for a validation
        if (!(quantity.length() > 0 && Double.parseDouble(quantity) > 0)) {
            mQuantityEditText.setError(getString(R.string.please_enter_amount));
            focusView = mQuantityEditText;
            cancel = true;
        }

        if (name.length() == 0) {
            mNameEditText.setError(getString(R.string.please_add_item_name));
            focusView = mNameEditText;
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
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void goToNextPage() {
        String receiver = mMobileNumberEditText.getText().toString();
        String item_name = mNameEditText.getText().toString();
        String description = mDescriptionEditText.getText().toString();
        String quantity = mQuantityEditText.getText().toString();

        Bundle bundle = new Bundle();
        bundle.putString(Constants.INVOICE_RECEIVER_TAG, receiver);
        bundle.putString(Constants.INVOICE_ITEM_NAME_TAG, item_name);
        bundle.putString(Constants.INVOICE_DESCRIPTION_TAG, description);
        bundle.putString(Constants.INVOICE_QUANTITY_TAG, quantity);

        CreateInvoiceFragmentStepTwo frag = new CreateInvoiceFragmentStepTwo();
        frag.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, frag).commit();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_PICK_CONTACT) {

            final CharSequence[] numbers = getNameAndPhoneList(data.getData());
            int size = numbers.length;
            if (size < 1) {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.no_numbers_found,
                            Toast.LENGTH_LONG).show();
            } else if (size == 1) {

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
        } else if (resultCode == Activity.RESULT_CANCELED && requestCode == REQUEST_PICK_CONTACT) {
            if (getActivity() != null)
                Toast.makeText(getActivity(), getString(R.string.no_contact_selected),
                        Toast.LENGTH_SHORT).show();
        }
    }

    private CharSequence[] getNameAndPhoneList(Uri data) {
        ArrayList<String> list = new ArrayList<>();

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
