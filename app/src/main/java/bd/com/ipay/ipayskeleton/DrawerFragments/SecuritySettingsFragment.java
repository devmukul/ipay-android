package bd.com.ipay.ipayskeleton.DrawerFragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;

import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.SecuritySettingsActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.IconifiedTextViewWithButton;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.LogoutRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.LogoutResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.MyApplication;

public class SecuritySettingsFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mLogoutTask = null;
    private LogoutResponse mLogOutResponse;

    private IconifiedTextViewWithButton setPINHeader;
    private IconifiedTextViewWithButton changePasswordHeader;
    private IconifiedTextViewWithButton trustedDevicesHeader;
    private IconifiedTextViewWithButton passwordRecoveryHeader;
    private IconifiedTextViewWithButton logoutHeader;

    private ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_account_settings, container, false);
        setTitle();

        setPINHeader = (IconifiedTextViewWithButton) v.findViewById(R.id.set_pin);
        changePasswordHeader = (IconifiedTextViewWithButton) v.findViewById(R.id.change_password);
        trustedDevicesHeader = (IconifiedTextViewWithButton) v.findViewById(R.id.trusted_device);
        passwordRecoveryHeader = (IconifiedTextViewWithButton) v.findViewById(R.id.password_recovery);
        logoutHeader = (IconifiedTextViewWithButton) v.findViewById(R.id.logout_from_all_devices);

        mProgressDialog = new ProgressDialog(getActivity());

        setPINHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SecuritySettingsActivity) getActivity()).switchToSetPinFragment();
            }
        });

        changePasswordHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SecuritySettingsActivity) getActivity()).switchToChangePasswordFragment();
            }
        });

        trustedDevicesHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SecuritySettingsActivity) getActivity()).switchToTrustedDeviceFragment();
            }
        });

        passwordRecoveryHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SecuritySettingsActivity) getActivity()).switchToPasswordRecoveryFragment();
            }
        });

        logoutHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new android.app.AlertDialog.Builder(getContext())
                        .setMessage(R.string.logout_from_all_device_warning)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                logOutFromAllDevices();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
            }
        });

        if (getArguments() != null && getArguments().getBoolean(Constants.EXPAND_PIN, false)) {
            setPINHeader.performClick();
        }
        return v;
    }

    private void logOutFromAllDevices() {
        if (mLogoutTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_signing_out));
        mProgressDialog.show();

        LogoutRequest mLogoutModel = new LogoutRequest(ProfileInfoCacheManager.getMobileNumber());
        Gson gson = new Gson();
        String json = gson.toJson(mLogoutModel);

        mLogoutTask = new HttpRequestPostAsyncTask(Constants.COMMAND_LOG_OUT,
                Constants.BASE_URL_MM + Constants.URL_LOG_OUT_from_all_device, json, getActivity());
        mLogoutTask.mHttpResponseListener = this;

        mLogoutTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void setTitle() {
        getActivity().setTitle(R.string.security);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mLogoutTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_LONG).show();
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_LOG_OUT)) {

            try {
                mLogOutResponse = gson.fromJson(result.getJsonString(), LogoutResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    ((MyApplication) getActivity().getApplication()).launchLoginPage(null);
                } else {
                    Toast.makeText(getActivity(), mLogOutResponse.getMessage(), Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), R.string.could_not_sign_out, Toast.LENGTH_LONG).show();
            }

            mProgressDialog.dismiss();
            mLogoutTask = null;

        }
    }

}