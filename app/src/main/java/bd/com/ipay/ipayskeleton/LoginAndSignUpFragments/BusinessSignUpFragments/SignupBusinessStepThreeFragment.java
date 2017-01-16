package bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.BusinessSignUpFragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;

import bd.com.ipay.ipayskeleton.Activities.SignupOrLoginActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.CustomView.AddressInputSignUpView;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.LoginAndSignUp.OTPRequestBusinessSignup;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.LoginAndSignUp.OTPResponseBusinessSignup;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DeviceInfoFactory;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;


public class SignupBusinessStepThreeFragment extends Fragment implements HttpResponseListener {
    private HttpRequestPostAsyncTask mRequestOTPTask = null;
    private OTPResponseBusinessSignup mOtpResponseBusinessSignup;

    private EditText mBusinessHolderFullNameView;

    private CheckBox mAddressCheckbox;

    private Button mSignupBusinessButton;
    private EditText mBirthdayEditText;
    private EditText mGenderEditText;
    private CheckBox mMaleCheckBox;
    private CheckBox mFemaleCheckBox;
    private TextView mTermsConditions;
    private TextView mPrivacyPolicy;
    private CheckBox mAgreementCheckBox;

    private int mYear;
    private int mMonth;
    private int mDay;

    private AddressInputSignUpView mPersonalAddressView;

    private String mDeviceID;

    private DatePickerDialog mDatePickerDialog;
    private String mDOB;

