package bd.com.ipay.ipayskeleton.SecuritySettingsFragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import bd.com.ipay.ipayskeleton.Activities.SignupOrLoginActivity;
import bd.com.ipay.ipayskeleton.FingerPrintAuthentication.FingerprintAuthenticationDialog;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.TokenManager;

public class FingerPrintAuthenticationSettingsFragment extends Fragment {
    private CheckBox mFingerPrintAuthOptionCheckbox;

    private SharedPreferences mPref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fingerprint_settings, container, false);
        setTitle();

        mFingerPrintAuthOptionCheckbox = (CheckBox) view.findViewById(R.id.fingerprint_option_checkbox);

        mPref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);

        setCheckedStatusOfFingerprintAuth();
        setFingerprintAuthCheckboxActions();

        return view;
    }

    public void setTitle() {
        getActivity().setTitle(R.string.set_pin);
    }

    private void setCheckedStatusOfFingerprintAuth() {
        boolean isFingerPrintAuthOn = mPref.getBoolean(Constants.LOGIN_WITH_FINGERPRINT_AUTH, false);
        if (isFingerPrintAuthOn) {
            mFingerPrintAuthOptionCheckbox.setChecked(true);
        } else mFingerPrintAuthOptionCheckbox.setChecked(false);
    }

    private void setFingerprintAuthCheckboxActions() {
        mFingerPrintAuthOptionCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
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
                                setCheckedStatusOfFingerprintAuth();
                            }
                        });
                    }
                } else {
                    ProfileInfoCacheManager.clearEncryptedPassword();
                }
            }
        });
    }

    /*private void clearEncryptedPassword() {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(Constants.KEY_PASSWORD, "");
        mPref.edit().putBoolean(Constants.LOGIN_WITH_FINGERPRINT_AUTH, false).apply();
        editor.commit();
    }*/
}

