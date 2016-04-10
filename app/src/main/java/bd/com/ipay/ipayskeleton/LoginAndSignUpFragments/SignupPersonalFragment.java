package bd.com.ipay.ipayskeleton.LoginAndSignUpFragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.SignupOrLoginActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.OTPRequestPersonalSignup;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.OTPResponsePersonalSignup;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class SignupPersonalFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mRequestOTPTask = null;
    private OTPResponsePersonalSignup mOtpResponsePersonalSignup;

    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private EditText mNameView;
    private EditText mMobileNumberView;
    private ImageView mDatePickerButton;
    private Button mSignupPersonalButton;
    private CheckBox mAgreementCheckBox;
    private CheckBox mMaleCheckBox;
    private CheckBox mFemaleCheckBox;
    private TextView mTermsCondtions;
    private EditText mBirthdayEditText;
    private EditText mPromoCodeEditText;

    private int mYear;
    private int mMonth;
    private int mDay;

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
        View v = inflater.inflate(R.layout.fragment_signup_personal, container, false);
        mDatePickerButton = (ImageView) v.findViewById(R.id.myDatePickerButton);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.progress_dialog_text_sending_sms));

        mPasswordView = (EditText) v.findViewById(R.id.password);
        mConfirmPasswordView = (EditText) v.findViewById(R.id.confirm_password);
        mNameView = (EditText) v.findViewById(R.id.user_name);
        mMobileNumberView = (EditText) v.findViewById(R.id.mobile_number);
        mSignupPersonalButton = (Button) v.findViewById(R.id.personal_sign_in_button);
        mAgreementCheckBox = (CheckBox) v.findViewById(R.id.checkBoxTermsConditions);
        mMaleCheckBox = (CheckBox) v.findViewById(R.id.checkBoxMale);
        mFemaleCheckBox = (CheckBox) v.findViewById(R.id.checkBoxFemale);
        mTermsCondtions = (TextView) v.findViewById(R.id.textViewTermsConditions);
        mBirthdayEditText = (EditText) v.findViewById(R.id.birthdayEditText);
        mPromoCodeEditText = (EditText) v.findViewById(R.id.promo_code_edittext);

        TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        mDeviceID = telephonyManager.getDeviceId();

        // Enable hyperlinked
        mTermsCondtions.setMovementMethod(LinkMovementMethod.getInstance());

        final DatePickerDialog dialog = new DatePickerDialog(
                getActivity(), mDateSetListener, 1990, 0, 1);
        mDatePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
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

        mSignupPersonalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isConnectionAvailable(getActivity())) attemptRequestOTP();
                else if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            }
        });