    private ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_signup_business_step_three, container, false);

        mProgressDialog = new ProgressDialog(getActivity());

        mBusinessHolderFullNameView = (EditText) v.findViewById(R.id.full_name);

        mSignupBusinessButton = (Button) v.findViewById(R.id.business_sign_in_button);
        mBirthdayEditText = (EditText) v.findViewById(R.id.birthdayEditText);
        mGenderEditText = (EditText) v.findViewById(R.id.genderEditText);
        mMaleCheckBox = (CheckBox) v.findViewById(R.id.checkBoxMale);
        mFemaleCheckBox = (CheckBox) v.findViewById(R.id.checkBoxFemale);
        mAddressCheckbox = (CheckBox) v.findViewById(R.id.checkboxBusinessAddress);
        mTermsConditions = (TextView) v.findViewById(R.id.textViewTermsConditions);
        mPrivacyPolicy = (TextView) v.findViewById(R.id.textViewPrivacyPolicy);
        mAgreementCheckBox = (CheckBox) v.findViewById(R.id.checkBoxTermsConditions);

        mPersonalAddressView = (AddressInputSignUpView) v.findViewById(R.id.personal_address);

        mPersonalAddressView.setHintAddressInput(getString(R.string.address_line_1),
                getString(R.string.address_line_2));
        mDeviceID = DeviceInfoFactory.getDeviceId(getActivity());

        mDatePickerDialog =Utilities.getDatePickerDialog(getActivity(),null,mDateSetListener);

        // Enable hyperlinked
        mTermsConditions.setMovementMethod(LinkMovementMethod.getInstance());
        mPrivacyPolicy.setMovementMethod(LinkMovementMethod.getInstance());

        mBirthdayEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatePickerDialog.show();
            }
        });
        mSignupBusinessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isConnectionAvailable(getActivity())) attemptRequestOTP();
                else if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            }
        });

        mAddressCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setAccountHolderAddress();
                } else {
                    resetAccountHolderAddress();
                }
            }
        });

        mMaleCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGenderEditText.setError(null);
                mMaleCheckBox.setChecked(true);
                mFemaleCheckBox.setChecked(false);

                setGenderCheckBoxTextColor(mMaleCheckBox.isChecked(), mFemaleCheckBox.isChecked());
            }
        });

        mFemaleCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGenderEditText.setError(null);
                mFemaleCheckBox.setChecked(true);
                mMaleCheckBox.setChecked(false);

                setGenderCheckBoxTextColor(mMaleCheckBox.isChecked(), mFemaleCheckBox.isChecked());
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.title_signup_business_page);
        setGenderCheckBoxTextColor(mMaleCheckBox.isChecked(), mFemaleCheckBox.isChecked());
    }

    private void setGenderCheckBoxTextColor(boolean maleCheckBoxChecked, boolean femaleCheckBoxChecked) {
        if (maleCheckBoxChecked)
            mMaleCheckBox.setTextColor((Color.WHITE));
        else
            mMaleCheckBox.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));

        if (femaleCheckBoxChecked)
            mFemaleCheckBox.setTextColor((Color.WHITE));
        else
            mFemaleCheckBox.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
    }

    private void attemptRequestOTP() {
        if (mRequestOTPTask != null) {
            return;
        }

        String name = mBusinessHolderFullNameView.getText().toString().trim();

        // Store values at the time of the login attempt.
        SignupOrLoginActivity.mMobileNumberPersonal = SignupOrLoginActivity.mMobileNumberBusiness;
        SignupOrLoginActivity.mAccountType = Constants.BUSINESS_ACCOUNT_TYPE;
        SignupOrLoginActivity.mBirthdayBusinessHolder = mDOB;
        SignupOrLoginActivity.mNameBusiness = name;

        boolean cancel = false;
        View focusView = null;

        mBirthdayEditText.setError(null);
        mBusinessHolderFullNameView.setError(null);

        if (mBusinessHolderFullNameView.getText().toString().trim().length() == 0) {
            mBusinessHolderFullNameView.setError(getString(R.string.error_invalid_name));
            focusView = mBusinessHolderFullNameView;
            cancel = true;

        } else if (!InputValidator.isValidName(mBusinessHolderFullNameView.getText().toString().trim())) {
            mBusinessHolderFullNameView.setError(getString(R.string.please_enter_valid_name));
            focusView = mBusinessHolderFullNameView;
            cancel = true;

        } else if (SignupOrLoginActivity.mBirthdayBusinessHolder == null || SignupOrLoginActivity.mBirthdayBusinessHolder.length() == 0) {
            mBirthdayEditText.setError(getString(R.string.error_invalid_birthday));
            focusView = mBirthdayEditText;
            cancel = true;

        } else if (mDOB == null) {
            mBirthdayEditText.setError(getString(R.string.error_invalid_birthday));
            focusView = mBirthdayEditText;
            cancel = true;

        } else if (!mPersonalAddressView.verifyUserInputs()) {
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
            SignupOrLoginActivity.mAddressBusinessHolder = mPersonalAddressView.getInformation();
            mProgressDialog.setMessage(getString(R.string.progress_dialog_text_sending_sms));
            mProgressDialog.show();
            OTPRequestBusinessSignup mOtpRequestBusinessSignup = new OTPRequestBusinessSignup(SignupOrLoginActivity.mMobileNumberBusiness,
                    Constants.MOBILE_ANDROID + mDeviceID, Constants.BUSINESS_ACCOUNT_TYPE);
            Gson gson = new Gson();
            String json = gson.toJson(mOtpRequestBusinessSignup);
            mRequestOTPTask = new HttpRequestPostAsyncTask(Constants.COMMAND_OTP_VERIFICATION,
                    Constants.BASE_URL_MM + Constants.URL_OTP_REQUEST_BUSINESS, json, getActivity());
            mRequestOTPTask.mHttpResponseListener = this;
            mRequestOTPTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private void setAccountHolderAddress() {
        mPersonalAddressView.setInformation(SignupOrLoginActivity.mAddressBusiness);
    }

    private void resetAccountHolderAddress() {
        mPersonalAddressView.resetInformation();
    }

    private final DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                    String[] mWeekArray, mMonthArray;
                    String birthDate, birthMonth, birthYear;
                    int dayofweek;

                    mYear = year;
                    mMonth = monthOfYear + 1;
                    mDay = dayOfMonth;
                    mWeekArray = getResources().getStringArray(R.array.day_of_week);
                    mMonthArray = getResources().getStringArray(R.array.month_name);

                    if (mDay < 10) birthDate = "0" + mDay;
                    else birthDate = mDay + "";
                    if (mMonth < 10) birthMonth = "0" + mMonth;
                    else birthMonth = mMonth + "";
                    birthYear = mYear + "";

                    Calendar c = Calendar.getInstance();
                    c.setTime(new Date(mYear, mMonth - 1, mDay));
                    dayofweek = c.get(Calendar.DAY_OF_WEEK);

                    mDOB = birthDate + "/" + birthMonth + "/" + birthYear;
                    mBirthdayEditText.setError(null);
                    mBirthdayEditText.setText(mWeekArray[dayofweek - 1] + " , " + mDay + " " + mMonthArray[mMonth - 1] + " , " + mYear);
                }
            };

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mRequestOTPTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.otp_request_failed, Toast.LENGTH_SHORT).show();
            return;
        }


        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_OTP_VERIFICATION)) {

            String message;
            try {
                mOtpResponseBusinessSignup = gson.fromJson(result.getJsonString(), OTPResponseBusinessSignup.class);
                message = mOtpResponseBusinessSignup.getMessage();
            } catch (Exception e) {
                e.printStackTrace();
                message = getString(R.string.server_down);
            }


            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.otp_going_to_send, Toast.LENGTH_LONG).show();

                SignupOrLoginActivity.otpDuration = mOtpResponseBusinessSignup.getOtpValidFor();
                ((SignupOrLoginActivity) getActivity()).switchToOTPVerificationBusinessFragment();

            } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_BAD_REQUEST) {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

                // Previous OTP has not expired yet.
                SignupOrLoginActivity.otpDuration = mOtpResponseBusinessSignup.getOtpValidFor();
                ((SignupOrLoginActivity) getActivity()).switchToOTPVerificationBusinessFragment();

            } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_ACCEPTABLE) {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

                // Previous OTP has not been expired yet
                SignupOrLoginActivity.otpDuration = mOtpResponseBusinessSignup.getOtpValidFor();
                ((SignupOrLoginActivity) getActivity()).switchToOTPVerificationBusinessFragment();

            } else {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }

            mProgressDialog.dismiss();
            mRequestOTPTask = null;
        }
    }

}


