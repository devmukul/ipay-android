package bd.com.ipay.ipayskeleton.Utilities;

import android.app.Application;
import android.util.Log;

import bd.com.ipay.ipayskeleton.Service.GCM.PushNotificationStatusHolder;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("Application Context", getApplicationContext().toString());
        ProfileInfoCacheManager.initialize(getApplicationContext());
        PushNotificationStatusHolder.initialize(getApplicationContext());
    }
}
