package bd.com.ipay.ipayskeleton.LoginAndSignUpFragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.SignupOrLoginActivity;
import bd.com.ipay.ipayskeleton.Api.GetBusinessTypesAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Customview.AddressInputView;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.OTPRequestBusinessSignup;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.OTPResponseBusinessSignup;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.BusinessType;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Common.CommonData;
import bd.com.ipay.ipayskeleton.Utilities.Common.GenderList;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class SignupBusinessFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mRequestOTPTask = null;
    private OTPResponseBusinessSignup mOtpResponseBusinessSignup;

    private EditText mBusinessEmailView;
    private EditText mBusinessHolderFullNameView;

    private CheckBox mAddressCheckbox;
    private Spinner mBusinessType;

    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private EditText mBusinessNameView;
    private EditText mBusinessMobileNumberView;
    private ImageView mDatePickerButton;
    private Button mSignupBusinessButton;
    private CheckBox mAgreementCheckBox;
    private TextView mTermsConditions;
    private EditText mBirthdayEditText;
    private EditText mPersonalMobileNumberView;
    private EditText mPromoCodeEditText;

    private Spinner mGenderSpinner;

    private int mYear;
    private int mMonth;
    private int mDay;

    private AddressInputView mBusinessAddressView;
    private AddressInputView mPersonalAddressView;

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
        View v = inflater.inflate(R.layout.fragment_signup_business, container, false);
        mDatePickerButton = (ImageView) v.findViewById(R.id.myDatePickerButton);

        mProgressDialog = new ProgressDialog(getActivity());

        mPasswordView = (EditText) v.findViewById(R.id.password);
        mConfirmPasswordView = (EditText) v.findViewById(R.id.confirm_password);
        mBusinessEmailView = (EditText) v.findViewById(R.id.email);
        mBusinessMobileNumberView = (EditText) v.findViewById(R.id.business_mobile_number);

        mBusinessNameView = (EditText) v.findViewById(R.id.business_name);
        mBusinessHolderFullNameView = (EditText) v.findViewById(R.id.full_name);
        mBusinessType = (Spinner) v.findViewById(R.id.business_type);

        mSignupBusinessButton = (Button) v.findViewById(R.id.business_sign_in_button);
        mAgreementCheckBox = (CheckBox) v.findViewById(R.id.checkBoxTermsConditions);
        mTermsConditions = (TextView) v.findViewById(R.id.textViewTermsConditions);
        mPersonalMobileNumberView = (EditText) v.findViewById(R.id.personal_mobile_number);
        mBirthdayEditText = (EditText) v.findViewById(R.id.birthdayEditText);
        mGenderSpinner = (Spinner) v.findViewById(R.id.gender);
        mAddressCheckbox = (CheckBox) v.findViewById(R.id.checkboxBusinessAddress);
        mPromoCodeEditText = (EditText) v.findViewById(R.id.promo_code_edittext);

        mBusinessAddressView = (AddressInputView) v.findViewById(R.id.business_address);
        mPersonalAddressView = (AddressInputView) v.findViewById(R.id.personal_address);

        TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        mDeviceID = telephonyManager.getDeviceId();

        // Enable hyperlinked
        mTermsConditions.setMovementMethod(LinkMovementMethod.getInstance());

        mDatePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(),
                        mDateSetListener,
                        1990, 0, 1).show();
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


        ArrayAdapter<CharSequence> mAdapterGender = new ArrayAdapter<CharSequence>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, GenderList.genderNames);
        mGenderSpinner.setAdapter(mAdapterGender);

        // Asynchronously load business types into the spinner
        GetBusinessTypesAsyncTask getBusinessTypesAsyncTask =
                new GetBusinessTypesAsyncTask(getActivity(), businessTypeLoadListener);
        mProgressDialog.setMessage(getActivity().getString(R.string.progress_dialog_fetching_business_types));
        mProgressDialog.show();
        getBusinessTypesAsyncTask.execute();

        return v;
    }

    private void attemptRequestOTP() {
        if (mRequestOTPTask != null) {
            return;
        }

        // Reset errors.
        mPasswordView.setError(null);

        String name = mBusinessHolderFullNameView.getText().toString().trim();

        // Store values at the time of the login attempt.
        SignupOrLoginActivity.mPasswordBusiness = mPasswordView.getText().toString().trim();
        SignupOrLoginActivity.mBusinessName = mBusinessNameView.getText().toString().trim();
        SignupOrLoginActivity.mEmailBusiness = mBusinessEmailView.getText().toString().trim();
        SignupOrLoginActivity.mMobileNumberPersonal = "+880" +
                mPersonalMobileNumberView.getText().toString().trim();
        SignupOrLoginActivity.mMobileNumberBusiness = "+880" + mBusinessMobileNumberView.getText().toString().trim();  // TODO: change Bangladesh
        SignupOrLoginActivity.mAccountType = Constants.BUSINESS_ACCOUNT_TYPE;
        SignupOrLoginActivity.mBirthdayBusinessHolder = mBirthdayEditText.getText().toString().trim();
        SignupOrLoginActivity.mNameBusiness = name;
        SignupOrLoginActivity.mGender = GenderList.genderNameToCodeMap.get(
                mGenderSpinner.getSelectedItem().toString());
        SignupOrLoginActivity.mTypeofBusiness = CommonData.getBusinessTypeId(mBusinessType.getSelectedItem().toString());
        SignupOrLoginActivity.mPromoCode = mPromoCodeEditText.getText().toString().trim();

        SignupOrLoginActivity.mAddressBusiness = mBusinessAddressView.getInformation();
        SignupOrLoginActivity.mAddressBusinessHolder = mPersonalAddressView.getInformation();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        String passwordValidationMsg = Utilities.isPasswordValid(SignupOrLoginActivity.mPasswordBusiness);
        if (passwordValidationMsg.length() > 0) {
            mPasswordView.setError(passwordValidationMsg);
            focusView = mPasswordView;
            cancel = true;

        } else if (!mConfirmPasswordView.getText().toString().trim().equals(SignupOrLoginActivity.mPasswordBusiness) && mConfirmPasswordView.getVisibility() == View.VISIBLE) {
            mConfirmPasswordView.setError(getString(R.string.confirm_password_not_matched));
            focusView = mConfirmPasswordView;
            cancel = true;

        } else if (mBusinessMobileNumberView.getText().toString().trim().length() != 10) {
            mBusinessMobileNumberView.setError(getString(R.string.error_invalid_mobile_number));
            focusView = mBusinessMobileNumberView;
            cancel = true;

        } else if (mBusinessNameView.getText().toString().trim().length() == 0) {
            mBusinessNameView.setError(getString(R.string.invalid_business_name));
            focusView = mBusinessNameView;
            cancel = true;

        } else if (!Utilities.isValidEmail(SignupOrLoginActivity.mEmailBusiness)) {
            mBusinessEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mBusinessEmailView;
            cancel = true;

        } else if (SignupOrLoginActivity.mBirthdayBusinessHolder == null || SignupOrLoginActivity.mBirthdayBusinessHolder.length() == 0) {
            cancel = true;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.please_select_your_birthday, Toast.LENGTH_LONG).show();

        } else if (mPersonalMobileNumberView.getText().toString().trim().length() != 10) {
            mPersonalMobileNumberView.setError(getString(R.string.error_invalid_mobile_number));
            focusView = mPersonalMobileNumberView;
            cancel = true;

        } else if (mBusinessHolderFullNameView.getText().toString().trim().length() == 0) {
            mBusinessHolderFullNameView.setError(getString(R.string.error_invalid_name));
            focusView = mBusinessHolderFullNameView;
            cancel = true;

        } else if (mBirthdayEditText.getText().toString().trim().length() == 0) {
            mBirthdayEditText.setError(getString(R.string.error_invalid_birthday));
            focusView = mBirthdayEditText;
            cancel = true;

        } else if (mPromoCodeEditText.getText().toString().trim().length() == 0) {
            mPromoCodeEditText.setError(getActivity().getString(R.string.error_promo_code_empty));
            focusView = mPromoCodeEditText;
            cancel = true;
        } else if (!mAgreementCheckBox.isChecked()) {
            cancel = true;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.please_check_terms_and_conditions, Toast.LENGTH_LONG).show();

        } else if (!mBusinessAddressView.verifyUserInputs()) {
            cancel = true;
        } else if (!mPersonalAddressView.verifyUserInputs()) {
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            if (focusView != null) focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mProgressDialog.setMessage(getString(R.string.progress_dialog_text_sending_sms));
            mProgressDialog.show();
            OTPRequestBusinessSignup mOtpRequestBusinessSignup = new OTPRequestBusinessSignup(SignupOrLoginActivity.mMobileNumberBusiness,
                    Constants.MOBILE_ANDROID + mDeviceID, Constants.BUSINESS_ACCOUNT_TYPE, SignupOrLoginActivity.mPromoCode);
            Gson gson = new Gson();
            String json = gson.toJson(mOtpRequestBusinessSignup);
            mRequestOTPTask = new HttpRequestPostAsyncTask(Constants.COMMAND_OTP_VERIFICATION,
                    Constants.BASE_URL + Constants.URL_OTP_REQUEST_BUSINESS, json, getActivity());
            mRequestOTPTask.mHttpResponseListener = this;
            mRequestOTPTask.execute((Void) null);
        }
    }

    private void setAccountHolderAddress() {
        if (mBusinessAddressView.verifyUserInputs()) {
            mPersonalAddressView.setInformation(mBusinessAddressView.getInformation());
        }
    }

    private void resetAccountHolderAddress() {
        mPersonalAddressView.resetInformation();
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
                    mOtpResponseBusinessSignup = gson.fromJson(resultList.get(2), OTPResponseBusinessSignup.class);
                    message = mOtpResponseBusinessSignup.getMessage();
                } catch (Exception e) {
                    e.printStackTrace();
                    message = getString(R.string.server_down);
                }
            } else {
                message = getString(R.string.server_down);
            }

            if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                SignupOrLoginActivity.otpDuration = mOtpResponseBusinessSignup.getOtpValidFor();
                ((SignupOrLoginActivity) getActivity()).switchToOTPVerificationBusinessFragment();

            } else if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_BAD_REQUEST)) {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

                // Previous OTP has not expired yet.
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

    GetBusinessTypesAsyncTask.BusinessTypeLoadListener businessTypeLoadListener =
            new GetBusinessTypesAsyncTask.BusinessTypeLoadListener() {
                @Override
                public void onLoadSuccess(List<BusinessType> businessTypes) {
                    ArrayAdapter<String> businessTypeAdapter = new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_dropdown_item_1line,
                            CommonData.getBusinessTypeNames());
                    mBusinessType.setAdapter(businessTypeAdapter);
                    mProgressDialog.dismiss();
                }

                @Override
                public void onLoadFailed() {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), R.string.error_loading_data, Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                        mProgressDialog.dismiss();
                    }
                }
            };
}


