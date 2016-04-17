package bd.com.ipay.ipayskeleton.Customview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import bd.com.ipay.ipayskeleton.Activities.HomeActivity;
import bd.com.ipay.ipayskeleton.R;

public class AddPinDialogBuilder extends MaterialDialog.Builder {

    private EditText mPinField;

    public AddPinDialogBuilder(Context context) {
        super(context);
        initializeView(context);
    }

    private void initializeView(final Context context) {
        this
                .content(R.string.dialog_set_pin)
                .positiveText(R.string.set_pin)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        ((HomeActivity) context).switchToAccountSettingsFragmentForPin();
                    }
                });
    }

    public String getPin() {
        return mPinField.getText().toString();
    }
}
