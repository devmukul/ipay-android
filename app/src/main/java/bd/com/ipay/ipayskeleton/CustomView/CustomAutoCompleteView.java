package bd.com.ipay.ipayskeleton.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

public class CustomAutoCompleteView extends AutoCompleteTextView {

    public CustomAutoCompleteView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CustomAutoCompleteView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomAutoCompleteView(Context context) {
        super(context);
    }

    @Override
    public void performFiltering(CharSequence text, int keyCode) {
        // Empty string for using only the content from edit text as filter
        text = "";
        super.performFiltering(text, keyCode);
    }

    /*
     * After a selection we have to capture the new value and append to the existing text
     */
    @Override
    public void replaceText(CharSequence text) {
        super.replaceText(text);
    }
}

