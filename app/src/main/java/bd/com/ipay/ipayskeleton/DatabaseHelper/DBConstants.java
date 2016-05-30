package bd.com.ipay.ipayskeleton.DatabaseHelper;

import android.net.Uri;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class DBConstants {

    public static final String TAG = "DataBaseOpenHelper";
    public static final String ACTION_DATABASE_CHANGED = "ACTION_DATABASE_CHANGED";
    public static final String DB_PATH = "/data/data/" + Constants.ApplicationPackage + "/databases/";
    public static final String DB_IPAY = "iPayDatabase";
    public static final String DB_TABLE_FRIENDS = "friends";
    public static final String DB_TABLE_PUSH_EVENTS = "push_events";
    public static final Uri DB_TABLE_SUBSCRIBERS_URI = Uri
            .parse("sqlite://" + Constants.ApplicationPackage + "/" + DB_TABLE_FRIENDS);

    // Subscriber table
    public static final String KEY_MOBILE_NUMBER = "mobile_number";
    public static final String KEY_VERIFICATION_STATUS = "verification_status";
    public static final String KEY_NAME = "name";
    public static final String KEY_ACCOUNT_TYPE = "account_type";
    public static final String KEY_PROFILE_PICTURE = "profile_picture";
    public static final String KEY_IS_MEMBER = "is_member";

    public static final String KEY_TAG_NAME = "tag_name";
    public static final String KEY_JSON = "json";

    public static final int VERIFIED_USER = 1;
    public static final int NOT_VERIFIED_USER = 0;

    public static final int IPAY_MEMBER = 1;
    public static final int NOT_IPAY_MEMBER = 0;
}
