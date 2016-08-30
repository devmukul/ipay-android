package bd.com.ipay.ipayskeleton.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class LauncherActivity extends AppCompatActivity {

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
            startActivity(i);
            finish();
        }

        finish();
    }
}
