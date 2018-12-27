package bd.com.ipay.ipayskeleton.CustomView;

import android.content.Context;
import android.util.AttributeSet;

public class SelectionView extends android.support.v7.widget.AppCompatEditText {

    public SelectionView(Context context) {
        super(context);
    }

    public SelectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SelectionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(){
        this.setClickable(false);
    }
}
