package bd.com.ipay.ipayskeleton.Activities.DrawerActivities;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import bd.com.ipay.ipayskeleton.SecuritySettingsFragments.AddTrustedPersonFragment;
import bd.com.ipay.ipayskeleton.SecuritySettingsFragments.ChangePasswordFragment;
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

    private boolean switchedToSettingsFragment = false;
    private boolean switchedToAddTrustedPersonFragment = false;
    private boolean switchedToPasswordRecoveryFragment = false;

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
            if (switchedToSettingsFragment)
                switchToAccountSettingsFragment();
            else if (switchedToAddTrustedPersonFragment)
                switchToTrustedPersonFragment();
            else if (switchedToPasswordRecoveryFragment)
                switchToPasswordRecoveryFragment();
            else
                onBackPressed();

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Utilities.hideKeyboard(this);
        if (switchedToSettingsFragment)
            switchToAccountSettingsFragment();
        else if (switchedToAddTrustedPersonFragment)
            switchToTrustedPersonFragment();
        else if (switchedToPasswordRecoveryFragment)
            switchToPasswordRecoveryFragment();
        else
            super.onBackPressed();
    }

    public void switchToAccountSettingsFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new SecuritySettingsFragment()).commit();
        switchedToSettingsFragment = false;
        switchedToAddTrustedPersonFragment = false;
        switchedToPasswordRecoveryFragment = false;

    }

    public void switchToPasswordRecoveryFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new PasswordRecoveryFragment()).commit();
        switchedToSettingsFragment = true;
        switchedToAddTrustedPersonFragment = false;
        switchedToPasswordRecoveryFragment = false;
    }

    public void switchToSetPinFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new SetPinFragment()).commit();
        switchedToSettingsFragment = true;
        switchedToAddTrustedPersonFragment = false;
        switchedToPasswordRecoveryFragment = false;
    }

    public void switchToChangePasswordFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new ChangePasswordFragment()).commit();
        switchedToSettingsFragment = true;
        switchedToAddTrustedPersonFragment = false;
        switchedToPasswordRecoveryFragment = false;
    }

    public void switchToTrustedDeviceFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new TrustedDeviceFragment()).commit();
        switchedToSettingsFragment = true;
        switchedToAddTrustedPersonFragment = false;
        switchedToPasswordRecoveryFragment = false;
    }

    public void switchToTrustedPersonFragment() {
        Utilities.hideKeyboard(this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new TrustedNetworkFragment()).commit();
        switchedToSettingsFragment = false;
        switchedToAddTrustedPersonFragment = false;
        switchedToPasswordRecoveryFragment = true;
    }

    public void switchToAddTrustedPerson() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new AddTrustedPersonFragment()).commit();
        switchedToSettingsFragment = false;
        switchedToAddTrustedPersonFragment = true;
        switchedToPasswordRecoveryFragment = false;
    }

    public void switchToSecurityQuestionFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SecurityQuestionFragment()).commit();
        switchedToSettingsFragment = false;
        switchedToAddTrustedPersonFragment = false;
        switchedToPasswordRecoveryFragment = true;
    }

    public void switchToUpdateSecurityQuestionFragment(Bundle bundle) {
        while (getSupportFragmentManager().getBackStackEntryCount() > 3)
            getSupportFragmentManager().popBackStackImmediate();

        UpdateSecurityQuestionFragment updateSecurityQuestionFragment = new UpdateSecurityQuestionFragment();
        updateSecurityQuestionFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, updateSecurityQuestionFragment)
                .addToBackStack(null)
                .commit();

        switchedToSettingsFragment = false;
        switchedToAddTrustedPersonFragment = false;
        switchedToPasswordRecoveryFragment = false;
    }

    @Override
    public Context setContext() {
        return SecuritySettingsActivity.this;
    }
}
