package bd.com.ipay.ipayskeleton.SecuritySettingsFragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;

import bd.com.ipay.ipayskeleton.FingerPrintAuthentication.FingerprintAuthenticationDialog;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class FingerPrintAuthenticationSettingsFragment extends Fragment {
    Button mFingerPrintActivateButton;

    private SharedPreferences mPref;
    private boolean isFingerPrintAuthOn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fingerprint_settings, container, false);
        setTitle();

        mFingerPrintActivateButton = (Button) view.findViewById(R.id.fingerprint_activate_button);
        mPref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);

        setFingerprintActivateButtonStatus();
        setFingerprintAuthCheckboxActions();

        return view;
    }

    public void setTitle() {
        getActivity().setTitle(R.string.touch_id_for_login);
    }

    private void setFingerprintActivateButtonStatus() {
        isFingerPrintAuthOn = mPref.getBoolean(Constants.LOGIN_WITH_FINGERPRINT_AUTH, false);

        if (isFingerPrintAuthOn) {
            mFingerPrintActivateButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            mFingerPrintActivateButton.setText(R.string.fingerprint_authentication_activated);
            mFingerPrintActivateButton.setTextColor(getResources().getColor(R.color.colorWhite));
        } else {
            mFingerPrintActivateButton.setBackgroundColor(getResources().getColor(R.color.colorGray));
            mFingerPrintActivateButton.setText(R.string.activate_fingerprint);
            mFingerPrintActivateButton.setTextColor(Color.BLACK);
        }
    }

    private void setFingerprintAuthCheckboxActions() {
        mFingerPrintActivateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFingerPrintAuthOn) {
                    {
                        FingerprintAuthenticationDialog fingerprintAuthenticationDialog = new FingerprintAuthenticationDialog(getContext(),
                                FingerprintAuthenticationDialog.Stage.FINGERPRINT_ENCRYPT);
                        fingerprintAuthenticationDialog.setFinishEncryptionCheckerListener(new FingerprintAuthenticationDialog.FinishEncryptionCheckerListener() {
                            @Override
                            public void ifEncryptionFinished() {
                                if (mPref.getString(Constants.KEY_PASSWORD, "") != "") {
                                    mPref.edit().putBoolean(Constants.LOGIN_WITH_FINGERPRINT_AUTH, true).apply();
                                } else
                                    mPref.edit().putBoolean(Constants.LOGIN_WITH_FINGERPRINT_AUTH, false).apply();
                                setFingerprintActivateButtonStatus();
                            }
                        });
                    }
                } else {
                    ProfileInfoCacheManager.clearEncryptedPassword();
                    setFingerprintActivateButtonStatus();
                }
            }
        });
    }
}

