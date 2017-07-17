package bd.com.ipay.ipayskeleton.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefConstants;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

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
            intent = new Intent(LauncherActivity.this, SignupOrLoginActivity.class);
            intent.putExtra(Constants.TARGET_FRAGMENT, Constants.SIGN_IN);
            startActivity(intent);
            finish();
        }
    }
}
