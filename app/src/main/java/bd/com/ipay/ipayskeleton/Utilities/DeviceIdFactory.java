package bd.com.ipay.ipayskeleton.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

import java.util.UUID;

/**
 * Generates a random device ID in its first launch, later returns the same ID when a device ID
 * is requested.
 *
 * We could have just used {@link TelephonyManager#getDeviceId()} instead, but this requires
 * READ_PHONE_STATE permission, which we want to avoid.
 */
public class DeviceIdFactory {

    public static String getDeviceId(Context context) {
        SharedPreferences pref = context.getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);

        if (pref.contains(Constants.DEVICE_ID)) {
            return pref.getString(Constants.DEVICE_ID, "");
        } else {
            String deviceId = UUID.randomUUID().toString();
            pref.edit().putString(Constants.DEVICE_ID, deviceId).apply();
            return deviceId;
        }
    }
}
