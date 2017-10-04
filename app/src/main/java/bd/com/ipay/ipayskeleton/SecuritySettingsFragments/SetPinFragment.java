package bd.com.ipay.ipayskeleton.SecuritySettingsFragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.SecuritySettingsActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPutAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.ChangeCredentials.SetPinRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.ChangeCredentials.SetPinResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.MyApplication;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class SetPinFragment extends BaseFragment implements HttpResponseListener {
    private HttpRequestPutAsyncTask mSavePINTask = null;
    private SetPinResponse mSetPinResponse;

    private ProgressDialog mProgressDialog;

    private EditText mEnterPINEditText;
    private EditText mConfirmPINEditText;
    private EditText mEnterPasswordEditText;
    private Button mSetPINButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_set_pin, container, false);
        setTitle();
        mEnterPINEditText = (EditText) v.findViewById(R.id.new_pin);
        mConfirmPINEditText = (EditText) v.findViewById(R.id.confirm_pin);
        mEnterPasswordEditText = (EditText) v.findViewById(R.id.password);
        mSetPINButton = (Button) v.findViewById(R.id.save_pin);

        mEnterPINEditText.requestFocus();
        final InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        mProgressDialog = new ProgressDialog(getActivity());

        mSetPINButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSavePIN();
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.sendScreenTracker(mTracker, getString(R.string.screen_name_set_pin));
    }

    private void attemptSavePIN() {

        if (mSavePINTask != null) {
            return;
        }

        boolean cancel = false;
        View focusView = null;

        String passwordValidationMsg = InputValidator.isPasswordValid(mEnterPasswordEditText.getText().toString());

        if (mEnterPINEditText.getText().toString().trim().length() != 4) {
            mEnterPINEditText.setError(getString(R.string.error_invalid_pin));
            focusView = mEnterPINEditText;
            cancel = true;
        } else if (mConfirmPINEditText.getText().toString().length() != 4
                || !(mEnterPINEditText.getText().toString().equals(mConfirmPINEditText.getText().toString()))) {
            mConfirmPINEditText.setError(getString(R.string.confirm_pin_not_matched));
            focusView = mConfirmPINEditText;
            cancel = true;
        } else if (passwordValidationMsg.length() > 0) {
            mEnterPasswordEditText.setError(passwordValidationMsg);
            focusView = mEnterPasswordEditText;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Hiding keyboard after save button pressed in set pin
            Utilities.hideKeyboard(getActivity());

            String pin = mEnterPINEditText.getText().toString().trim();
            String password = mEnterPasswordEditText.getText().toString().trim();

            mProgressDialog.setMessage(getString(R.string.saving_pin));
            mProgressDialog.show();
            SetPinRequest mSetPinRequest = new SetPinRequest(pin, password);
            Gson gson = new Gson();
            String json = gson.toJson(mSetPinRequest);
            mSavePINTask = new HttpRequestPutAsyncTask(Constants.COMMAND_SET_PIN,
                    Constants.BASE_URL_MM + Constants.URL_SET_PIN, json, getActivity());
            mSavePINTask.mHttpResponseListener = this;
            mSavePINTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public void setTitle() {
        getActivity().setTitle(R.string.set_pin);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mSavePINTask = null;

            if (getActivity() != null)
                Toaster.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_LONG);
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_SET_PIN)) {

            try {
                mSetPinResponse = gson.fromJson(result.getJsonString(), SetPinResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mSetPinResponse.getMessage(), Toast.LENGTH_LONG).show();
                    ((SecuritySettingsActivity) getActivity()).switchToAccountSettingsFragment();
                    //Google Analytic event
                    Utilities.sendSuccessEventTracker(mTracker, "Pin Set", ProfileInfoCacheManager.getAccountId());

                } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_BLOCKED) {
                    if (getActivity() != null)
                        ((MyApplication) getActivity().getApplication()).launchLoginPage(mSetPinResponse.getMessage());
                    Utilities.sendBlockedEventTracker(mTracker, "Pin Set", ProfileInfoCacheManager.getAccountId());
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mSetPinResponse.getMessage(), Toast.LENGTH_LONG).show();

                    //Google Analytic event
                    Utilities.sendFailedEventTracker(mTracker, "Pin Set", ProfileInfoCacheManager.getAccountId(), mSetPinResponse.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toaster.makeText(getActivity(), R.string.save_failed, Toast.LENGTH_LONG);

                //Google Analytic event
                Utilities.sendExceptionTracker(mTracker, ProfileInfoCacheManager.getAccountId(), e.getMessage());
            }

            mProgressDialog.dismiss();
            mSavePINTask = null;

        }
    }

}
