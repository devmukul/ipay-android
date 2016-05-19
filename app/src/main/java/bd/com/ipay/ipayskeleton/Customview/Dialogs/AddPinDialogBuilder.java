package bd.com.ipay.ipayskeleton.Customview.Dialogs;

import android.app.Activity;
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

import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.HomeActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPutAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.Model.MMModule.ChangeCredentials.SetPinRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.ChangeCredentials.SetPinResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class AddPinDialogBuilder extends MaterialDialog.Builder implements HttpResponseListener {

    private HttpRequestPutAsyncTask mSavePINTask = null;
    private SetPinResponse mSetPinResponse;

    private ProgressDialog mProgressDialog;

    private EditText mPinField;
    private EditText mPasswordField;

    private AddPinListener mAddPinListener;

    public AddPinDialogBuilder(Context context, AddPinListener addPinListener) {
        super(context);
        initializeView();
        this.mAddPinListener = addPinListener;
    }

    private void initializeView() {
        customView(R.layout.dialog_add_pin, true);

        View v = this.build().getCustomView();

        mPinField = (EditText) v.findViewById(R.id.new_pin);
        mPasswordField = (EditText) v.findViewById(R.id.password);

        title(R.string.dialog_prompt_add_pin);

        positiveText(R.string.set_pin);
        negativeText(R.string.cancel);

        mProgressDialog = new ProgressDialog(context);

        onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                String pin = mPinField.getText().toString();
                String password = mPasswordField.getText().toString();

                if (pin.isEmpty()) {
                    Toast.makeText(context, R.string.failed_empty_pin, Toast.LENGTH_LONG).show();
                } else if (!Utilities.isPasswordValid(password).isEmpty()) {
                    Toast.makeText(context, Utilities.isPasswordValid(password), Toast.LENGTH_LONG).show();
                } else {
                    attemptSavePin(pin, password);
                }

                Utilities.hideKeyboard(getContext(), mPasswordField);
            }
        });
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
    public void httpResponseReceiver(HttpResponseObject result) {

        mProgressDialog.dismiss();

        if (result == null) {
            mSavePINTask = null;
            if (getContext() != null)
                Toast.makeText(getContext(), R.string.request_failed, Toast.LENGTH_LONG).show();
            return;
        }


        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_SET_PIN)) {

            try {
                mSetPinResponse = gson.fromJson(result.getJsonString(), SetPinResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (getContext() != null)
                        Toast.makeText(getContext(), mSetPinResponse.getMessage(), Toast.LENGTH_LONG).show();

                    mAddPinListener.onPinAddSuccess(mSetPinResponse);
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
        void onPinAddSuccess(SetPinResponse setPinResponse);
    }
}
