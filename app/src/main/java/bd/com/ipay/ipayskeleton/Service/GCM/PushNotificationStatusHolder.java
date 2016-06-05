package bd.com.ipay.ipayskeleton.Service.GCM;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class PushNotificationStatusHolder {

    private SharedPreferences pref;

    public PushNotificationStatusHolder(Context context) {
        pref = context.getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
    }

    public boolean isUpdateNeeded(String tag) {
        return pref.getBoolean(tag, true);
    }

    public void setUpdateNeeded(String tag, boolean isUpdateNeeded) {
        pref.edit().putBoolean(tag, isUpdateNeeded).apply();
    }
}
