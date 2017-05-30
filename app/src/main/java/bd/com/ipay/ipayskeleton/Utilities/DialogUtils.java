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

    public static void showServiceNotAllowedDialog(final Context mContext) {
        MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                .title(R.string.service_not_allowed)
                .cancelable(false)
                .content(R.string.contact_support)
                .negativeText(R.string.cancel)
                .show();
        dialog.show();
    }
}
