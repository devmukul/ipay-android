package bd.com.ipay.ipayskeleton.CustomView.Dialogs;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import bd.com.ipay.ipayskeleton.Api.HttpRequestPutAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.ChangeCredentials.SetPinRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.ChangeCredentials.SetPinResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class AddPinDialogBuilder extends MaterialDialog.Builder implements HttpResponseListener {

    private HttpRequestPutAsyncTask mSavePINTask = null;
    private SetPinResponse mSetPinResponse;

    private ProgressDialog mProgressDialog;

    private EditText mPinField;
    private EditText mConfirmPinField;
    private EditText mPasswordField;

    private final AddPinListener mAddPinListener;

    public AddPinDialogBuilder(Context context, AddPinListener addPinListener) {
        super(context);
        initializeView();
        this.mAddPinListener = addPinListener;
    }

    private void initializeView() {
        customView(R.layout.dialog_add_pin, true);

        View v = this.build().getCustomView();
        autoDismiss(false);

        mPinField = (EditText) v.findViewById(R.id.new_pin);
        mConfirmPinField = (EditText) v.findViewById(R.id.confirm_pin);
        mPasswordField = (EditText) v.findViewById(R.id.password);

        title(R.string.dialog_prompt_add_pin);

        positiveText(R.string.set_pin);
        negativeText(R.string.cancel);

        mProgressDialog = new ProgressDialog(context);

        onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                View focusView;
                String pin = mPinField.getText().toString();
                String password = mPasswordField.getText().toString();
                String passwordValidationMsg = InputValidator.isPasswordValid(password);

                if (pin.trim().length() != 4) {
                    mPinField.setError(getContext().getString(R.string.error_invalid_pin));
                    focusView = mPinField;
                    focusView.requestFocus();
                } else if (mConfirmPinField.getText().toString().length() != 4
                        || !(mPinField.getText().toString().equals(mConfirmPinField.getText().toString()))) {
                    mConfirmPinField.setError(getContext().getString(R.string.confirm_pin_not_matched));
                    focusView = mConfirmPinField;
                    focusView.requestFocus();
                } else if (passwordValidationMsg.length() > 0) {
                    mPasswordField.setError(passwordValidationMsg);
                    focusView = mPasswordField;
                    focusView.requestFocus();
                } else {
                    hideKeyboard();
                    dialog.dismiss();

                    attemptSavePin(pin, password);
                }
            }
        });

        onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                hideKeyboard();
                dialog.dismiss();
            }
        });

        Utilities.showKeyboard(context);

    }

    private void hideKeyboard() {
        Utilities.hideKeyboard(getContext(), mPinField);
        Utilities.hideKeyboard(getContext(), mConfirmPinField);
        Utilities.hideKeyboard(getContext(), mPasswordField);
    }

    private void attemptSavePin(String pin, String password) {
        if (mSavePINTask != null) {
            return;
        }

        mProgressDialog.setMessage(getContext().getString(R.string.saving_pin));
        mProgressDialog.show();

        SetPinRequest setPinRequest = new SetPinRequest(pin, password);

        Gson gson = new Gson();
        String json = gson.toJson(setPinRequest);

        mSavePINTask = new HttpRequestPutAsyncTask(Constants.COMMAND_SET_PIN,
                Constants.BASE_URL_MM + Constants.URL_SET_PIN, json, getContext(), this);
        mSavePINTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {

        mProgressDialog.dismiss();

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
					|| result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mSavePINTask = null;
            if (getContext() != null)
                Toast.makeText(getContext(), R.string.service_not_available, Toast.LENGTH_LONG).show();
            return;
        }


        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_SET_PIN)) {

            try {
                mSetPinResponse = gson.fromJson(result.getJsonString(), SetPinResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (getContext() != null)
                        Toast.makeText(getContext(), mSetPinResponse.getMessage(), Toast.LENGTH_LONG).show();

                    mAddPinListener.onPinAddSuccess();
                } else {
                    if (getContext() != null)
                        Toast.makeText(getContext(), mSetPinResponse.getMessage(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getContext() != null)
                    Toast.makeText(getContext(), R.string.save_failed, Toast.LENGTH_LONG).show();
            }

            mProgressDialog.dismiss();
            mSavePINTask = null;
        }
    }

    public interface AddPinListener {
        void onPinAddSuccess();
    }
}
