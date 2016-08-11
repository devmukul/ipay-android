package bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.PersonalSignUpFragments;

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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import bd.com.ipay.ipayskeleton.Activities.SignupOrLoginActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.CheckPromoCodeRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.CheckPromoCodeResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.DeviceInfoFactory;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class SignupPersonalStepOneFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mCheckPromoCodeTask = null;
    private CheckPromoCodeResponse mCheckPromoCodeResponse;

    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private EditText mMobileNumberView;

    private EditText mNameView;
    private Button mNextButton;
    private CheckBox mMaleCheckBox;
    private CheckBox mFemaleCheckBox;
    private EditText mPromoCodeEditText;
    private EditText mBirthdayEditText;
    private EditText mGenderEditText;
    private ImageView mCrossButton;
    private Button mLoginButton;
    private String mDeviceID;
    private String mDOB;
    private ProgressDialog mProgressDialog;

    private int mYear;
    private int mMonth;
    private int mDay;

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.title_signup_personal_page);
        if (mMaleCheckBox.isChecked())
            mMaleCheckBox.setTextColor((Color.WHITE));
        if (mFemaleCheckBox.isChecked())
            mFemaleCheckBox.setTextColor((Color.WHITE));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_signup_personal_step_one, container, false);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.progress_dialog_validating_promo_code));

        mNameView = (EditText) v.findViewById(R.id.user_name);
        mPasswordView = (EditText) v.findViewById(R.id.password);
        mConfirmPasswordView = (EditText) v.findViewById(R.id.confirm_password);
        mMobileNumberView = (EditText) v.findViewById(R.id.mobile_number);
        mNextButton = (Button) v.findViewById(R.id.personal_sign_in_button);
        mMaleCheckBox = (CheckBox) v.findViewById(R.id.checkBoxMale);
        mFemaleCheckBox = (CheckBox) v.findViewById(R.id.checkBoxFemale);
        mPromoCodeEditText = (EditText) v.findViewById(R.id.promo_code_edittext);
        mBirthdayEditText = (EditText) v.findViewById(R.id.birthdayEditText);
        mGenderEditText = (EditText) v.findViewById(R.id.genderEditText);
        mCrossButton = (ImageView) v.findViewById(R.id.button_cross);
        mLoginButton = (Button) v.findViewById(R.id.button_log_in);

        mNameView.requestFocus();

        final DatePickerDialog dialog = new DatePickerDialog(
                getActivity(), mDateSetListener, 1990, 0, 1);

        mBirthdayEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        if (mMaleCheckBox.isChecked())
            mMaleCheckBox.setTextColor((Color.WHITE));
        if (mFemaleCheckBox.isChecked())
            mFemaleCheckBox.setTextColor((Color.WHITE));

        mMaleCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGenderEditText.setError(null);
                mMaleCheckBox.setChecked(true);
                mFemaleCheckBox.setChecked(false);
                mFemaleCheckBox.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
                mMaleCheckBox.setTextColor((Color.WHITE));
            }
        });

        mFemaleCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGenderEditText.setError(null);
                mFemaleCheckBox.setChecked(true);
                mMaleCheckBox.setChecked(false);
                mMaleCheckBox.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
                mFemaleCheckBox.setTextColor((Color.WHITE));

            }
        });
        mDeviceID = DeviceInfoFactory.getDeviceId(getActivity());

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isConnectionAvailable(getActivity())) attemptCheckPromoCode();
                else if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            }
        });

        mCrossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SignupOrLoginActivity) getActivity()).switchToTourActivity();
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SignupOrLoginActivity) getActivity()).switchToLoginFragment();
            }
        });

        return v;
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
                    mBirthdayEditText.setText(mWeekArray[dayofweek - 1] + " , " + mDay + mMonthArray[mMonth - 1] + " , " + mYear);
                }
            };

    private void attemptCheckPromoCode() {
        if (mCheckPromoCodeTask != null) {
            return;
        }

        // Reset errors.
        mNameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        SignupOrLoginActivity.mName = mNameView.getText().toString().trim();
        SignupOrLoginActivity.mBirthday = mDOB;

        // Store values at the time of the login attempt.
        SignupOrLoginActivity.mPassword = mPasswordView.getText().toString().trim();
        SignupOrLoginActivity.mMobileNumber = ContactEngine.formatMobileNumberBD(
                mMobileNumberView.getText().toString().trim());
        SignupOrLoginActivity.mAccountType = Constants.PERSONAL_ACCOUNT_TYPE;
        SignupOrLoginActivity.mPromoCode = mPromoCodeEditText.getText().toString().trim();
        if (mMaleCheckBox.isChecked()) SignupOrLoginActivity.mGender = Constants.GENDER_MALE;
        else SignupOrLoginActivity.mGender = Constants.GENDER_FEMALE;

        boolean cancel = false;
        View focusView = null;

        if (!ContactEngine.isValidNumber(SignupOrLoginActivity.mMobileNumber)) {
            mMobileNumberView.setError(getString(R.string.error_invalid_mobile_number));
            focusView = mMobileNumberView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        String passwordValidationMsg = InputValidator.isPasswordValid(SignupOrLoginActivity.mPassword);
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

        if (mPromoCodeEditText.getText().toString().trim().length() == 0) {
            mPromoCodeEditText.setError(getActivity().getString(R.string.error_promo_code_empty));
            focusView = mPromoCodeEditText;
            cancel = true;
        }

        if (mNameView.getText().toString().trim().length() == 0) {
            mNameView.setError(getString(R.string.error_invalid_first_name));
            focusView = mNameView;
            cancel = true;
        }

        if (SignupOrLoginActivity.mBirthday == null || SignupOrLoginActivity.mBirthday.length() == 0) {
            mBirthdayEditText.setError(getString(R.string.error_invalid_birthday));
            focusView = mBirthdayEditText;
            cancel = true;
        }

        if (!mMaleCheckBox.isChecked() && !mFemaleCheckBox.isChecked()) {
            mGenderEditText.setError(getString(R.string.please_select_a_gender));
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
    public void httpResponseReceiver(HttpResponseObject result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mCheckPromoCodeTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.try_again_later, Toast.LENGTH_SHORT).show();
            return;
        }


        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_CHECK_PROMO_CODE)) {

            String message;
            try {
                mCheckPromoCodeResponse = gson.fromJson(result.getJsonString(), CheckPromoCodeResponse.class);
                message = mCheckPromoCodeResponse.getMessage();
            } catch (Exception e) {
                e.printStackTrace();
                message = getString(R.string.server_down);
            }


            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
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

