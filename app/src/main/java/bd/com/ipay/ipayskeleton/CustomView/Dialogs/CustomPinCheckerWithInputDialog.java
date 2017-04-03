package bd.com.ipay.ipayskeleton.CustomView.Dialogs;

import android.content.Context;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import bd.com.ipay.ipayskeleton.Utilities.PinChecker;

public class CustomPinCheckerWithInputDialog {
    private PinCheckAndSetListener pinCheckAndSetListener;
    private Context context;

    public CustomPinCheckerWithInputDialog(final Context context, final PinCheckAndSetListener pinCheckAndSetListener) {
        this.context = context;
        this.pinCheckAndSetListener = pinCheckAndSetListener;

        initPinChecker();
    }

    private void initPinChecker() {
        PinChecker pinChecker = new PinChecker(context, new PinChecker.PinCheckerListener() {
            @Override
            public void ifPinAdded() {
                showPinInputDialog();
            }
        });
        pinChecker.execute();
    }

    private void showPinInputDialog() {
        final PinInputDialogBuilder pinInputDialogBuilder = new PinInputDialogBuilder(context);

        pinInputDialogBuilder.onSubmit(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                if (pinCheckAndSetListener != null) {
                    pinCheckAndSetListener.ifPinCheckedAndAdded(pinInputDialogBuilder.getPin());
                }
            }
        });

        pinInputDialogBuilder.build().show();
    }

    public interface PinCheckAndSetListener {
        void ifPinCheckedAndAdded(String pin);
    }
}
