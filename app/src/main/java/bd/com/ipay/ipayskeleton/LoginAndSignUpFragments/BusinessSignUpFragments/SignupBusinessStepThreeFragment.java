package bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.BusinessSignUpFragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
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
import android.widget.Toast;

import com.google.gson.Gson;


import bd.com.ipay.ipayskeleton.Activities.SignupOrLoginActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.Customview.AddressInputView;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.OTPRequestBusinessSignup;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.OTPResponseBusinessSignup;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Common.GenderList;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DeviceIdFactory;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;


public class SignupBusinessStepThreeFragment extends Fragment implements HttpResponseListener {
    private HttpRequestPostAsyncTask mRequestOTPTask = null;
    private OTPResponseBusinessSignup mOtpResponseBusinessSignup;

    private EditText mBusinessHolderFullNameView;

    private CheckBox mAddressCheckbox;

    private ImageView mDatePickerButton;
    private Button mSignupBusinessButton;
    private EditText mBirthdayEditText;
    private EditText mPersonalMobileNumberView;

    private Spinner mGenderSpinner;

    private int mYear;
    private int mMonth;
    private int mDay;

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
        View v = inflater.inflate(R.layout.fragment_signup_business_step_three, container, false);
        mDatePickerButton = (ImageView) v.findViewById(R.id.myDatePickerButton);

        mProgressDialog = new ProgressDialog(getActivity());


        mBusinessHolderFullNameView = (EditText) v.findViewById(R.id.full_name);

        mSignupBusinessButton = (Button) v.findViewById(R.id.business_sign_in_button);
        mPersonalMobileNumberView = (EditText) v.findViewById(R.id.personal_mobile_number);
        mBirthdayEditText = (EditText) v.findViewById(R.id.birthdayEditText);
        mGenderSpinner = (Spinner) v.findViewById(R.id.gender);
        mAddressCheckbox = (CheckBox) v.findViewById(R.id.checkboxBusinessAddress);

        mPersonalAddressView = (AddressInputView) v.findViewById(R.id.personal_address);

        mDeviceID = DeviceIdFactory.getDeviceId(getActivity());


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

        return v;
    }

    private void attemptRequestOTP() {
        if (mRequestOTPTask != null) {
            return;
        }

        String name = mBusinessHolderFullNameView.getText().toString().trim();

        // Store values at the time of the login attempt.
        SignupOrLoginActivity.mMobileNumberPersonal = "+880" +
                mPersonalMobileNumberView.getText().toString().trim();
        SignupOrLoginActivity.mAccountType = Constants.BUSINESS_ACCOUNT_TYPE;
        SignupOrLoginActivity.mBirthdayBusinessHolder = mBirthdayEditText.getText().toString().trim();
        SignupOrLoginActivity.mNameBusiness = name;
        SignupOrLoginActivity.mGender = GenderList.genderNameToCodeMap.get(
                mGenderSpinner.getSelectedItem().toString());

        boolean cancel = false;
        View focusView = null;

        if (SignupOrLoginActivity.mBirthdayBusinessHolder == null || SignupOrLoginActivity.mBirthdayBusinessHolder.length() == 0) {
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
            SignupOrLoginActivity.mAddressBusinessHolder = mPersonalAddressView.getInformation();
            mProgressDialog.setMessage(getString(R.string.progress_dialog_text_sending_sms));
            mProgressDialog.show();
            OTPRequestBusinessSignup mOtpRequestBusinessSignup = new OTPRequestBusinessSignup(SignupOrLoginActivity.mMobileNumberBusiness,
                    Constants.MOBILE_ANDROID + mDeviceID, Constants.BUSINESS_ACCOUNT_TYPE, SignupOrLoginActivity.mPromoCode);
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
    public void httpResponseReceiver(HttpResponseObject result) {

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

            String message = "";
            try {
                mOtpResponseBusinessSignup = gson.fromJson(result.getJsonString(), OTPResponseBusinessSignup.class);
                message = mOtpResponseBusinessSignup.getMessage();
            } catch (Exception e) {
                e.printStackTrace();
                message = getString(R.string.server_down);
            }


            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                SignupOrLoginActivity.otpDuration = mOtpResponseBusinessSignup.getOtpValidFor();
                ((SignupOrLoginActivity) getActivity()).switchToOTPVerificationBusinessFragment();

            } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_BAD_REQUEST) {
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

}


