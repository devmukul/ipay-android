package bd.com.ipay.ipayskeleton.Activities.DrawerActivities;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.DrawerFragments.SecuritySettingsFragment;
import bd.com.ipay.ipayskeleton.ProfileFragments.TrustedNetworkFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class SecuritySettingsActivity extends BaseActivity {

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
            if (switchedToPasswordRecoveryFragment)
                switchToAccountSettingsFragment();
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
        if (switchedToPasswordRecoveryFragment)
            switchToAccountSettingsFragment();
        else
            super.onBackPressed();
    }

    private void switchToAccountSettingsFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new SecuritySettingsFragment()).commit();
        switchedToPasswordRecoveryFragment = false;

    }

    public void switchToPasswordRecovery() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new TrustedNetworkFragment()).commit();
        switchedToPasswordRecoveryFragment = true;
    }

    @Override
    public Context setContext() {
        return SecuritySettingsActivity.this;
    }
}
