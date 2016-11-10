package bd.com.ipay.ipayskeleton.Model.Friend;

import android.app.Activity;
import android.database.Cursor;

import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;

public class SearchContactClass {

    private String mQuery = "";
    private Activity activity;

    public SearchContactClass(Activity activity) {
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
}
