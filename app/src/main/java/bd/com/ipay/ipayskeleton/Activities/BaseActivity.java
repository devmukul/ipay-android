package bd.com.ipay.ipayskeleton.Activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import bd.com.ipay.ipayskeleton.Utilities.MyApplication;

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
        myApp.isAppInBackground = false;
    }

    @Override
    public void onPause() {
        super.onPause();

        ((MyApplication) this.getApplication()).isAppInBackground = true;
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();

        ((MyApplication) this.getApplication()).stopUserInactivityDetectorTimer();
        ((MyApplication) this.getApplication()).startUserInactivityDetectorTimer();
    }

}