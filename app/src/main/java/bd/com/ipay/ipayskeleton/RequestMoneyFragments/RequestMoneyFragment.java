package bd.com.ipay.ipayskeleton.RequestMoneyFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.QRCodeViewerActivity;
import bd.com.ipay.ipayskeleton.Activities.RequestMoneyActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.RequestMoneyRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.RequestMoneyResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.ServiceCharge.GetServiceChargeRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.ServiceCharge.GetServiceChargeResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CircleTransform;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class RequestMoneyFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mRequestMoneyTask = null;
    private RequestMoneyResponse mRequestMoneyResponse;

    private HttpRequestPostAsyncTask mServiceChargeTask = null;
    private GetServiceChargeResponse mGetServiceChargeResponse;

    private final int PICK_CONTACT = 100;

    private Button buttonRequest;
    private ImageView buttonSelectFromContacts;
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
        buttonSelectFromContacts = (ImageView) v.findViewById(R.id.select_sender_from_contacts);
        buttonRequest = (Button) v.findViewById(R.id.button_request_money);
        mDescriptionEditText = (EditText) v.findViewById(R.id.description);
        mAmountEditText = (EditText) v.findViewById(R.id.amount);

        if (getActivity().getIntent().hasExtra(Constants.MOBILE_NUMBER)) {
            mMobileNumberEditText.setText(getActivity().getIntent().getStringExtra(Constants.MOBILE_NUMBER));
        }

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
                if (Utilities.isConnectionAvailable(getActivity()) && verifyUserInputs())
                    showRequestMoneyConfirmationDialog();
                else if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            }
        });

        if (Utilities.isConnectionAvailable(getActivity()))
            attemptGetServiceCharge();
        else
            Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();

        return v;
    }

    private void attemptGetServiceCharge() {
        if (mServiceChargeTask != null) {
            return;
        }

        SharedPreferences pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Context.MODE_PRIVATE);
        int accountType = pref.getInt(Constants.ACCOUNT_TYPE, Constants.PERSONAL_ACCOUNT_TYPE);
        int accountClass = Constants.DEFAULT_USER_CLASS;

        mProgressDialog.setMessage(getString(R.string.loading));
        mProgressDialog.show();
        GetServiceChargeRequest mServiceChargeRequest = new GetServiceChargeRequest(Constants.SERVICE_ID_SEND_MONEY, accountType, accountClass);
        Gson gson = new Gson();
        String json = gson.toJson(mServiceChargeRequest);
        mServiceChargeTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_SERVICE_CHARGE,
                Constants.BASE_URL + Constants.URL_SERVICE_CHARGE, json, getActivity());
        mServiceChargeTask.mHttpResponseListener = this;
        mServiceChargeTask.execute((Void) null);
    }

    private void showRequestMoneyConfirmationDialog() {
        String receiver = mMobileNumberEditText.getText().toString().trim();
        String amount = mAmountEditText.getText().toString().trim();
        String serviceChargeDescription = "";

        if (mGetServiceChargeResponse != null) {
            if (mGetServiceChargeResponse.getServiceCharge(new BigDecimal(amount)).compareTo(new BigDecimal(0)) > 0)
                serviceChargeDescription = "You'll be charged " + mGetServiceChargeResponse.getServiceCharge(new BigDecimal(amount)) + " Tk. for this request, after receiving the money.";
            else if (mGetServiceChargeResponse.getServiceCharge(new BigDecimal(amount)).compareTo(new BigDecimal(0)) == 0)
                serviceChargeDescription = getString(R.string.no_extra_charges);
            else {
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
                return;
            }

        } else {
            Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder alertDialogue = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_request_money_query, null);
        alertDialogue.setView(dialogLayout);

        TextView title = (TextView) dialogLayout.findViewById(R.id.title);
        TextView msg = (TextView) dialogLayout.findViewById(R.id.message);
        RoundedImageView image = (RoundedImageView) dialogLayout.findViewById(R.id.portrait);

        title.setText(R.string.confirm_query);
        msg.setText("You're going to send a request for " + amount + " Tk. to " + receiver
                + "\n" + serviceChargeDescription
                + "\nDo you want to continue?");

        File file = new File(Environment.getExternalStorageDirectory().getPath()
                + Constants.PICTURE_FOLDER + receiver.replaceAll("[^0-9]", "") + ".jpg");

        if (file.exists()) {
            try {
                Glide.with(getActivity())
                        .load(file.getPath().toString())
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)     // Skip the cache. Load from disk each time
                        .into(image);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                Glide.with(getActivity())
                        .load(R.drawable.ic_person)
                        .crossFade()
                        .transform(new CircleTransform(getActivity()))
                        .into(image);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        alertDialogue.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                attemptRequestMoney();
            }
        });

        alertDialogue.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });

        alertDialogue.show();
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

            // Format the number
            receiver = ContactEngine.formatMobileNumberBD(receiver);

            mProgressDialog.setMessage(getString(R.string.requesting_money));
            mProgressDialog.show();
            RequestMoneyRequest mRequestMoneyRequest = new RequestMoneyRequest(receiver, Double.parseDouble(amount)
                    , title, description);
            Gson gson = new Gson();
            String json = gson.toJson(mRequestMoneyRequest);
            mRequestMoneyTask = new HttpRequestPostAsyncTask(Constants.COMMAND_REQUEST_MONEY,
                    Constants.BASE_URL + Constants.URL_REQUEST_MONEY, json, getActivity());
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
                        // If new request page was directly launched, then no need to go back to the
                        // request fragment, just finish the activity
                        if (getActivity().getIntent().getBooleanExtra(RequestMoneyActivity.LAUNCH_NEW_REQUEST, false)) {
                            getActivity().finish();
                        } else {
                            ((RequestMoneyActivity) getActivity()).switchToMyRequestsFragment();
                        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == PICK_CONTACT) {

            final CharSequence[] numbers = getNameAndPhoneList(data.getData());
            int size = numbers.length;
            if (size < 1) {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.no_contact_selected,
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
