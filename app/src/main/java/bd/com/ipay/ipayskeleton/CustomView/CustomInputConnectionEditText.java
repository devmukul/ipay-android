package bd.com.ipay.ipayskeleton.CustomView;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;


public class CustomInputConnectionEditText extends android.support.v7.widget.AppCompatEditText {
    public CustomInputConnectionEditText(Context context) {
        super(context);
    }

    public CustomInputConnectionEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomInputConnectionEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        final EditorInfo editorInfo = new EditorInfo();
        editorInfo.actionLabel = "DEL";
        editorInfo.actionId = 9981;
        return new InputConnectionWrapper(super.onCreateInputConnection(editorInfo), true) {
            @Override
            public boolean sendKeyEvent(KeyEvent event) {
                return super.sendKeyEvent(event);
            }
        };
    }

}
