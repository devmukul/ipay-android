package bd.com.ipay.ipayskeleton.Customview;

import android.content.Context;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;

import bd.com.ipay.ipayskeleton.R;

public class PinDialogBuilder extends MaterialDialog.Builder {

    private EditText mPinField;

    public PinDialogBuilder(Context context) {
        super(context);
        initializeView(context);
    }

    private void initializeView(Context context) {
        customView(R.layout.dialog_enter_pin, true);
        mPinField = (EditText) this.build().getCustomView().findViewById(R.id.enter_pin);
        positiveText(R.string.ok);
        negativeText(R.string.cancel);
    }

    public String getPin() {
        return mPinField.getText().toString();
    }
}
