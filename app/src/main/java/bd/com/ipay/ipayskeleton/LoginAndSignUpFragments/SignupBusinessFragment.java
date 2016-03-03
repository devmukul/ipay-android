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
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.OTPRequestBusinessSignup;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.OTPResponseBusinessSignup;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.BusinessType;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CommonData;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class SignupBusinessFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mRequestOTPTask = null;
    private OTPResponseBusinessSignup mOtpResponseBusinessSignup;

    private EditText mBusinessEmailView;
    private EditText mBusinessHolderFullNameView;
    private EditText mBusinessAddressPostCode;
    private EditText mBusinessHolderAddressPostCode;
    private EditText mBusinessAddressCountry;
    private EditText mBusinessHolderAddressLine1;
    private EditText mBusinessHolderAddressLine2;
    private EditText mBusinessHolderAddressCountry;
    private EditText mBusinessAddressLine1;
    private EditText mBusinessAddressLine2;
    private CheckBox mAddressCheckbox;
    private Spinner mBusinessType;
    private Spinner mBusinessCity;
    private Spinner mBusinessHolderCity;
    private Spinner mBusinessDistrict;
    private Spinner mBusinessHolderDistrict;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private EditText mBusinessNameView;
    private EditText mMobileNumberView;
    private ImageView mDatePickerButton;
    private Button mSignupBusinessButton;
    private CheckBox mAgreementCheckBox;
    private TextView mTermsConditions;
    private EditText mBirthdayEditText;

    private int mYear;
    private int mMonth;
    private int mDay;

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
        mProgressDialog.setMessage(getString(R.string.progress_dialog_text_sending_sms));

        mPasswordView = (EditText) v.findViewById(R.id.password);
        mConfirmPasswordView = (EditText) v.findViewById(R.id.confirm_password);
        mBusinessEmailView = (EditText) v.findViewById(R.id.email);
        mMobileNumberView = (EditText) v.findViewById(R.id.mobile_number);

        mBusinessNameView = (EditText) v.findViewById(R.id.business_name);
        mBusinessHolderFullNameView = (EditText) v.findViewById(R.id.full_name);
        mBusinessAddressPostCode = (EditText) v.findViewById(R.id.postcode);
        mBusinessAddressCountry = (EditText) v.findViewById(R.id.country);
        mBusinessType = (Spinner) v.findViewById(R.id.business_type);
        mBusinessAddressLine1 = (EditText) v.findViewById(R.id.business_addr_line1);
        mBusinessAddressLine2 = (EditText) v.findViewById(R.id.business_addr_line2);
        mBusinessCity = (Spinner) v.findViewById(R.id.city);
        mBusinessDistrict = (Spinner) v.findViewById(R.id.district);

        mBusinessHolderAddressPostCode = (EditText) v.findViewById(R.id.acc_holders_postcode);
        mBusinessHolderAddressCountry = (EditText) v.findViewById(R.id.acc_holders_country);
        mBusinessHolderAddressLine1 = (EditText) v.findViewById(R.id.acc_holders_business_addr_line1);
        mBusinessHolderAddressLine2 = (EditText) v.findViewById(R.id.acc_holders_business_addr_line2);
        mBusinessHolderCity = (Spinner) v.findViewById(R.id.acc_holders_city);
        mBusinessHolderDistrict = (Spinner) v.findViewById(R.id.acc_holders_district);

        mSignupBusinessButton = (Button) v.findViewById(R.id.business_sign_in_button);
        mAgreementCheckBox = (CheckBox) v.findViewById(R.id.checkBoxTermsConditions);
        mTermsConditions = (TextView) v.findViewById(R.id.textViewTermsConditions);
        mBirthdayEditText = (EditText) v.findViewById(R.id.birthdayEditText);
        mAddressCheckbox = (CheckBox) v.findViewById(R.id.checkboxBusinessAddress);

        TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        mDeviceID = telephonyManager.getDeviceId();

        // Enable hyperlinked
        mTermsConditions.setMovementMethod(LinkMovementMethod.getInstance());

        ArrayAdapter<CharSequence> mAdapterBusinessCity = ArrayAdapter.createFromResource(getActivity(),
                R.array.city, android.R.layout.simple_dropdown_item_1line);
        mBusinessCity.setAdapter(mAdapterBusinessCity);
        mBusinessHolderCity.setAdapter(mAdapterBusinessCity);

        ArrayAdapter<CharSequence> mAdapterDistrict = ArrayAdapter.createFromResource(getActivity(),
                R.array.district, android.R.layout.simple_dropdown_item_1line);
        mBusinessDistrict.setAdapter(mAdapterDistrict);
        mBusinessHolderDistrict.setAdapter(mAdapterDistrict);

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
        String lastName = name.substring(name.lastIndexOf(" ") + 1);

        String firstName = "";
        if (name.split(" ").length > 1) {
            String names[] = name.split(" ");
            firstName = names[0];
        } else firstName = lastName;

        // Store values at the time of the login attempt.
        SignupOrLoginActivity.mPasswordBusiness = mPasswordView.getText().toString().trim();
        SignupOrLoginActivity.mBusinessName = mBusinessNameView.getText().toString().trim();
        SignupOrLoginActivity.mEmailBusiness = mBusinessEmailView.getText().toString().trim();
        SignupOrLoginActivity.mMobileNumberBusiness = "+880" + mMobileNumberView.getText().toString().trim();  // TODO: change Bangladesh
        SignupOrLoginActivity.mAccountType = Constants.BUSINESS_ACCOUNT_TYPE;
        SignupOrLoginActivity.mBirthdayBusinessHolder = mBirthdayEditText.getText().toString().trim();
        SignupOrLoginActivity.mFirstNameBusiness = firstName;
        SignupOrLoginActivity.mLastNameBusiness = lastName;
        SignupOrLoginActivity.mAddressLine1Business = mBusinessAddressLine1.getText().toString().trim();
        SignupOrLoginActivity.mAddressLine2Business = mBusinessAddressLine2.getText().toString().trim();
        SignupOrLoginActivity.mPostcodeBusiness = mBusinessAddressPostCode.getText().toString().trim();
        SignupOrLoginActivity.mCountryBusiness = mBusinessAddressCountry.getText().toString().trim();
        SignupOrLoginActivity.mDistrictBusiness = mBusinessDistrict.getSelectedItem().toString().trim();
        SignupOrLoginActivity.mCityBusiness = mBusinessCity.getSelectedItem().toString().trim();
        SignupOrLoginActivity.mTypeofBusiness = mBusinessType.getSelectedItem().toString().trim();

        SignupOrLoginActivity.mAddressLine1BusinessHolder = mBusinessHolderAddressLine1.getText().toString().trim();
        SignupOrLoginActivity.mAddressLine2BusinessHolder = mBusinessHolderAddressLine2.getText().toString().trim();
        SignupOrLoginActivity.mPostcodeBusinessHolder = mBusinessHolderAddressPostCode.getText().toString().trim();
        SignupOrLoginActivity.mCountryBusinessHolder = mBusinessHolderAddressCountry.getText().toString().trim();
        SignupOrLoginActivity.mDistrictBusinessHolder = mBusinessHolderDistrict.getSelectedItem().toString().trim();
        SignupOrLoginActivity.mCityBusinessHolder = mBusinessHolderCity.getSelectedItem().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!isPasswordValid(SignupOrLoginActivity.mPasswordBusiness)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;

        } else if (!mConfirmPasswordView.getText().toString().trim().equals(SignupOrLoginActivity.mPasswordBusiness) && mConfirmPasswordView.getVisibility() == View.VISIBLE) {
            mConfirmPasswordView.setError(getString(R.string.confirm_password_not_matched));
            focusView = mConfirmPasswordView;
            cancel = true;

        } else if (mMobileNumberView.getText().toString().trim().length() != 10) {
            mMobileNumberView.setError(getString(R.string.error_invalid_mobile_number));
            focusView = mMobileNumberView;
            cancel = true;

        } else if (mBusinessNameView.getText().toString().trim().length() == 0) {
            mBusinessNameView.setError(getString(R.string.invalid_business_name));
            focusView = mBusinessNameView;
            cancel = true;

        } else if (!isEmailValid(SignupOrLoginActivity.mEmailBusiness)) {
            mBusinessEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mBusinessEmailView;
            cancel = true;

        } else if (SignupOrLoginActivity.mBirthdayBusinessHolder == null || SignupOrLoginActivity.mBirthdayBusinessHolder.length() == 0) {
            cancel = true;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.please_select_your_birthday, Toast.LENGTH_LONG).show();

        } else if (mBirthdayEditText.getText().toString().trim().length() == 0) {
            mBirthdayEditText.setError(getString(R.string.error_invalid_birthday));
            focusView = mBirthdayEditText;
            cancel = true;

        } else if (!mAgreementCheckBox.isChecked()) {
            cancel = true;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.please_check_terms_and_conditions, Toast.LENGTH_LONG).show();

        } else if (mBusinessAddressLine1.getText().toString().trim().length() == 0) {
            mBusinessAddressLine1.setError(getString(R.string.invalid_address_line_1));
            focusView = mBusinessAddressLine1;
            cancel = true;

        } else if (mBusinessAddressLine2.getText().toString().trim().length() == 0) {
            mBusinessAddressLine2.setError(getString(R.string.invalid_address_line_2));
            focusView = mBusinessAddressLine2;
            cancel = true;

        } else if (mBusinessCity.getSelectedItemPosition() == 0) {
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.invalid_city, Toast.LENGTH_LONG).show();
            cancel = true;

        } else if (mBusinessDistrict.getSelectedItemPosition() == 0) {
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.invalid_district, Toast.LENGTH_LONG).show();
            cancel = true;

        } else if (mBusinessAddressPostCode.getText().toString().trim().length() == 0) {
            mBusinessAddressPostCode.setError(getString(R.string.invalid_postcode));
            focusView = mBusinessAddressPostCode;
            cancel = true;

        } else if (mBusinessAddressCountry.getText().toString().trim().length() == 0) {
            mBusinessAddressCountry.setError(getString(R.string.invalid_country));
            focusView = mBusinessAddressCountry;
            cancel = true;

        } else if (mBusinessHolderAddressLine1.getText().toString().trim().length() == 0) {
            mBusinessHolderAddressLine1.setError(getString(R.string.invalid_address_line_1));
            focusView = mBusinessHolderAddressLine1;
            cancel = true;

        } else if (mBusinessHolderAddressLine2.getText().toString().trim().length() == 0) {
            mBusinessHolderAddressLine2.setError(getString(R.string.invalid_address_line_2));
            focusView = mBusinessHolderAddressLine2;
            cancel = true;

        } else if (mBusinessHolderCity.getSelectedItemPosition() == 0) {
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.invalid_city, Toast.LENGTH_LONG).show();
            cancel = true;

        } else if (mBusinessHolderDistrict.getSelectedItemPosition() == 0) {
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.invalid_district, Toast.LENGTH_LONG).show();
            cancel = true;

        } else if (mBusinessHolderAddressPostCode.getText().toString().trim().length() == 0) {
            mBusinessHolderAddressPostCode.setError(getString(R.string.invalid_postcode));
            focusView = mBusinessHolderAddressPostCode;
            cancel = true;

        } else if (mBusinessHolderAddressCountry.getText().toString().trim().length() == 0) {
            mBusinessHolderAddressCountry.setError(getString(R.string.invalid_country));
            focusView = mBusinessHolderAddressCountry;
            cancel = true;

        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            if (focusView != null) focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mProgressDialog.show();
            OTPRequestBusinessSignup mOtpRequestBusinessSignup = new OTPRequestBusinessSignup(SignupOrLoginActivity.mMobileNumberBusiness,
                    Constants.MOBILE_ANDROID + mDeviceID, Constants.BUSINESS_ACCOUNT_TYPE);
            Gson gson = new Gson();
            String json = gson.toJson(mOtpRequestBusinessSignup);
            mRequestOTPTask = new HttpRequestPostAsyncTask(Constants.COMMAND_OTP_VERIFICATION,
                    Constants.BASE_URL_POST_MM + Constants.URL_OTP_REQUEST_BUSINESS, json, getActivity());
            mRequestOTPTask.mHttpResponseListener = this;
            mRequestOTPTask.execute((Void) null);
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private boolean isEmailValid(String email) {
        return email.length() > 0 && email.contains("@") && email.contains(".");
    }

    private void setAccountHolderAddress() {

        boolean cancel = false;
        View focusView = null;

        if (mBusinessAddressLine1.getText().toString().trim().length() == 0) {
            mBusinessAddressLine1.setError(getString(R.string.invalid_address_line_1));
            focusView = mBusinessAddressLine1;
            cancel = true;
        } else if (mBusinessAddressLine2.getText().toString().trim().length() == 0) {
            mBusinessAddressLine2.setError(getString(R.string.invalid_address_line_2));
            focusView = mBusinessAddressLine2;
            cancel = true;
        } else if (mBusinessCity.getSelectedItemPosition() == 0) {
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.invalid_city, Toast.LENGTH_LONG).show();
            cancel = true;
        } else if (mBusinessDistrict.getSelectedItemPosition() == 0) {
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.invalid_district, Toast.LENGTH_LONG).show();
            cancel = true;
        } else if (mBusinessAddressPostCode.getText().toString().trim().length() == 0) {
            mBusinessAddressPostCode.setError(getString(R.string.invalid_postcode));
            focusView = mBusinessAddressPostCode;
            cancel = true;
        } else if (mBusinessAddressCountry.getText().toString().trim().length() == 0) {
            mBusinessAddressCountry.setError(getString(R.string.invalid_country));
            focusView = mBusinessAddressCountry;
            cancel = true;
        }

        if (cancel) {
            if (focusView != null) focusView.requestFocus();
            mAddressCheckbox.setChecked(false);

        } else {
            mBusinessHolderAddressLine1.setText(mBusinessAddressLine1.getText().toString().trim());
            mBusinessHolderAddressLine2.setText(mBusinessAddressLine2.getText().toString().trim());
            mBusinessHolderAddressPostCode.setText(mBusinessAddressPostCode.getText().toString().trim());
            mBusinessHolderAddressCountry.setText(mBusinessAddressCountry.getText().toString().trim());
            mBusinessHolderCity.setSelection(mBusinessCity.getSelectedItemPosition());
            mBusinessHolderDistrict.setSelection(mBusinessDistrict.getSelectedItemPosition());
        }

    }

    private void resetAccountHolderAddress() {
        mBusinessHolderAddressLine1.setText("");
        mBusinessHolderAddressLine2.setText("");
        mBusinessHolderAddressPostCode.setText("");
        mBusinessHolderAddressCountry.setText("");
        mBusinessHolderCity.setSelection(0);
        mBusinessHolderDistrict.setSelection(0);
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

            if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_ACCEPTED)) {
                ((SignupOrLoginActivity) getActivity()).switchToOTPVerificationBusinessFragment();

            } else if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_BAD_REQUEST)) {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
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


