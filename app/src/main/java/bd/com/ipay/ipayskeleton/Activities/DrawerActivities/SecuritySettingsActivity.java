package bd.com.ipay.ipayskeleton.Activities.DrawerActivities;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.DrawerFragments.AccountSettingsFragment;
import bd.com.ipay.ipayskeleton.ProfileFragments.TrustedNetworkFragment;
import bd.com.ipay.ipayskeleton.R;

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

    public void switchToAccountSettingsFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new AccountSettingsFragment()).commit();
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
