package bd.com.ipay.ipayskeleton.CustomView;

import android.content.Context;
import android.util.AttributeSet;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class CustomContactsSearchView extends ContactsSearchView {
    public String tag;

    public CustomContactsSearchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CustomContactsSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomContactsSearchView(Context context) {
        super(context);
    }

    public void setFragmentTag(String tag) {
        this.tag = tag;

        setFilters();
    }

    @Override
    public String FragmentTag() {
        return tag;
    }

    private void setFilters() {
        switch (tag) {
            case Constants.SEND_MONEY:
                mFilterByiPayMembersOnly = true;
                break;
            case Constants.REQUEST_MONEY:
                mFilterByiPayMembersOnly = true;
                break;
            case Constants.REQUEST_PAYMENT:
                mFilterByiPayMembersOnly = true;
                break;
        }
    }

}
