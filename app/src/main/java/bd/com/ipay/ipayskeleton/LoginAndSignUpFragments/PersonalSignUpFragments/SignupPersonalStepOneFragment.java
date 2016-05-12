package bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.PersonalSignUpFragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.SignupOrLoginActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.CheckPromoCodeRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.CheckPromoCodeResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class SignupPersonalStepOneFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mCheckPromoCodeTask = null;
    private CheckPromoCodeResponse mCheckPromoCodeResponse;

    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private EditText mMobileNumberView;
    private Button mNextButton;
    private TextView mTermsConditions;
    private TextView mPrivacyPolicy;
    private CheckBox mMaleCheckBox;
    private CheckBox mFemaleCheckBox;
    private CheckBox mAgreementCheckBox;
    private EditText mPromoCodeEditText;

    private String mDeviceID;
    private ProgressDialog mProgressDialog;

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.title_signup_personal_page);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_signup_personal_step_one, container, false);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.progress_dialog_validating_promo_code));

        mPasswordView = (EditText) v.findViewById(R.id.password);
        mConfirmPasswordView = (EditText) v.findViewById(R.id.confirm_password);
        mMobileNumberView = (EditText) v.findViewById(R.id.mobile_number);
        mNextButton = (Button) v.findViewById(R.id.personal_sign_in_button);
        mMaleCheckBox = (CheckBox) v.findViewById(R.id.checkBoxMale);
        mFemaleCheckBox = (CheckBox) v.findViewById(R.id.checkBoxFemale);
        mAgreementCheckBox = (CheckBox) v.findViewById(R.id.checkBoxTermsConditions);
        mTermsConditions = (TextView) v.findViewById(R.id.textViewTermsConditions);
        mPrivacyPolicy = (TextView) v.findViewById(R.id.textViewPrivacyPolicy);
        mPromoCodeEditText = (EditText) v.findViewById(R.id.promo_code_edittext);

        // Enable hyperlinked
        mTermsConditions.setMovementMethod(LinkMovementMethod.getInstance());
        mPrivacyPolicy.setMovementMethod(LinkMovementMethod.getInstance());

        mMaleCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) mFemaleCheckBox.setChecked(false);
            }
        });

        mFemaleCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) mMaleCheckBox.setChecked(false);
            }
        });

        TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        mDeviceID = telephonyManager.getDeviceId();

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
        SignupOrLoginActivity.mPassword = mPasswordView.getText().toString().trim();
        SignupOrLoginActivity.mMobileNumber = "+880" + mMobileNumberView.getText().toString().trim();  // TODO: change Bangladesh
        SignupOrLoginActivity.mAccountType = Constants.PERSONAL_ACCOUNT_TYPE;
        SignupOrLoginActivity.mPromoCode = mPromoCodeEditText.getText().toString().trim();
        if (mMaleCheckBox.isChecked()) SignupOrLoginActivity.mGender = Constants.GENDER_MALE;
        else SignupOrLoginActivity.mGender = Constants.GENDER_FEMALE;

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        String passwordValidationMsg = Utilities.isPasswordValid(SignupOrLoginActivity.mPassword);
        if (passwordValidationMsg.length() > 0) {
            mPasswordView.setError(passwordValidationMsg);
            focusView = mPasswordView;
            cancel = true;
        }

        if (!mConfirmPasswordView.getText().toString().trim().equals(SignupOrLoginActivity.mPassword) && mConfirmPasswordView.getVisibility() == View.VISIBLE) {
            mConfirmPasswordView.setError(getString(R.string.confirm_password_not_matched));
            focusView = mConfirmPasswordView;
            cancel = true;
        }

        if (mMobileNumberView.getText().toString().trim().length() != 10) {
            mMobileNumberView.setError(getString(R.string.error_invalid_mobile_number));
            focusView = mMobileNumberView;
            cancel = true;
        }

        if (mPromoCodeEditText.getText().toString().trim().length() == 0) {
            mPromoCodeEditText.setError(getActivity().getString(R.string.error_promo_code_empty));
            focusView = mPromoCodeEditText;
            cancel = true;
        }

        if (!mAgreementCheckBox.isChecked()) {
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
            CheckPromoCodeRequest mCheckPromoCodeRequest = new CheckPromoCodeRequest(SignupOrLoginActivity.mMobileNumber,
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
    public void httpResponseReceiver(String result) {

        if (result == null) {
            mProgressDialog.dismiss();
            mCheckPromoCodeTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.try_again_later, Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));

        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_CHECK_PROMO_CODE)) {

            String message = "";
            if (resultList.size() > 2) {
                try {
                    mCheckPromoCodeResponse = gson.fromJson(resultList.get(2), CheckPromoCodeResponse.class);
                    message = mCheckPromoCodeResponse.getMessage();
                } catch (Exception e) {
                    e.printStackTrace();
                    message = getString(R.string.server_down);
                }
            } else {
                message = getString(R.string.server_down);
            }

            if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                // Move to step two
                ((SignupOrLoginActivity) getActivity()).switchToSignupPersonalStepTwoFragment();

            } else {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }

            mProgressDialog.dismiss();
            mCheckPromoCodeTask = null;
        }
    }
}

