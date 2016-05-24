package bd.com.ipay.ipayskeleton.Utilities;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import org.acra.*;
import org.acra.annotation.*;

import bd.com.ipay.ipayskeleton.R;

@ReportsCrashes(formKey = "", mailTo = "dev.android@ipay.com.bd", mode = ReportingInteractionMode.SILENT,
        resToastText = R.string.crash_toast_text, resNotifTickerText = R.string.crash_notif_ticker_text,
        resNotifTitle = R.string.crash_notif_title, resNotifText = R.string.crash_notif_text,
        resNotifIcon = android.R.drawable.stat_notify_error, resDialogText = R.string.crash_dialog_text,
        resDialogIcon = android.R.drawable.ic_dialog_info, resDialogTitle = R.string.crash_dialog_title,
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt,
        resDialogOkToast = R.string.crash_dialog_ok_toast)
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

//        if (!Constants.DEBUG)
//            ACRA.init(this);
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}