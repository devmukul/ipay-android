package bd.com.ipay.ipayskeleton.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class LauncherActivity extends BaseActivity {

    private boolean firstLaunch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i;

        SharedPreferences pref = getSharedPreferences(Constants.ApplicationTag, MODE_PRIVATE);
        firstLaunch = pref.getBoolean(Constants.FIRST_LAUNCH, true);

        if (firstLaunch) {
            i = new Intent(LauncherActivity.this, TourActivity.class);
            startActivity(i);
            finish();
        } else {
            i = new Intent(LauncherActivity.this, SignupOrLoginActivity.class);
            i.putExtra(Constants.TARGET_FRAGMENT, Constants.SIGN_IN);
            // Test
            // TODO: remove for test
//            pref.edit().putString(Constants.USERID, "+8801782182129").commit();
//            pref.edit().putString(Constants.NAME, "Reaz Murshed").commit();
//            pref.edit().putString(Constants.BIRTHDAY, "01-09-1989").commit();
//            pref.edit().putString(Constants.GENDER, "M").commit();
//            i = new Intent(LauncherActivity.this, HomeActivity.class);


            startActivity(i);
            finish();
        }

        finish();
    }

    @Override
    public Context setContext() {
        return LauncherActivity.this;
    }
}
