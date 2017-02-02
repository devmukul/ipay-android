package bd.com.ipay.ipayskeleton.SecuritySettingsFragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import bd.com.ipay.ipayskeleton.FingerPrintAuthentication.FingerprintAuthenticationDialog;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class FingerPrintAuthenticationSettingsFragment extends Fragment {
    private Button mFingerPrintActivateButton;
    private FingerprintAuthenticationDialog mFingerprintAuthenticationDialog;

    private SharedPreferences mPref;
    private boolean isFingerPrintAuthOn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fingerprint_settings, container, false);
        setTitle();

        mFingerPrintActivateButton = (Button) view.findViewById(R.id.fingerprint_activate_button);
        mPref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);

        setFingerprintActivationButtonView();
        setFingerprintActivationButtonActions();

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mFingerprintAuthenticationDialog != null) {
            mFingerprintAuthenticationDialog.stopFingerprintHandler();
        }
    }

    public void setTitle() {
        getActivity().setTitle(R.string.fingerprint_authentication);
    }

    private void setFingerprintActivationButtonView() {
        isFingerPrintAuthOn = mPref.getBoolean(Constants.IS_FINGERPRINT_AUTHENTICATION_ON, false);

        if (isFingerPrintAuthOn) {
            mFingerPrintActivateButton.setBackground(getResources().getDrawable(R.drawable.background_transparent_with_color_primary_border));
            mFingerPrintActivateButton.setText(R.string.deactivate_fingerprint);
            mFingerPrintActivateButton.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else {
            mFingerPrintActivateButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            mFingerPrintActivateButton.setText(R.string.activate_fingerprint);
            mFingerPrintActivateButton.setTextColor(getResources().getColor(R.color.colorWhite));
        }
    }

    private void setFingerprintActivationButtonActions() {
        mFingerPrintActivateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFingerPrintAuthOn)
                    showFingerprintActivationDialog();
                else
                    showFingerPrintDeactivationDialog();
            }
        });
    }

    private void showFingerprintActivationDialog() {
        mFingerprintAuthenticationDialog = new FingerprintAuthenticationDialog(getContext(),
                FingerprintAuthenticationDialog.Stage.FINGERPRINT_ENCRYPT);
        mFingerprintAuthenticationDialog.setFinishEncryptionCheckerListener(new FingerprintAuthenticationDialog.FinishEncryptionCheckerListener() {
            @Override
            public void ifEncryptionFinished() {
                if (mPref.getString(Constants.KEY_PASSWORD, "") != "") {
                    mPref.edit().putBoolean(Constants.IS_FINGERPRINT_AUTHENTICATION_ON, true).apply();
                    getActivity().onBackPressed();
                } else
                    mPref.edit().putBoolean(Constants.IS_FINGERPRINT_AUTHENTICATION_ON, false).apply();
                setFingerprintActivationButtonView();
            }
        });
    }

    private void showFingerPrintDeactivationDialog() {
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(getActivity());
        dialog
                .content(R.string.remove_fingerprint_authentication_alert_message)
                .cancelable(false)
                .positiveText(R.string.yes)
                .negativeText(R.string.no)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        ProfileInfoCacheManager.clearEncryptedPassword();

                        Toast.makeText(getActivity(), R.string.fingerprint_authentication_deactivated, Toast.LENGTH_LONG).show();
                        getActivity().onBackPressed();
                    }
                })
                .show();

    }
}

