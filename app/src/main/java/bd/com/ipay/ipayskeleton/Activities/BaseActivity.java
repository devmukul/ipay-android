package bd.com.ipay.ipayskeleton.Activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.MyApplication;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public abstract class BaseActivity extends AppCompatActivity {

    private final Context context;

    public BaseActivity() {
        this.context = setContext();
    }

    protected abstract Context setContext();

    @Override
    public void onResume() {
        super.onResume();

        MyApplication myApp = (MyApplication) this.getApplication();
        if (myApp.logoutForInactivity) {
            forceLogoutForInactivity();
        }

        myApp.stopUserInactivityDetectorTimer();
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MyApplication) this.getApplication()).startUserInactivityDetectorTimer();
    }

    private void forceLogoutForInactivity() {
        if (Utilities.isConnectionAvailable(context))
            ((MyApplication) this.getApplication())
                    .attemptLogout();
        else
            ((MyApplication) this.getApplication())
                    .launchLoginPage(getString(R.string.please_log_in_again));
    }
}