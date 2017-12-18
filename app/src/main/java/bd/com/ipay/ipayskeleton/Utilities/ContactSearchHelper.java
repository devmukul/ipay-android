package bd.com.ipay.ipayskeleton.Utilities;

import android.app.Activity;
import android.database.Cursor;

import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;

public class ContactSearchHelper {

    private String mQuery = "";
    private Activity activity;

    public ContactSearchHelper(Activity activity) {
        this.activity = activity;
    }

    public boolean searchMobileNumber(String mobileNumber) {
        mQuery = mobileNumber;

        DataHelper dataHelper = DataHelper.getInstance(activity);
        Cursor cursor = null;

        if (ProfileInfoCacheManager.isAccountSwitched()) {
            Long onAccountId = Long.parseLong(TokenManager.getOnAccountId());
            cursor = dataHelper.searchBusinessContacts(mQuery, false, false, false,
                    false, false, false, null, onAccountId);
        } else {
            cursor = dataHelper.searchContacts(mQuery, false, false, false,
                    false, false, false, null);
        }

        if (cursor != null) {
            if (cursor.getCount() > 0)
                return true;
        }

        return false;
    }
}
