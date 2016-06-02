package bd.com.ipay.ipayskeleton.Customview.Dialogs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;

import bd.com.ipay.ipayskeleton.R;

/**
 * You should call validatePin() first when the positive button is clicked
 */
public class PinInputDialogBuilder extends MaterialDialog.Builder {

    private EditText mPinField;

    public PinInputDialogBuilder(Context context) {
        super(context);
        initializeView();
    }

    private void initializeView() {
        customView(R.layout.dialog_enter_pin, true);
        autoDismiss(false);
        mPinField = (EditText) this.build().getCustomView().findViewById(R.id.enter_pin);
        positiveText(R.string.ok);
        negativeText(R.string.cancel);
    }

    public void onSubmit(final MaterialDialog.SingleButtonCallback onSubmitListener) {
        onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mPinField.getWindowToken(), 0);

                if (!getPin().isEmpty()) {
                    onSubmitListener.onClick(dialog, which);
                    build().dismiss();
                    dialog.dismiss();
                } else {
                    mPinField.setError(getContext().getString(R.string.failed_empty_pin));
                    View focusView = mPinField;
                    focusView.requestFocus();
                }
            }
        });

        onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        });
    }

    public String getPin() {
        return mPinField.getText().toString();
    }
}
