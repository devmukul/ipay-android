package bd.com.ipay.ipayskeleton.SendMoneyFragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.SendMoneyReviewActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.MMModule.SendMoney.SendMoneyQueryRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.SendMoney.SendMoneyQueryResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.SendMoney.SendMoneyRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.SendMoney.SendMoneyResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.ServiceCharge.GetServiceChargeRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.ServiceCharge.GetServiceChargeResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CircleTransform;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class SendMoneyFragment extends Fragment implements HttpResponseListener {

    private final int PICK_CONTACT = 100;

    private HttpRequestPostAsyncTask mSendMoneyTask = null;
    private SendMoneyResponse mSendMoneyResponse;

    private HttpRequestPostAsyncTask mServiceChargeTask = null;
    private GetServiceChargeResponse mGetServiceChargeResponse;

    private HttpRequestPostAsyncTask mSendMoneyQueryTask = null;
    private SendMoneyQueryResponse mSendMoneyQueryResponse;

    private Button buttonSend;
    private ImageView buttonSelectFromContacts;
    private Button buttonScanQRCode;
    private EditText mMobileNumberEditText;
    private EditText mDescriptionEditText;
    private EditText mAmountEditText;

    private ProgressDialog mProgressDialog;

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

        mProgressDialog = new ProgressDialog(getActivity());

        buttonSelectFromContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
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

        if (Utilities.isConnectionAvailable(getActivity()))
            attemptGetServiceCharge();
        else
            Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();

        return v;
    }

    void initiateScan() {
        IntentIntegrator.forFragment(this).initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == PICK_CONTACT) {

            final CharSequence[] numbers = getNameAndPhoneList(data.getData());
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
        } else if (resultCode == Activity.RESULT_CANCELED && requestCode == PICK_CONTACT) {
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

    private void attemptGetServiceCharge() {
        if (mServiceChargeTask != null) {
            return;
        }

        int accountType = pref.getInt(Constants.ACCOUNT_TYPE, Constants.PERSONAL_ACCOUNT_TYPE);
        int accountClass = Constants.DEFAULT_USER_CLASS;

        mProgressDialog.setMessage(getString(R.string.loading));
        mProgressDialog.show();
        GetServiceChargeRequest mServiceChargeRequest = new GetServiceChargeRequest(Constants.SERVICE_ID_SEND_MONEY, accountType, accountClass);
        Gson gson = new Gson();
        String json = gson.toJson(mServiceChargeRequest);
        mServiceChargeTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_SERVICE_CHARGE,
                Constants.BASE_URL_SM + Constants.URL_SERVICE_CHARGE, json, getActivity());
        mServiceChargeTask.mHttpResponseListener = this;
        mServiceChargeTask.execute((Void) null);
    }

    private void attemptSendMoney() {
        if (mSendMoneyTask != null) {
            return;
        }

        String amount = mAmountEditText.getText().toString().trim();
        String mobileNumber = mMobileNumberEditText.getText().toString().trim();
        String description = mDescriptionEditText.getText().toString().trim();
        String senderMobileNumber = pref.getString(Constants.USERID, "");

        mProgressDialog.setMessage(getString(R.string.progress_dialog_text_sending_money));
        mProgressDialog.show();
        SendMoneyRequest mSendMoneyRequest = new SendMoneyRequest(
                senderMobileNumber, ContactEngine.formatMobileNumberBD(mobileNumber), amount, description);
        Gson gson = new Gson();
        String json = gson.toJson(mSendMoneyRequest);
        mSendMoneyTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SEND_MONEY,
                Constants.BASE_URL_SM + Constants.URL_SEND_MONEY, json, getActivity());
        mSendMoneyTask.mHttpResponseListener = this;
        mSendMoneyTask.execute((Void) null);
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

    private void sendMoneyQuery() {
        if (mSendMoneyQueryTask != null) {
            return;
        }


        if (verifyUserInputs()) {
            String amount = mAmountEditText.getText().toString().trim();
            String mobileNumber = mMobileNumberEditText.getText().toString().trim();
            String description = mDescriptionEditText.getText().toString().trim();
            String senderMobileNumber = pref.getString(Constants.USERID, "");

            mProgressDialog.setMessage(getString(R.string.validating));
            mProgressDialog.show();
            SendMoneyQueryRequest mSendMoneyQueryRequest = new SendMoneyQueryRequest(
                    senderMobileNumber, ContactEngine.formatMobileNumberBD(mobileNumber), amount);
            Gson gson = new Gson();
            String json = gson.toJson(mSendMoneyQueryRequest);
            mSendMoneyQueryTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SEND_MONEY_QUERY,
                    Constants.BASE_URL_SM + Constants.URL_SEND_MONEY_QUERY, json, getActivity());
            mSendMoneyQueryTask.mHttpResponseListener = this;
            mSendMoneyQueryTask.execute((Void) null);
        }
    }

    private void launchReviewPage() {

        String receiver = mMobileNumberEditText.getText().toString().trim();
        BigDecimal amount = new BigDecimal(mAmountEditText.getText().toString().trim());
        String description = mDescriptionEditText.getText().toString().trim();
        BigDecimal serviceCharge = mGetServiceChargeResponse.getServiceCharge(amount);

        if (mGetServiceChargeResponse == null || serviceCharge.compareTo(new BigDecimal(0)) < 0) {
            Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(getActivity(), SendMoneyReviewActivity.class);
        intent.putExtra(Constants.AMOUNT, amount);
        intent.putExtra(Constants.RECEIVER, receiver);
        intent.putExtra(Constants.DESCRIPTION, description);
        startActivity(intent);

//        String serviceChargeDescription = "";
//        if (mGetServiceChargeResponse != null) {
//            if (mGetServiceChargeResponse.getServiceCharge(amount).compareTo(new BigDecimal(0)) > 0) {
//                serviceChargeDescription = "The receiver be charged " + mGetServiceChargeResponse.getServiceCharge(amount) + " Tk. for this transaction.";
//            } else if (mGetServiceChargeResponse.getServiceCharge(amount).compareTo(new BigDecimal(0)) == 0)
//                serviceChargeDescription = getString(R.string.no_extra_charges);
//            else {
//                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//        } else {
//            Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        AlertDialog.Builder alertDialogue = new AlertDialog.Builder(getActivity());
//
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//        View dialogLayout = inflater.inflate(R.layout.dialog_send_money_query, null);
//        alertDialogue.setView(dialogLayout);
//
//        TextView title = (TextView) dialogLayout.findViewById(R.id.title);
//        TextView msg = (TextView) dialogLayout.findViewById(R.id.message);
//        RoundedImageView image = (RoundedImageView) dialogLayout.findViewById(R.id.portrait);
//
//        title.setText(R.string.confirm_query);
//        msg.setText("You're going to send " + amount + " Tk. to " + receiver
//                + "\n" + serviceChargeDescription
//                + "\nDo you want to continue?");
//
//        File file = new File(Environment.getExternalStorageDirectory().getPath()
//                + Constants.PICTURE_FOLDER + receiver.replaceAll("[^0-9]", "") + ".jpg");
//
//        if (file.exists()) {
//            try {
//                Glide.with(getActivity())
//                        .load(file.getPath().toString())
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .skipMemoryCache(true)     // Skip the cache. Load from disk each time
//                        .into(image);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
//            try {
//                Glide.with(getActivity())
//                        .load(R.drawable.ic_person)
//                        .crossFade()
//                        .transform(new CircleTransform(getActivity()))
//                        .into(image);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        alertDialogue.setPositiveButton(R.string.send_money, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                attemptSendMoney();
//            }
//        });
//
//        alertDialogue.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                // Do nothing
//            }
//        });
//
//        alertDialogue.show();


    }

    @Override
    public void httpResponseReceiver(String result) {
        if (result == null) {
            mProgressDialog.show();
            mSendMoneyTask = null;
            mSendMoneyQueryTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.send_money_failed_due_to_server_down, Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_SEND_MONEY)) {

            if (resultList.size() > 2) {

                try {
                    mSendMoneyResponse = gson.fromJson(resultList.get(2), SendMoneyResponse.class);
                    String message = mSendMoneyResponse.getMessage();

                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                        // Return to HomeActivity
                        getActivity().finish();
                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.send_money_failed, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.send_money_failed, Toast.LENGTH_SHORT).show();
            }

            mProgressDialog.dismiss();
            mSendMoneyTask = null;

        } else if (resultList.get(0).equals(Constants.COMMAND_SEND_MONEY_QUERY)) {
            if (resultList.size() > 2) {
                try {
                    mSendMoneyQueryResponse = gson.fromJson(resultList.get(2), SendMoneyQueryResponse.class);
                    String sender = mSendMoneyQueryResponse.getSender();
                    String receiver = mSendMoneyQueryResponse.getReceiver();
                    double amount = mSendMoneyQueryResponse.getAmount();

                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_ACCEPTED)) {
//                        launchReviewPage(receiver, amount);
                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.send_money_query_failed, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.send_money_query_failed, Toast.LENGTH_SHORT).show();

            mProgressDialog.dismiss();
            mSendMoneyQueryTask = null;

        } else if (resultList.get(0).equals(Constants.COMMAND_GET_SERVICE_CHARGE)) {
            if (resultList.size() > 2) {
                try {
                    mGetServiceChargeResponse = gson.fromJson(resultList.get(2), GetServiceChargeResponse.class);

                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        // Do nothing
                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();

            mProgressDialog.dismiss();
            mServiceChargeTask = null;
        }
    }
}
