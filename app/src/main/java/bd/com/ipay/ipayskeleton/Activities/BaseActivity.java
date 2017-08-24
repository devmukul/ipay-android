package bd.com.ipay.ipayskeleton.Activities;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.analytics.Tracker;

import bd.com.ipay.ipayskeleton.Utilities.MyApplication;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public abstract class BaseActivity extends AppCompatActivity {

    private final Context context;
    private Tracker mTracker;

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