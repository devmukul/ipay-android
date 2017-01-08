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

    // This is how to disable AutoCompleteTextView filter
    @Override
    public void performFiltering(final CharSequence text, final int keyCode) {
        String filterText = "";
        super.performFiltering(filterText, keyCode);
    }

    /*
     * After a selection we have to capture the new value and append to the existing text
     */
    @Override
    public void replaceText(final CharSequence text) {
        super.replaceText(text);
    }
}

