package bd.com.ipay.ipayskeleton.Service.FCM;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactSearchHelper;

public class TokenRefreshListenerService extends FirebaseInstanceIdService {
    private SharedPreferences pref;

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is also called
     * when the InstanceID token is initially generated, so this is where
     * you retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        if (Constants.DEBUG) Log.d("Firebase Token", "Refreshed token: " + refreshedToken);

        pref = this.getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
        saveRegistrationTokenInPref(refreshedToken);
    }

    private void saveRegistrationTokenInPref(String refreshedToken) {
        pref.edit().putString(Constants.PUSH_NOTIFICATION_TOKEN, refreshedToken).apply();
    }
}

