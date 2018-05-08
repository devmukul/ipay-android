package bd.com.ipay.ipayskeleton.SecuritySettingsFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.FingerPrintAuthenticationManager.FingerprintAuthenticationDialog;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class FingerPrintAuthenticationSettingsFragment extends BaseFragment {
    private Button mFingerPrintActivateButton;
    private FingerprintAuthenticationDialog mFingerprintAuthenticationDialog;

    private boolean isFingerPrintAuthOn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fingerprint_settings, container, false);
        setTitle();

        mFingerPrintActivateButton = (Button) view.findViewById(R.id.fingerprint_activate_button);

        setFingerprintActivationButtonView();
        setFingerprintActivationButtonActions();

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mFingerprintAuthenticationDialog != null) {
            mFingerprintAuthenticationDialog.stopFingerprintAuthenticationListener();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.sendScreenTracker(mTracker, getString(R.string.screen_name_fingerprint_authentication));
    }

    public void setTitle() {
        getActivity().setTitle(R.string.fingerprint_authentication);
    }

    private void setFingerprintActivationButtonView() {
        isFingerPrintAuthOn = ProfileInfoCacheManager.getFingerprintAuthenticationStatus();

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
                if (ProfileInfoCacheManager.ifPasswordEncrypted()) {
                    ProfileInfoCacheManager.setFingerprintAuthenticationStatus(true);
                    getActivity().onBackPressed();
                } else
                    ProfileInfoCacheManager.setFingerprintAuthenticationStatus(false);
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

