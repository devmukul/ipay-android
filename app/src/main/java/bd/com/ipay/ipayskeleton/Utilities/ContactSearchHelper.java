package bd.com.ipay.ipayskeleton.Utilities;

import android.app.Activity;
import android.database.Cursor;

import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;

public class ContactSearchHelper {

    private String mQuery = "";
    private Activity activity;

    private boolean mFilterByVerifiedMembers;
    private boolean mFilterByiPayMembers;
    private boolean mFilterByBusinessMembers;

    public ContactSearchHelper(Activity activity) {
        this.activity = activity;
    }

    public boolean searchMobileNumber(String mobileNumber) {
        mQuery = mobileNumber;

        DataHelper dataHelper = DataHelper.getInstance(activity);
        Cursor cursor = dataHelper.searchFriends(mQuery, false, false, false,
                false, false, false, null);

        if (cursor != null) {
            if (cursor.getCount() > 0)
                return true;
        }

        return false;
    }

    public boolean isFilterByVerifiedMembers() {
        return mFilterByVerifiedMembers;
    }

    public void setFilterByVerifiedMembers(boolean mFilterByVerifiedMembers) {
        this.mFilterByVerifiedMembers = mFilterByVerifiedMembers;
    }

    public boolean isFilterByiPayMembers() {
        return mFilterByiPayMembers;
    }

    public void setFilterByiPayMembers(boolean mFilterByiPayMembers) {
        this.mFilterByiPayMembers = mFilterByiPayMembers;
    }

    public boolean isFilterByBusinessMembers() {
        return mFilterByBusinessMembers;
    }

    public void setFilterByBusinessMembers(boolean mFilterByBusinessMembers) {
        this.mFilterByBusinessMembers = mFilterByBusinessMembers;
    }
}
