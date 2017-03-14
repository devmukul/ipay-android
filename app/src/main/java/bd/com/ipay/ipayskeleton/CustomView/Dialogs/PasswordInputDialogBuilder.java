package bd.com.ipay.ipayskeleton.CustomView.Dialogs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class PasswordInputDialogBuilder extends MaterialDialog.Builder {

    private EditText mPasswordField;

    public PasswordInputDialogBuilder(Context context) {
        super(context);
        initializeView();
    }

    private void initializeView() {
        customView(R.layout.dialog_enter_password, true);
        autoDismiss(false);
        mPasswordField = (EditText) this.build().getCustomView().findViewById(R.id.enter_password);
        positiveText(R.string.ok);
        negativeText(R.string.cancel);

        Utilities.showKeyboard(context);
    }

    public void onSubmit(final MaterialDialog.SingleButtonCallback onSubmitListener) {
        onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                // Check for a valid password, if the user entered one.
                String passwordValidationMsg = InputValidator.isPasswordValid(mPasswordField.getText().toString());
                if (passwordValidationMsg.length() > 0) {
                    mPasswordField.setError(passwordValidationMsg);
                    View focusView = mPasswordField;
                    focusView.requestFocus();
                } else {
                    Utilities.hideKeyboard(context, mPasswordField);

                    onSubmitListener.onClick(dialog, which);

                    build().dismiss();
                    dialog.dismiss();
                }
            }
        });

        onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mPasswordField.getWindowToken(), 0);
                dialog.dismiss();
            }
        });
    }

    public String getPassword() {
        return mPasswordField.getText().toString();
    }
}
