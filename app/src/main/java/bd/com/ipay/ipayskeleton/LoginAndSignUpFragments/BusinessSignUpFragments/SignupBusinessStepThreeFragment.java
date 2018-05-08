package bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.BusinessSignUpFragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import bd.com.ipay.ipayskeleton.Activities.SignupOrLoginActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.CustomView.AddressInputSignUpView;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.LoginAndSignUp.OTPRequestBusinessSignup;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.LoginAndSignUp.OTPResponseBusinessSignup;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DeviceInfoFactory;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.InvalidInputResponse;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;


public class SignupBusinessStepThreeFragment extends BaseFragment implements HttpResponseListener {
    private HttpRequestPostAsyncTask mRequestOTPTask = null;
    private OTPResponseBusinessSignup mOtpResponseBusinessSignup;

    private EditText mBusinessHolderFullNameView;

    private CheckBox mAddressCheckbox;

    private Button mSignupBusinessButton;
    private EditText mBirthdayEditText;
    private EditText mGenderEditText;
    private CheckBox mMaleCheckBox;
    private CheckBox mFemaleCheckBox;
    private TextView mTermsConditionsView;
    private TextView mPrivacyPolicyView;
    private CheckBox mAgreementCheckBox;
    private AddressInputSignUpView mPersonalAddressView;
    private ProgressDialog mProgressDialog;

    private String mDeviceID;

    private DatePickerDialog mDatePickerDialog;
    private String mDOB;
    private int mYear;
    private int mMonth;
    private int mDay;
    private String mDayName;

    private final DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                    String[] mMonthArray;
                    String birthDate, birthMonth, birthYear;

                    mYear = year;
                    mMonth = monthOfYear + 1;
                    mDay = dayOfMonth;
                    mMonthArray = getResources().getStringArray(R.array.month_name);

                    if (mDay < 10) birthDate = "0" + mDay;
                    else birthDate = mDay + "";
                    if (mMonth < 10) birthMonth = "0" + mMonth;
                    else birthMonth = mMonth + "";
                    birthYear = mYear + "";
                    mDOB = birthDate + "/" + birthMonth + "/" + birthYear;
                    try {
                        Date date = new SimpleDateFormat("dd/MM/yyyy").parse(mDOB);
                        mDayName = new SimpleDateFormat("EE").format(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    mBirthdayEditText.setError(null);
                    mBirthdayEditText.setText(mDayName + " , " + mDay + " " + mMonthArray[mMonth - 1] + " , " + mYear);
                }
            };

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
        mTermsConditionsView = (TextView) v.findViewById(R.id.textViewTermsConditions);
        mPrivacyPolicyView = (TextView) v.findViewById(R.id.textViewPrivacyPolicy);
        mAgreementCheckBox = (CheckBox) v.findViewById(R.id.checkBoxTermsConditions);

        mPersonalAddressView = (AddressInputSignUpView) v.findViewById(R.id.personal_address);

        mPersonalAddressView.setHintAddressInput(getString(R.string.address_line_1),
                getString(R.string.address_line_2));
        mDeviceID = DeviceInfoFactory.getDeviceId(getActivity());

        mDatePickerDialog = Utilities.getDatePickerDialog(getActivity(), null, mDateSetListener);

        // Enable hyperlinked
        mTermsConditionsView.setMovementMethod(LinkMovementMethod.getInstance());
        mPrivacyPolicyView.setMovementMethod(LinkMovementMethod.getInstance());

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
        Utilities.sendScreenTracker(mTracker, getString(R.string.screen_name_business_signup_step_3));
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

        String businessHoldersName = mBusinessHolderFullNameView.getText().toString().trim();

        // Store values at the time of the login attempt.
        SignupOrLoginActivity.mMobileNumberPersonal = SignupOrLoginActivity.mMobileNumberBusiness;
        SignupOrLoginActivity.mAccountType = Constants.BUSINESS_ACCOUNT_TYPE;
        SignupOrLoginActivity.mBirthdayBusinessHolder = mDOB;
        SignupOrLoginActivity.mNameBusiness = businessHoldersName;

        boolean cancel = false;
        View focusView = null;

        mBirthdayEditText.setError(null);
        mBusinessHolderFullNameView.setError(null);

        if (businessHoldersName.length() == 0) {
            mBusinessHolderFullNameView.setError(getString(R.string.error_invalid_name));
            focusView = mBusinessHolderFullNameView;
            cancel = true;

        } else if (!InputValidator.isValidNameWithRequiredLength(businessHoldersName)) {
            mBusinessHolderFullNameView.setError(getString(R.string.error_invalid_name_with_required_length));
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
                    Constants.BASE_URL_MM + Constants.URL_OTP_REQUEST_BUSINESS, json, getActivity(), false);
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

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {

        if (HttpErrorHandler.isErrorFound(result, getContext(), mProgressDialog)) {
            mProgressDialog.dismiss();
            mRequestOTPTask = null;
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
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.server_down, Toast.LENGTH_LONG).show();
                //Google Analytic event
                Utilities.sendExceptionTracker(mTracker, ProfileInfoCacheManager.getAccountId(), e.getMessage());
                return;
            }


            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.otp_going_to_send, Toast.LENGTH_LONG).show();

                SignupOrLoginActivity.otpDuration = mOtpResponseBusinessSignup.getOtpValidFor();
                ((SignupOrLoginActivity) getActivity()).switchToOTPVerificationBusinessFragment();

                //Google Analytic event
                Utilities.sendSuccessEventTracker(mTracker, "Business Signup to OTP", ProfileInfoCacheManager.getAccountId());

            } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_ACCEPTED || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_EXPIRED) {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                SignupOrLoginActivity.otpDuration = mOtpResponseBusinessSignup.getOtpValidFor();
                ((SignupOrLoginActivity) getActivity()).switchToOTPVerificationBusinessFragment();
            } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_BAD_REQUEST) {
                InvalidInputResponse invalidInputResponse = gson.fromJson(result.getJsonString(), InvalidInputResponse.class);
                String[] errorFields = invalidInputResponse.getErrorFieldNames();
                String errorMessage = invalidInputResponse.getMessage();
                if (errorFields != null) {
                    Toast.makeText(getActivity(),
                            Utilities.getErrorMessageForInvalidInput(errorFields, errorMessage), Toast.LENGTH_LONG).show();
                    Utilities.sendFailedEventTracker(mTracker, "Business Signup to OTP", ProfileInfoCacheManager.getAccountId(), Utilities.getErrorMessageForInvalidInput(errorFields, errorMessage));
                } else {
                    Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                    Utilities.sendFailedEventTracker(mTracker, "Business Signup to OTP", ProfileInfoCacheManager.getAccountId(), errorMessage);
                }
            } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_ACCEPTABLE) {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            } else {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

                //Google Analytic event
                Utilities.sendFailedEventTracker(mTracker, "Business Signup to OTP", ProfileInfoCacheManager.getAccountId(), message);

            }

            mProgressDialog.dismiss();
            mRequestOTPTask = null;
        }
    }

}


