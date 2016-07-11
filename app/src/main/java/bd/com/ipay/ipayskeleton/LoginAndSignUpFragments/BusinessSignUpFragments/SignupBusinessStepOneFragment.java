package bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.BusinessSignUpFragments;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;


import bd.com.ipay.ipayskeleton.Activities.SignupOrLoginActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.CheckPromoCodeRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.CheckPromoCodeResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.DeviceIdFactory;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;


public class SignupBusinessStepOneFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mCheckPromoCodeTask = null;
    private CheckPromoCodeResponse mCheckPromoCodeResponse;

    private EditText mBusinessEmailView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private EditText mBusinessMobileNumberView;
    private Button mNextButton;
    private TextView mTermsConditions;
    private TextView mPrivacyPolicy;
    private CheckBox mAgreementCheckBox;
    private EditText mPromoCodeEditText;

    private String mDeviceID;
    private ProgressDialog mProgressDialog;

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.title_signup_business_page);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_signup_business_step_one, container, false);

        mProgressDialog = new ProgressDialog(getActivity());

        mPasswordView = (EditText) v.findViewById(R.id.password);
        mConfirmPasswordView = (EditText) v.findViewById(R.id.confirm_password);
        mBusinessEmailView = (EditText) v.findViewById(R.id.email);
        mBusinessMobileNumberView = (EditText) v.findViewById(R.id.business_mobile_number);
        mPromoCodeEditText = (EditText) v.findViewById(R.id.promo_code_edittext);
        mTermsConditions = (TextView) v.findViewById(R.id.textViewTermsConditions);
        mPrivacyPolicy = (TextView) v.findViewById(R.id.textViewPrivacyPolicy);
        mAgreementCheckBox = (CheckBox) v.findViewById(R.id.checkBoxTermsConditions);

        mNextButton = (Button) v.findViewById(R.id.business_next_button);


        mDeviceID = DeviceIdFactory.getDeviceId(getActivity());

        // Enable hyperlinked
        mTermsConditions.setMovementMethod(LinkMovementMethod.getInstance());
        mPrivacyPolicy.setMovementMethod(LinkMovementMethod.getInstance());


        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isConnectionAvailable(getActivity())) attemptCheckPromoCode();
                else if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            }
        });

        return v;
    }

    private void attemptCheckPromoCode() {
        if (mCheckPromoCodeTask != null) {
            return;
        }

        // Reset errors.
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        SignupOrLoginActivity.mPasswordBusiness = mPasswordView.getText().toString().trim();
        SignupOrLoginActivity.mEmailBusiness = mBusinessEmailView.getText().toString().trim();
        SignupOrLoginActivity.mMobileNumberBusiness = ContactEngine.formatMobileNumberBD(
                mBusinessMobileNumberView.getText().toString().trim());  // TODO: change Bangladesh
        SignupOrLoginActivity.mAccountType = Constants.BUSINESS_ACCOUNT_TYPE;
        SignupOrLoginActivity.mPromoCode = mPromoCodeEditText.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        String passwordValidationMsg = InputValidator.isPasswordValid(SignupOrLoginActivity.mPasswordBusiness);
        if (passwordValidationMsg.length() > 0) {
            mPasswordView.setError(passwordValidationMsg);
            focusView = mPasswordView;
            cancel = true;

        } else if (!mConfirmPasswordView.getText().toString().trim().equals(SignupOrLoginActivity.mPasswordBusiness) && mConfirmPasswordView.getVisibility() == View.VISIBLE) {
            mConfirmPasswordView.setError(getString(R.string.confirm_password_not_matched));
            focusView = mConfirmPasswordView;
            cancel = true;

        } else if (!ContactEngine.isValidNumber(SignupOrLoginActivity.mMobileNumberBusiness)) {
            mBusinessMobileNumberView.setError(getString(R.string.error_invalid_mobile_number));
            focusView = mBusinessMobileNumberView;
            cancel = true;

        } else if (!InputValidator.isValidEmail(SignupOrLoginActivity.mEmailBusiness)) {
            mBusinessEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mBusinessEmailView;
            cancel = true;

        } else if (mPromoCodeEditText.getText().toString().trim().length() == 0) {
            mPromoCodeEditText.setError(getActivity().getString(R.string.error_promo_code_empty));
            focusView = mPromoCodeEditText;
            cancel = true;
        } else if (!mAgreementCheckBox.isChecked()) {
            cancel = true;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.please_check_terms_and_conditions, Toast.LENGTH_LONG).show();

        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            if (focusView != null) focusView.requestFocus();
        } else {


            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mProgressDialog.show();
            CheckPromoCodeRequest mCheckPromoCodeRequest = new CheckPromoCodeRequest(SignupOrLoginActivity.mMobileNumberBusiness,
                    Constants.MOBILE_ANDROID + mDeviceID, SignupOrLoginActivity.mPromoCode, null);
            Gson gson = new Gson();
            String json = gson.toJson(mCheckPromoCodeRequest);
            mCheckPromoCodeTask = new HttpRequestPostAsyncTask(Constants.COMMAND_CHECK_PROMO_CODE,
                    Constants.BASE_URL_MM + Constants.URL_CHECK_PROMO_CODE, json, getActivity());
            mCheckPromoCodeTask.mHttpResponseListener = this;
            mCheckPromoCodeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
					|| result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mCheckPromoCodeTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.otp_request_failed, Toast.LENGTH_SHORT).show();
            return;
        }


        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_CHECK_PROMO_CODE)) {

            String message = "";
            try {
                mCheckPromoCodeResponse = gson.fromJson(result.getJsonString(), CheckPromoCodeResponse.class);
                message = mCheckPromoCodeResponse.getMessage();
            } catch (Exception e) {
                e.printStackTrace();
                message = getString(R.string.server_down);
            }


            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                // Move to step two
                ((SignupOrLoginActivity) getActivity()).switchToBusinessStepTwoFragment();

            } else {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }

            mProgressDialog.dismiss();
            mCheckPromoCodeTask = null;
        }
    }
}




