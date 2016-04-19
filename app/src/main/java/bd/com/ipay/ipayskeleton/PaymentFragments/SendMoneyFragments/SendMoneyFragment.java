package bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.math.BigDecimal;
import java.util.ArrayList;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.SendMoneyReviewActivity;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class SendMoneyFragment extends Fragment {

    private final int PICK_CONTACT_REQUEST = 100;
    private final int SEND_MONEY_REVIEW_REQUEST = 101;

//    private HttpRequestPostAsyncTask mSendMoneyQueryTask = null;
//    private SendMoneyQueryResponse mSendMoneyQueryResponse;

    private Button buttonSend;
    private ImageView buttonSelectFromContacts;
    private Button buttonScanQRCode;
    private EditText mMobileNumberEditText;
    private EditText mDescriptionEditText;
    private EditText mAmountEditText;

    private SharedPreferences pref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_send_money, container, false);
        mMobileNumberEditText = (EditText) v.findViewById(R.id.mobile_number);
        mDescriptionEditText = (EditText) v.findViewById(R.id.description);
        mAmountEditText = (EditText) v.findViewById(R.id.amount);
        buttonScanQRCode = (Button) v.findViewById(R.id.button_scan_qr_code);
        buttonSelectFromContacts = (ImageView) v.findViewById(R.id.select_receiver_from_contacts);
        buttonSend = (Button) v.findViewById(R.id.button_send_money);

        if (getActivity().getIntent().hasExtra(Constants.MOBILE_NUMBER)) {
            mMobileNumberEditText.setText(getActivity().getIntent().getStringExtra(Constants.MOBILE_NUMBER));
        }

        buttonSelectFromContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT_REQUEST);
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    // For now, we are directly sending the money without going through any send money query
                    // sendMoneyQuery();
                    if (verifyUserInputs()) {
                        launchReviewPage();
                    }
                } else if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            }
        });

        buttonScanQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateScan();
            }
        });

        pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);

        return v;
    }

    void initiateScan() {
        IntentIntegrator.forFragment(this).initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == PICK_CONTACT_REQUEST) {

            final String[] numbers = getNameAndPhoneList(data.getData());
            int size = numbers.length;
            if (size < 1) {
                Toast.makeText(getActivity(), R.string.no_numbers_found, Toast.LENGTH_LONG).show();
            } else if (size == 1) {
                // Format the number selected
                String bdNumberStr = numbers[0].toString();
                bdNumberStr = ContactEngine.formatMobileNumberBD(bdNumberStr);
                mMobileNumberEditText.setText(bdNumberStr);

            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.pick_a_number));
                builder.setItems(numbers, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Format the number selected
                        String bdNumberStr = numbers[which].toString();
                        bdNumberStr = ContactEngine.formatMobileNumberBD(bdNumberStr);
                        mMobileNumberEditText.setText(bdNumberStr);
                    }
                });
                builder.show();
            }
        } else if (resultCode == Activity.RESULT_CANCELED && requestCode == PICK_CONTACT_REQUEST) {
            if (getActivity() != null)
                Toast.makeText(getActivity(), getString(R.string.no_contact_selected),
                        Toast.LENGTH_SHORT).show();
        } else if (resultCode == Activity.RESULT_OK && requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(
                    requestCode, resultCode, data);
            if (scanResult == null) {
                return;
            }
            final String result = scanResult.getContents();
            if (result != null) {
                Handler mHandler = new Handler();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (result.matches("[0-9]+") && result.length() > 2) {
                            mMobileNumberEditText.setText("+" + result);
                        } else if (getActivity() != null)
                            Toast.makeText(getActivity(), getResources().getString(
                                    R.string.please_scan_a_valid_pin), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        } else if (resultCode == Activity.RESULT_CANCELED && requestCode == IntentIntegrator.REQUEST_CODE) {
            if (getActivity() != null)
                Toast.makeText(getActivity(), getString(R.string.scan_cancelled),
                        Toast.LENGTH_SHORT).show();
        } else if (resultCode == Activity.RESULT_OK && requestCode == SEND_MONEY_REVIEW_REQUEST) {
            if (getActivity() != null)
                getActivity().finish();
        }
    }

    public String[] getNameAndPhoneList(Uri data) {
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

        String[] numbers = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            numbers[i] = list.get(i);
        }
        return numbers;
    }

    private boolean verifyUserInputs() {
        mAmountEditText.setError(null);
        mMobileNumberEditText.setError(null);

        boolean cancel = false;
        View focusView = null;

        String mobileNumber = mMobileNumberEditText.getText().toString().trim();

        if (!(mAmountEditText.getText().toString().trim().length() > 0)) {
            focusView = mAmountEditText;
            mAmountEditText.setError(getString(R.string.please_enter_amount));
            cancel = true;
        }
        if (!ContactEngine.isValidNumber(mobileNumber)) {
            focusView = mMobileNumberEditText;
            mMobileNumberEditText.setError(getString(R.string.please_enter_valid_mobile_number));
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

//    private void sendMoneyQuery() {
//        if (mSendMoneyQueryTask != null) {
//            return;
//        }
//
//
//        if (verifyUserInputs()) {
//            String amount = mAmountEditText.getText().toString().trim();
//            String mobileNumber = mMobileNumberEditText.getText().toString().trim();
//            String description = mDescriptionEditText.getText().toString().trim();
//            String senderMobileNumber = pref.getString(Constants.USERID, "");
//
//            mProgressDialog.setMessage(getString(R.string.validating));
//            mProgressDialog.show();
//            SendMoneyQueryRequest mSendMoneyQueryRequest = new SendMoneyQueryRequest(
//                    senderMobileNumber, ContactEngine.formatMobileNumberBD(mobileNumber), amount);
//            Gson gson = new Gson();
//            String json = gson.toJson(mSendMoneyQueryRequest);
//            mSendMoneyQueryTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SEND_MONEY_QUERY,
//                    Constants.BASE_URL + Constants.URL_SEND_MONEY_QUERY, json, getActivity());
//            mSendMoneyQueryTask.mHttpResponseListener = this;
//            mSendMoneyQueryTask.execute((Void) null);
//        }
//    }

    private void launchReviewPage() {

        String receiver = mMobileNumberEditText.getText().toString().trim();
        BigDecimal amount = new BigDecimal(mAmountEditText.getText().toString().trim());
        String description = mDescriptionEditText.getText().toString().trim();

        Intent intent = new Intent(getActivity(), SendMoneyReviewActivity.class);
        intent.putExtra(Constants.AMOUNT, amount);
        intent.putExtra(Constants.RECEIVER, ContactEngine.formatMobileNumberBD(receiver));
        intent.putExtra(Constants.DESCRIPTION, description);

        startActivityForResult(intent, SEND_MONEY_REVIEW_REQUEST);

    }
}
