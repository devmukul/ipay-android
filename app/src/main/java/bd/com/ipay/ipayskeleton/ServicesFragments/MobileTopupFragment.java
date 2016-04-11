package bd.com.ipay.ipayskeleton.ServicesFragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.HomeActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.MMModule.ServiceCharge.GetServiceChargeRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.ServiceCharge.GetServiceChargeResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.TopUp.TopupRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.TopUp.TopupResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class MobileTopupFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mTopupTask = null;
    private TopupResponse mTopupResponse;

    private HttpRequestPostAsyncTask mServiceChargeTask = null;
    private GetServiceChargeResponse mGetServiceChargeResponse;

    private EditText mMobileNumberEditText;
    private EditText mAmountEditText;
    private Spinner mSelectOperator;
    private Button mRechargeButton;

    private RadioGroup mSelectType;
    private RadioButton mPrepaidRadioButton;
    private RadioButton mPostPaidRadioButton;

    private ProgressDialog mProgressDialog;
    private SharedPreferences pref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_mobile_topup, container, false);
        pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);

        mMobileNumberEditText = (EditText) v.findViewById(R.id.mobile_number);
        mAmountEditText = (EditText) v.findViewById(R.id.amount);
        mSelectOperator = (Spinner) v.findViewById(R.id.operator_list_spinner);
        mRechargeButton = (Button) v.findViewById(R.id.button_recharge);

        mSelectType = (RadioGroup) v.findViewById(R.id.mobile_number_type_selector);
        mPrepaidRadioButton = (RadioButton) v.findViewById(R.id.radio_button_prepaid);
        mPostPaidRadioButton = (RadioButton) v.findViewById(R.id.radio_button_postpaid);

        int mobileNumberType = pref.getInt(Constants.MOBILE_NUMBER_TYPE, Constants.MOBILE_TYPE_PREPAID);
        if (mobileNumberType == Constants.MOBILE_TYPE_PREPAID) {
            mPrepaidRadioButton.setChecked(true);
        } else {
            mPostPaidRadioButton.setChecked(true);
        }

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.recharging_balance));

        ArrayAdapter<CharSequence> mAdapterMobileOperators = ArrayAdapter.createFromResource(getActivity(),
                R.array.mobile_operators, android.R.layout.simple_dropdown_item_1line);
        mSelectOperator.setAdapter(mAdapterMobileOperators);

        mRechargeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialogue();
            }
        });

        String userMobileNumber = pref.getString(Constants.USERID, "");
        setPhoneNumber(userMobileNumber);

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
        GetServiceChargeRequest mServiceChargeRequest = new GetServiceChargeRequest(Constants.SERVICE_ID_TOP_UP, accountType, accountClass);
        Gson gson = new Gson();
        String json = gson.toJson(mServiceChargeRequest);
        mServiceChargeTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_SERVICE_CHARGE,
                Constants.BASE_URL_SM + Constants.URL_SERVICE_CHARGE, json, getActivity());
        mServiceChargeTask.mHttpResponseListener = this;
        mServiceChargeTask.execute((Void) null);
    }

    private void setPhoneNumber(String phoneNumber) {
        phoneNumber = ContactEngine.trimPrefix(phoneNumber);
        mMobileNumberEditText.setText(phoneNumber);

        mMobileNumberEditText.setEnabled(false);
        mMobileNumberEditText.setFocusable(false);

        final String[] OPERATOR_PREFIXES = {"17", "18", "16", "19", "15"};
        for (int i = 0; i < OPERATOR_PREFIXES.length; i++) {
            if (phoneNumber.startsWith(OPERATOR_PREFIXES[i])) {
                mSelectOperator.setSelection(i);
                break;
            }
        }
        mSelectOperator.setEnabled(false);
    }

    private void attemptTopUp() {
        if (mTopupTask != null) {
            return;
        }

        boolean cancel = false;
        View focusView = null;

//        if (mMobileNumberEditText.getText().toString().trim().length() != 10) {
//            mMobileNumberEditText.setError(getString(R.string.error_invalid_mobile_number));
//            focusView = mMobileNumberEditText;
//            cancel = true;
//        }

        if (mAmountEditText.getText().toString().trim().length() == 0) {
            mAmountEditText.setError(getString(R.string.please_enter_amount));
            focusView = mAmountEditText;
            cancel = true;
        }

        long senderAccountID = Long.parseLong(pref.getString(Constants.USERID, "").replaceAll("[^0-9]", ""));
        String receiverMobileNumber = "+880" + mMobileNumberEditText.getText().toString().trim();

        int mobileNumberType;
        if (mSelectType.getCheckedRadioButtonId() == R.id.radio_button_prepaid)
            mobileNumberType = Constants.MOBILE_TYPE_PREPAID;
        else
            mobileNumberType = Constants.MOBILE_TYPE_POSTPAID;
        pref.edit().putInt(Constants.MOBILE_NUMBER_TYPE, mobileNumberType).apply();

        int operatorCode = mSelectOperator.getSelectedItemPosition() + 1;
        double amount = Double.parseDouble(mAmountEditText.getText().toString().trim());
        int accountType = pref.getInt(Constants.ACCOUNT_TYPE, 1);
        String countryCode = "+88"; // TODO: For now Bangladesh Only

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            mProgressDialog.show();
            // TODO: token theke userclass and userType ashbe
            TopupRequest mTopupRequestModel = new TopupRequest(senderAccountID, receiverMobileNumber,
                    mobileNumberType, operatorCode, amount, countryCode, accountType, Constants.DEFAULT_USER_CLASS);
            Gson gson = new Gson();
            String json = gson.toJson(mTopupRequestModel);
            mTopupTask = new HttpRequestPostAsyncTask(Constants.COMMAND_TOPUP_REQUEST,
                    Constants.BASE_URL_SM + Constants.URL_TOPUP_REQUEST, json, getActivity());
            mTopupTask.mHttpResponseListener = this;
            mTopupTask.execute((Void) null);
        }
    }

    private void showAlertDialogue() {

        boolean cancel = false;
        View focusView = null;

        if (mAmountEditText.getText().toString().trim().length() == 0) {
            mAmountEditText.setError(getString(R.string.please_enter_amount));
            focusView = mAmountEditText;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            double amount = Double.parseDouble(mAmountEditText.getText().toString().trim());
            String serviceChargeDescription = "";

            if (mGetServiceChargeResponse != null) {
                if (mGetServiceChargeResponse.getServiceCharge(new BigDecimal(amount)).compareTo(new BigDecimal(0)) > 0)
                    serviceChargeDescription = "You'll be charged " + mGetServiceChargeResponse.getServiceCharge(new BigDecimal(amount)) + " Tk. from your iPay account for this recharge.";
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
            alertDialogue.setTitle(R.string.confirm_add_money);
            alertDialogue.setMessage("You're going to recharge your mobile with " + amount + " Tk. from your iPay account."
                    + "\n" + serviceChargeDescription
                    + "\nDo you want to continue?");

            alertDialogue.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    attemptTopUp();
                }
            });

            alertDialogue.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing
                }
            });

            alertDialogue.show();
        }
    }

    @Override
    public void httpResponseReceiver(String result) {

        if (result == null) {
            mProgressDialog.dismiss();
            mTopupTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.recharge_failed, Toast.LENGTH_LONG).show();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_TOPUP_REQUEST)) {

            if (resultList.size() > 2) {
                try {
                    mTopupResponse = gson.fromJson(resultList.get(2), TopupResponse.class);

                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_PROCESSING)) {
                        // TODO: Save transaction in database
                        getActivity().finish();
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.progress_dialog_processing, Toast.LENGTH_LONG).show();
                    } else if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        // TODO: Save transaction in database
                        getActivity().finish();
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.progress_dialog_processing, Toast.LENGTH_LONG).show();
                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.recharge_failed, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.recharge_failed, Toast.LENGTH_LONG).show();
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.recharge_failed, Toast.LENGTH_LONG).show();

            mProgressDialog.dismiss();
            mTopupTask = null;

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
