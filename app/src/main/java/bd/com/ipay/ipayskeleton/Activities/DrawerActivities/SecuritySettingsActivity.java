package bd.com.ipay.ipayskeleton.Activities.DrawerActivities;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.analytics.Tracker;

import bd.com.ipay.ipayskeleton.SecuritySettingsFragments.AddTrustedPersonFragment;
import bd.com.ipay.ipayskeleton.SecuritySettingsFragments.ChangePasswordFragment;
import bd.com.ipay.ipayskeleton.SecuritySettingsFragments.FingerPrintAuthenticationSettingsFragment;
import bd.com.ipay.ipayskeleton.SecuritySettingsFragments.PasswordRecoveryFragment;
import bd.com.ipay.ipayskeleton.SecuritySettingsFragments.SecurityQuestionFragment;
import bd.com.ipay.ipayskeleton.SecuritySettingsFragments.SetPinFragment;
import bd.com.ipay.ipayskeleton.SecuritySettingsFragments.TrustedDeviceFragment;
import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.DrawerFragments.SecuritySettingsFragment;
import bd.com.ipay.ipayskeleton.SecuritySettingsFragments.TrustedNetworkFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.SecuritySettingsFragments.UpdateSecurityQuestionFragment;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class SecuritySettingsActivity extends BaseActivity {

    public static long otpDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_log);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        switchToAccountSettingsFragment();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Utilities.hideKeyboard(this);
            if (getSupportFragmentManager().getBackStackEntryCount() > 0)
                getSupportFragmentManager().popBackStack();
            else {
                finish();
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Utilities.hideKeyboard(this);
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStack();
        else
            super.onBackPressed();
    }

    public void switchToAccountSettingsFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStackImmediate();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new SecuritySettingsFragment()).commit();

    }

    public void switchToPasswordRecoveryFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 1)
            getSupportFragmentManager().popBackStackImmediate();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new PasswordRecoveryFragment()).addToBackStack(null).commit();
    }

    public void switchToSetPinFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 1)
            getSupportFragmentManager().popBackStackImmediate();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new SetPinFragment()).addToBackStack(null).commit();
    }

    public void switchToChangePasswordFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 1)
            getSupportFragmentManager().popBackStackImmediate();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new ChangePasswordFragment()).addToBackStack(null).commit();
    }

    public void switchToTrustedDeviceFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 1)
            getSupportFragmentManager().popBackStackImmediate();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new TrustedDeviceFragment()).addToBackStack(null).commit();
    }

    public void switchToTrustedPersonFragment() {
        Utilities.hideKeyboard(this);
        while (getSupportFragmentManager().getBackStackEntryCount() > 2)
            getSupportFragmentManager().popBackStackImmediate();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new TrustedNetworkFragment()).addToBackStack(null).commit();
    }

    public void switchToAddTrustedPerson() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 3)
            getSupportFragmentManager().popBackStackImmediate();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new AddTrustedPersonFragment()).addToBackStack(null).commit();
    }

    public void switchToSecurityQuestionFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 2)
            getSupportFragmentManager().popBackStackImmediate();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SecurityQuestionFragment()).addToBackStack(null).commit();
    }

    public void switchToUpdateSecurityQuestionFragment(Bundle bundle) {
        while (getSupportFragmentManager().getBackStackEntryCount() > 3)
            getSupportFragmentManager().popBackStackImmediate();

        UpdateSecurityQuestionFragment updateSecurityQuestionFragment = new UpdateSecurityQuestionFragment();
        updateSecurityQuestionFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().
                replace(R.id.fragment_container, updateSecurityQuestionFragment).addToBackStack(null).commit();
    }

    public void switchToFingerprintAuthenticationSettingsFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 1)
            getSupportFragmentManager().popBackStackImmediate();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new FingerPrintAuthenticationSettingsFragment()).addToBackStack(null).commit();
    }

    @Override
    public Context setContext() {
        return SecuritySettingsActivity.this;
    }
}
