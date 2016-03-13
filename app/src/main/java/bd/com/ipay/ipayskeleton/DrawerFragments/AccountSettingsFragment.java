package bd.com.ipay.ipayskeleton.DrawerFragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.HomeActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.MMModule.ChangeCredentials.ChangePasswordRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.ChangeCredentials.ChangePasswordResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.ChangeCredentials.SetPinRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.ChangeCredentials.SetPinResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class AccountSettingsFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mSavePINTask = null;
    private SetPinResponse mSetPinResponse;

    private HttpRequestPostAsyncTask mChangePasswordTask = null;
    private ChangePasswordResponse mChangePasswordResponse;

    private EditText mEnterPINEditText;
    private EditText mEnterPasswordEditText;
    private View setPINHeader;
    private View changePasswordHeader;
    private Button setPINButton;
    private ImageView setPinArrow;
    private ImageView changePassArrow;
    private LinearLayout mPINChangeLayout;
    private LinearLayout mPassChangeLayout;
    private Button changePasswordButton;
    private EditText mEnterCurrentPasswordEditText;
    private EditText mEnterNewPasswordEditText;
    private EditText mEnterConfrimNewPasswordEditText;

    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_account_settings, container, false);
        ((HomeActivity) getActivity()).setTitle(R.string.account_settings);

        mEnterPINEditText = (EditText) v.findViewById(R.id.new_pin);
        mEnterPasswordEditText = (EditText) v.findViewById(R.id.password);

        mEnterCurrentPasswordEditText = (EditText) v.findViewById(R.id.current_password);
        mEnterNewPasswordEditText = (EditText) v.findViewById(R.id.new_password);
        mEnterConfrimNewPasswordEditText = (EditText) v.findViewById(R.id.confirm_new_password);
        changePasswordButton = (Button) v.findViewById(R.id.save_pass);

        setPINHeader = v.findViewById(R.id.set_pin_header);
        changePasswordHeader = v.findViewById(R.id.change_password);
        setPINButton = (Button) v.findViewById(R.id.save_pin);
        setPinArrow = (ImageView) v.findViewById(R.id.change_pin_arrow);
        changePassArrow = (ImageView) v.findViewById(R.id.change_pass_arrow);
        mPINChangeLayout = (LinearLayout) v.findViewById(R.id.pin_change_layout);
        mPassChangeLayout = (LinearLayout) v.findViewById(R.id.pass_change_layout);

        mProgressDialog = new ProgressDialog(getActivity());

        setPINHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPINChangeLayout.getVisibility() == View.VISIBLE) {
                    mPINChangeLayout.setVisibility(View.GONE);
                    setPinArrow.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
                } else {
                    mPINChangeLayout.setVisibility(View.VISIBLE);
                    Utilities.setLayoutAnim_slideDown(mPINChangeLayout, getActivity());
                    setPinArrow.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
                }
            }
        });

        changePasswordHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPassChangeLayout.getVisibility() == View.VISIBLE) {
                    mPassChangeLayout.setVisibility(View.GONE);
                    changePassArrow.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
                } else {
                    mPassChangeLayout.setVisibility(View.VISIBLE);
                    Utilities.setLayoutAnim_slideDown(mPassChangeLayout, getActivity());
                    changePassArrow.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
                }
            }
        });

        setPINButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSavePIN();
            }
        });

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptChangePassword();
            }
        });

        return v;
    }

    private void attemptSavePIN() {
        if (mSavePINTask != null) {
            return;
        }

        boolean cancel = false;
        View focusView = null;

        if (mEnterPINEditText.getText().toString().trim().length() != 4) {
            Toast.makeText(getActivity(), R.string.error_invalid_pin, Toast.LENGTH_LONG).show();
            focusView = mEnterPINEditText;
            cancel = true;
        } else if (mEnterPasswordEditText.getText().toString().trim().length() == 0) {
            Toast.makeText(getActivity(), R.string.error_invalid_password, Toast.LENGTH_LONG).show();
            focusView = mEnterPasswordEditText;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            String pin = mEnterPINEditText.getText().toString().trim();
            String password = mEnterPasswordEditText.getText().toString().trim();

            mProgressDialog.setMessage(getString(R.string.saving_pin));
            mProgressDialog.show();
            SetPinRequest mSetPinRequest = new SetPinRequest(password, pin);
            Gson gson = new Gson();
            String json = gson.toJson(mSetPinRequest);
            mSavePINTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SET_PIN,
                    Constants.BASE_URL_POST_MM + Constants.URL_SET_PIN, json, getActivity());
            mSavePINTask.mHttpResponseListener = this;
            mSavePINTask.execute((Void) null);
        }
    }

    private void attemptChangePassword() {
        if (mChangePasswordTask != null) {
            return;
        }

        boolean cancel = false;
        View focusView = null;

        if (mEnterCurrentPasswordEditText.getText().toString().length() < 5) {
            Toast.makeText(getActivity(), R.string.error_invalid_password, Toast.LENGTH_LONG).show();
            focusView = mEnterCurrentPasswordEditText;
            cancel = true;
        } else if (mEnterNewPasswordEditText.getText().toString().length() < 5) {
            Toast.makeText(getActivity(), R.string.error_invalid_password, Toast.LENGTH_LONG).show();
            focusView = mEnterNewPasswordEditText;
            cancel = true;
        } else if (mEnterConfrimNewPasswordEditText.getText().toString().length() < 5
                || !(mEnterConfrimNewPasswordEditText.getText().toString()
                .equals(mEnterConfrimNewPasswordEditText.getText().toString()))) {
            Toast.makeText(getActivity(), R.string.confirm_password_not_matched, Toast.LENGTH_LONG).show();
            focusView = mEnterConfrimNewPasswordEditText;
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
            mChangePasswordTask = new HttpRequestPostAsyncTask(Constants.COMMAND_CHANGE_PASSWORD,
                    Constants.BASE_URL_POST_MM + Constants.URL_CHANGE_PASSWORD, json, getActivity());
            mChangePasswordTask.mHttpResponseListener = this;
            mChangePasswordTask.execute((Void) null);
        }
    }

    @Override
    public void httpResponseReceiver(String result) {

        if (result == null) {
            mProgressDialog.dismiss();
            mSavePINTask = null;
            mChangePasswordTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_LONG).show();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_SET_PIN)) {

            if (resultList.size() > 2) {
                try {
                    mSetPinResponse = gson.fromJson(resultList.get(2), SetPinResponse.class);

                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), mSetPinResponse.getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), mSetPinResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.save_failed, Toast.LENGTH_LONG).show();
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.save_failed, Toast.LENGTH_LONG).show();

            mProgressDialog.dismiss();
            mSavePINTask = null;

        } else if (resultList.get(0).equals(Constants.COMMAND_CHANGE_PASSWORD)) {

            if (resultList.size() > 2) {
                try {
                    mChangePasswordResponse = gson.fromJson(resultList.get(2), ChangePasswordResponse.class);

                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), mChangePasswordResponse.getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), mChangePasswordResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.change_pass_failed, Toast.LENGTH_LONG).show();
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.change_pass_failed, Toast.LENGTH_LONG).show();

            mProgressDialog.dismiss();
            mChangePasswordTask = null;
        }
    }
}