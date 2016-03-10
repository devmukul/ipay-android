package bd.com.ipay.ipayskeleton.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class LauncherActivity extends Activity {

    public static boolean isLoggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = null;

        SharedPreferences pref = getSharedPreferences(Constants.ApplicationTag, MODE_PRIVATE);

        if (pref.contains(Constants.LOGGEDIN) && pref.getBoolean(Constants.LOGGEDIN, false)) {
            isLoggedIn = true;
        }

        // TODO: remove this ...
//        isLoggedIn = true;

        if (!isLoggedIn) {
            i = new Intent(LauncherActivity.this, TourActivity.class);
            startActivity(i);
            finish();
        } else {
            i = new Intent(LauncherActivity.this, SignupOrLoginActivity.class);
            // Test
            // TODO: remove for test
//            pref.edit().putString(Constants.USERID, "+8801672191819").commit();
//            pref.edit().putString(Constants.NAME, "Samsil").commit();
//            pref.edit().putString(Constants.BIRTHDAY, "31-11-1987").commit();
//            pref.edit().putString(Constants.GENDER, "M").commit();
            i = new Intent(LauncherActivity.this, HomeActivity.class);
            startActivity(i);
            finish();
        }

        finish();
    }
}
