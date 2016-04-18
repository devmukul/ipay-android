package bd.com.ipay.ipayskeleton.PaymentFragments.ServicesFragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
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

import bd.com.ipay.ipayskeleton.Activities.MobileTopUpReviewActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.MMModule.ServiceCharge.GetServiceChargeRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.ServiceCharge.GetServiceChargeResponse;
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

    private static final int MOBILE_TOPUP_REVIEW_REQUEST = 100;

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
                Constants.BASE_URL + Constants.URL_SERVICE_CHARGE, json, getActivity());
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

    private boolean verifyUserInputs() {
        boolean cancel = false;
        View focusView = null;

        if (mAmountEditText.getText().toString().trim().length() == 0) {
            mAmountEditText.setError(getString(R.string.please_enter_amount));
            focusView = mAmountEditText;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == MOBILE_TOPUP_REVIEW_REQUEST) {
            if (getActivity() != null)
                getActivity().finish();
        }
    }

    private void launchReviewPage() {

        double amount = Double.parseDouble(mAmountEditText.getText().toString().trim());
        BigDecimal serviceCharge = mGetServiceChargeResponse.getServiceCharge(new BigDecimal(amount));

        if (mGetServiceChargeResponse == null || serviceCharge.compareTo(BigDecimal.ZERO) < 0) {
            Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }

        int mobileNumberType;
        if (mSelectType.getCheckedRadioButtonId() == R.id.radio_button_prepaid)
            mobileNumberType = Constants.MOBILE_TYPE_PREPAID;
        else
            mobileNumberType = Constants.MOBILE_TYPE_POSTPAID;
        pref.edit().putInt(Constants.MOBILE_NUMBER_TYPE, mobileNumberType).apply();

        int operatorCode = mSelectOperator.getSelectedItemPosition() + 1;
        String countryCode = "+88"; // TODO: For now Bangladesh Only

        Intent intent = new Intent(getActivity(), MobileTopUpReviewActivity.class);
        intent.putExtra(Constants.AMOUNT, amount);
        intent.putExtra(Constants.SERVICE_CHARGE, serviceCharge);
        intent.putExtra(Constants.MOBILE_NUMBER_TYPE, mobileNumberType);
        intent.putExtra(Constants.OPERATOR_CODE, operatorCode);
        intent.putExtra(Constants.COUNTRY_CODE, countryCode);

        startActivityForResult(intent, MOBILE_TOPUP_REVIEW_REQUEST);
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

        if (resultList.get(0).equals(Constants.COMMAND_GET_SERVICE_CHARGE)) {
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