//        if (SignupOrLoginActivity.mName != null) {
//            rePopulateData();
//        }

        return v;
    }

    private void rePopulateData() {
        mNameView.setText(SignupOrLoginActivity.mName);
        mMobileNumberView.setText(SignupOrLoginActivity.mMobileNumber);
        mPasswordView.setText(SignupOrLoginActivity.mPassword);
        mConfirmPasswordView.setText(SignupOrLoginActivity.mPassword);
        if (SignupOrLoginActivity.mGender == Constants.GENDER_FEMALE)
            mFemaleCheckBox.setChecked(true);
        else mMaleCheckBox.setChecked(true);
        mDatePickerButton.setVisibility(View.GONE);
    }

    private void attemptRequestOTP() {
        if (mRequestOTPTask != null) {
            return;
        }

        // Reset errors.
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        SignupOrLoginActivity.mPassword = mPasswordView.getText().toString().trim();
        String name = mNameView.getText().toString().trim();

        SignupOrLoginActivity.mName = name;
        SignupOrLoginActivity.mMobileNumber = "+880" + mMobileNumberView.getText().toString().trim();  // TODO: change Bangladesh
        SignupOrLoginActivity.mAccountType = Constants.PERSONAL_ACCOUNT_TYPE;
        SignupOrLoginActivity.mBirthday = mBirthdayEditText.getText().toString().trim();
        if (mMaleCheckBox.isChecked()) SignupOrLoginActivity.mGender = Constants.GENDER_MALE;
        else SignupOrLoginActivity.mGender = Constants.GENDER_FEMALE;
        SignupOrLoginActivity.mPromoCode = mPromoCodeEditText.getText().toString().trim();

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

        if (mNameView.getText().toString().trim().length() == 0) {
            mNameView.setError(getString(R.string.error_invalid_first_name));
            focusView = mNameView;
            cancel = true;
        }

        if (mPromoCodeEditText.getText().toString().trim().length() == 0) {
            mPromoCodeEditText.setError(getActivity().getString(R.string.error_promo_code_empty));
            focusView = mPromoCodeEditText;
            cancel = true;
        }

//        if (mLastNameView.getText().toString().trim().length() == 0) {
//            mLastNameView.setError(getString(R.string.error_invalid_last_name));
//            focusView = mLastNameView;
//            cancel = true;
//        }

        if (SignupOrLoginActivity.mBirthday == null || SignupOrLoginActivity.mBirthday.length() == 0) {
            cancel = true;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.please_select_your_birthday, Toast.LENGTH_LONG).show();
        }

        if (mBirthdayEditText.getText().toString().trim().length() == 0) {
            mBirthdayEditText.setError(getString(R.string.error_invalid_birthday));
            focusView = mBirthdayEditText;
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
            OTPRequestPersonalSignup mOtpRequestPersonalSignup = new OTPRequestPersonalSignup(SignupOrLoginActivity.mMobileNumber,
                    Constants.MOBILE_ANDROID + mDeviceID, Constants.PERSONAL_ACCOUNT_TYPE, SignupOrLoginActivity.mPromoCode);
            Gson gson = new Gson();
            String json = gson.toJson(mOtpRequestPersonalSignup);
            mRequestOTPTask = new HttpRequestPostAsyncTask(Constants.COMMAND_OTP_VERIFICATION,
                    Constants.BASE_URL_POST_MM + Constants.URL_OTP_REQUEST, json, getActivity());
            mRequestOTPTask.mHttpResponseListener = this;
            mRequestOTPTask.execute((Void) null);
        }
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear + 1;
                    mDay = dayOfMonth;

                    String birthDate, birthMonth, birthYear;
                    if (mDay < 10) birthDate = "0" + mDay;
                    else birthDate = mDay + "";
                    if (mMonth < 10) birthMonth = "0" + mMonth;
                    else birthMonth = mMonth + "";
                    birthYear = mYear + "";

//                    SignupOrLoginActivity.mBirthday = birthDate + birthMonth + birthYear;
//                    String[] months = getActivity().getResources().getStringArray(R.array.months);
                    mBirthdayEditText.setText(birthDate + "/" + birthMonth + "/" + birthYear);
                }
            };

    @Override
    public void httpResponseReceiver(String result) {

        if (result == null) {
            mProgressDialog.dismiss();
            mRequestOTPTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.otp_request_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));

        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_OTP_VERIFICATION)) {

            String message = "";
            if (resultList.size() > 2) {
                try {
                    mOtpResponsePersonalSignup = gson.fromJson(resultList.get(2), OTPResponsePersonalSignup.class);
                    message = mOtpResponsePersonalSignup.getMessage();
                } catch (Exception e) {
                    e.printStackTrace();
                    message = getString(R.string.server_down);
                }
            } else {
                message = getString(R.string.server_down);
            }

            if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.otp_going_to_send, Toast.LENGTH_LONG).show();
                ((SignupOrLoginActivity) getActivity()).switchToOTPVerificationPersonalFragment();

            } else if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_NOT_ACCEPTABLE)) {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            } else {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }

            mProgressDialog.dismiss();
            mRequestOTPTask = null;
        }
    }
}

