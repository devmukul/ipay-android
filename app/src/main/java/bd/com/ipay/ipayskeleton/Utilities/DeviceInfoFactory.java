package bd.com.ipay.ipayskeleton.Utilities;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

public class DeviceInfoFactory {

    public static String getDeviceName() {
        return android.os.Build.MANUFACTURER + "-" + android.os.Build.PRODUCT + " -" + Build.MODEL;
    }

    public static String getDeviceId(Context context) {
        return (android.os.Build.MODEL + "-" + Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID)).replaceAll("\\s+", "-");
    }
}
