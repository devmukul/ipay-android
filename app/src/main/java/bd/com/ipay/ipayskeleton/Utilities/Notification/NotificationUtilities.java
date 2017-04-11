package bd.com.ipay.ipayskeleton.Utilities.Notification;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import java.util.List;

import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class NotificationUtilities {

    private static boolean isForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        return componentInfo.getPackageName().equals(Constants.ApplicationPackage);
    }

    private static boolean isLoggedIn() {
        return ProfileInfoCacheManager.getLoggedInStatus(false);
    }

    public static boolean isUserActive(Context context) {
        return (isForeground(context) && isLoggedIn());
    }
}
