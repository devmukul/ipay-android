package bd.com.ipay.ipayskeleton.SecuritySettingsFragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.SecuritySettingsActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPutAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.Model.MMModule.ChangeCredentials.ChangePasswordRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.ChangeCredentials.ChangePasswordResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class ChangePasswordFragment extends Fragment implements HttpResponseListener {
    private HttpRequestPutAsyncTask mChangePasswordTask = null;
    private ChangePasswordResponse mChangePasswordResponse;

    private ProgressDialog mProgressDialog;
    private SharedPreferences pref;

    private EditText mEnterCurrentPasswordEditText;
    private EditText mEnterNewPasswordEditText;
    private EditText mEnterConfirmNewPasswordEditText;
    private Button mChangePasswordButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_change_password, container, false);
        setTitle();

        mEnterCurrentPasswordEditText = (EditText) v.findViewById(R.id.current_password);
        mEnterNewPasswordEditText = (EditText) v.findViewById(R.id.new_password);
        mEnterConfirmNewPasswordEditText = (EditText) v.findViewById(R.id.confirm_new_password);

        mChangePasswordButton = (Button) v.findViewById(R.id.save_pass);

        mProgressDialog = new ProgressDialog(getActivity());

        mEnterCurrentPasswordEditText.requestFocus();
        final InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);;
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        mChangePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptChangePassword();
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void attemptChangePassword() {

        //hiding keyboard after save button pressed in change password
        Utilities.hideKeyboard(getActivity());

        if (mChangePasswordTask != null) {
            return;
        }

        boolean cancel = false;
        View focusView = null;

        String passwordValidationMsg = InputValidator.isPasswordValid(mEnterNewPasswordEditText.getText().toString().trim());

        if (mEnterCurrentPasswordEditText.getText().toString().length() < 5) {
            mEnterCurrentPasswordEditText.setError(getString(R.string.error_invalid_password));
            focusView = mEnterCurrentPasswordEditText;
            cancel = true;

        } else if (passwordValidationMsg.length() > 0) {
            mEnterNewPasswordEditText.setError(passwordValidationMsg);
            focusView = mEnterNewPasswordEditText;
            cancel = true;

        } else if (mEnterConfirmNewPasswordEditText.getText().toString().length() < 5
                || !(mEnterNewPasswordEditText.getText().toString()
                .equals(mEnterConfirmNewPasswordEditText.getText().toString()))) {
            mEnterConfirmNewPasswordEditText.setError(getString(R.string.confirm_password_not_matched));
            focusView = mEnterConfirmNewPasswordEditText;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            String newPassword = mEnterNewPasswordEditText.getText().toString().trim();
            String password = mEnterCurrentPasswordEditText.getText().toString().trim();

            mProgressDialog.setMessage(getString(R.string.change_password_progress));
            mProgressDialog.show();
            ChangePasswordRequest mChangePasswordRequest = new ChangePasswordRequest(password, newPassword);
            Gson gson = new Gson();
            String json = gson.toJson(mChangePasswordRequest);
            mChangePasswordTask = new HttpRequestPutAsyncTask(Constants.COMMAND_CHANGE_PASSWORD,
                    Constants.BASE_URL_MM + Constants.URL_CHANGE_PASSWORD, json, getActivity());
            mChangePasswordTask.mHttpResponseListener = this;
            mChangePasswordTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public void setTitle() {
        getActivity().setTitle(R.string.change_password);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mChangePasswordTask = null;

            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_LONG).show();
            return;
        }


        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_CHANGE_PASSWORD)) {

            try {
                mChangePasswordResponse = gson.fromJson(result.getJsonString(), ChangePasswordResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mChangePasswordResponse.getMessage(), Toast.LENGTH_LONG).show();
                    ((SecuritySettingsActivity) getActivity()).switchToAccountSettingsFragment();
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mChangePasswordResponse.getMessage(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.change_pass_failed, Toast.LENGTH_LONG).show();
            }

            mProgressDialog.dismiss();
            mChangePasswordTask = null;

        }
    }


}

