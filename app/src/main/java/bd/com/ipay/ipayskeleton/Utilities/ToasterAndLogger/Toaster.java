package bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger;

import android.content.Context;
import android.widget.Toast;

import bd.com.ipay.ipayskeleton.Utilities.AppInstance.AppInstanceUtilities;

public class Toaster {

    public static void makeText(Context context, String text, int duration) {
        if (AppInstanceUtilities.isUserActive(context)) {
            Toast.makeText(context, text, duration).show();
        }
    }

    public static void makeText(Context context, int resId, int duration) {
        if (AppInstanceUtilities.isUserActive(context)) {
            Toast.makeText(context, resId, duration).show();
        }
    }
}
