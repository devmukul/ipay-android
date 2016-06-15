package bd.com.ipay.ipayskeleton.ForgotPasswordFragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import bd.com.ipay.ipayskeleton.Activities.ForgotPasswordActivity;
import bd.com.ipay.ipayskeleton.Activities.SignupOrLoginActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.Model.MMModule.ForgetPassword.ForgetPasswordRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.ForgetPassword.ForgetPasswordResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.DeviceIdFactory;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class ForgetPasswordFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mForgetPasswordTask = null;
    private ForgetPasswordResponse mForgetPasswordResponse;

    private EditText mMobileNumberEditText;
    private EditText mNameEditText;
    private EditText mDateOfBirthEditText;
    private ImageView mDatePickerButton;
    private DatePickerDialog datePickerDialog;
    private Button mForgetPasswordButton;

    private ProgressDialog mProgressDialog;

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.title_forget_password_page);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forget_password, container, false);

        mMobileNumberEditText = (EditText) view.findViewById(R.id.mobile_number);
        mNameEditText = (EditText) view.findViewById(R.id.name);
        mDateOfBirthEditText = (EditText) view.findViewById(R.id.birthdayEditText);
        mDatePickerButton = (ImageView) view.findViewById(R.id.myDatePickerButton);
        mForgetPasswordButton = (Button) view.findViewById(R.id.button_forget_password);

        mProgressDialog = new ProgressDialog(getActivity());

        datePickerDialog = new DatePickerDialog(
                getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mDateOfBirthEditText.setText(
                        String.format("%02d/%02d/%4d", dayOfMonth, monthOfYear + 1, year));
            }
        }, 1990, 0, 1);

        mDatePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        mForgetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyUserInputs()) {
                    String deviceId = DeviceIdFactory.getDeviceId(getActivity());

                    attemptSendOTPForgetPassword(mNameEditText.getText().toString(),
                            mMobileNumberEditText.getText().toString().trim(),
                            mDateOfBirthEditText.getText().toString().trim(),
                            Constants.MOBILE_ANDROID + deviceId);
                }
            }
        });

        return view;
    }

    private boolean verifyUserInputs() {
        boolean cancel = false;
        View focusView = null;

        String mobileNumber = mMobileNumberEditText.getText().toString().trim();
        String name = mNameEditText.getText().toString().trim();
        String dob = mDateOfBirthEditText.getText().toString().trim();

        if (!ContactEngine.isValidNumber(mobileNumber)) {
            mMobileNumberEditText.setError(getString(R.string.error_invalid_mobile_number));
            cancel = true;
            focusView = mMobileNumberEditText;
        } else if (name.isEmpty()) {
            mNameEditText.setError(getString(R.string.error_invalid_name));
            cancel = true;
            focusView = mNameEditText;
        } else if (!Utilities.isDateOfBirthValid(dob)) {
            mDateOfBirthEditText.setError(getString(R.string.please_enter_valid_date_of_birth));
            cancel = true;
            focusView = mDateOfBirthEditText;
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void attemptSendOTPForgetPassword(String name, String mobileNumber, String dob, String deviceID) {
        if (mForgetPasswordTask != null) {
            return;
        }

        mobileNumber = ContactEngine.formatMobileNumberBD(mobileNumber);

        mProgressDialog.setMessage(getString(R.string.sending_otp));
        mProgressDialog.show();
        ForgetPasswordRequest mForgetPasswordRequest = new ForgetPasswordRequest(name,
                mobileNumber, dob, deviceID);

        Gson gson = new Gson();
        String json = gson.toJson(mForgetPasswordRequest);
        mForgetPasswordTask = new HttpRequestPostAsyncTask(Constants.COMMAND_FORGET_PASSWORD_SEND_OTP,
                Constants.BASE_URL_MM + Constants.URL_SEND_OTP_FORGET_PASSWORD, json, getActivity());

        // Save the mobile number and device id in a static field so that it can be used later in OTP verification fragment
        SignupOrLoginActivity.mMobileNumber = mobileNumber;

        mForgetPasswordTask.mHttpResponseListener = this;
        mForgetPasswordTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR) {
            mProgressDialog.dismiss();
            mForgetPasswordTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }


        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_FORGET_PASSWORD_SEND_OTP)) {
            try {
                mForgetPasswordResponse = gson.fromJson(result.getJsonString(), ForgetPasswordResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    Intent intent = new Intent(getActivity(), ForgotPasswordActivity.class);
                    intent.putParcelableArrayListExtra(Constants.TRUSTED_OTP_RECEIVERS, mForgetPasswordResponse.getTrustedOtpReceiverList());
                    startActivity(intent);

                    // TODO Discuss with server team when "OTP has not been expired yet" message is received
//                    } else if (resultList.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_ACCEPTABLE) {
//                        Intent intent = new Intent(getActivity(), ForgotPasswordActivity.class);
//                        intent.putParcelableArrayListExtra(Constants.TRUSTED_OTP_RECEIVERS, mForgetPasswordResponse.getTrustedOtpReceiverList());
//                        startActivity(intent);

                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mForgetPasswordResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.otp_request_failed, Toast.LENGTH_SHORT).show();
            }

            mProgressDialog.dismiss();
            mForgetPasswordTask = null;
        }
    }
}
