package bd.com.ipay.ipayskeleton.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefConstants;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.TokenManager;

public class LauncherActivity extends AppCompatActivity {

    private boolean firstLaunch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences pref = getSharedPreferences(Constants.ApplicationTag, MODE_PRIVATE);
        firstLaunch = pref.getBoolean(SharedPrefConstants.FIRST_LAUNCH, true);
        startApplication();

        finish();

    }

    private void startApplication() {
        Intent intent;

        if (firstLaunch) {
            intent = new Intent(LauncherActivity.this, TourActivity.class);
            startActivity(intent);
            finish();
        } else {
            boolean loggedIn = ProfileInfoCacheManager.getLoggedInStatus(true);

            if (SharedPrefManager.isRememberMeActive() && loggedIn) {
                if (ProfileInfoCacheManager.isAccountSwitched()) {
                    TokenManager.setOnAccountId(ProfileInfoCacheManager.getOnAccountId());
                }
                intent = new Intent(LauncherActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                intent = new Intent(LauncherActivity.this, SignupOrLoginActivity.class);
                intent.putExtra(Constants.TARGET_FRAGMENT, Constants.SIGN_IN);
                startActivity(intent);
            }
            finish();
        }
    }
}
