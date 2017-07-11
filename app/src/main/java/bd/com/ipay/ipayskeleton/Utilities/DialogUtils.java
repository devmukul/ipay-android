package bd.com.ipay.ipayskeleton.Utilities;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import bd.com.ipay.ipayskeleton.R;

public class DialogUtils {
    public static void showAppUpdateRequiredDialog(final Context mContext) {
        MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                .title(R.string.update_ipay)
                .cancelable(false)
                .content(R.string.update_your_application)
                .positiveText(R.string.update_now)
                .negativeText(R.string.later)
                .show();

        dialog.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                Utilities.goToiPayInAppStore(mContext);
                ((Activity) mContext).finish();
            }
        });

        dialog.getBuilder().onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
                ((Activity) mContext).finish();
            }
        });

        dialog.show();
    }

    public static void showServiceNotAllowedDialog(final Context context) {
        showAlertDialog(context, context.getString(R.string.contact_support_message));
    }

    public static void showLiveChatNotAvailableDialog(final Context context) {
        showAlertDialog(context, context.getString(R.string.live_chat_not_available));
    }

    public static void showQRCodeNotAvailableDialog(final Context context) {
        showAlertDialog(context, context.getString(R.string.qr_code_unavailable));
    }

    public static void showUnableToSaveFileDialog(Context context) {
        showAlertDialog(context, context.getString(R.string.file_save_failed));
    }


    public static void showAlertDialog(final Context context, String message) {
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .cancelable(false)
                .content(message)
                .negativeText(R.string.cancel)
                .show();
        dialog.show();
    }

}
