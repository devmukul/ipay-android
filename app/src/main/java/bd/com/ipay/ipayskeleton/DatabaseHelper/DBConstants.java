package bd.com.ipay.ipayskeleton.DatabaseHelper;

import android.net.Uri;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class DBConstants {

    public static final String TAG = "DataBaseOpenHelper";
    public static final String ACTION_DATABASE_CHANGED = "ACTION_DATABASE_CHANGED";
    public static final String DB_PATH = "/data/data/" + Constants.ApplicationPackage + "/databases/";
    public static final String DB_IPAY = "iPayDatabase";
    public static final String DB_TABLE_ACTIVITY_LOG = "activity_log";
    public static final String DB_TABLE_FRIENDS = "friends";
    public static final String DB_TABLE_PUSH_EVENTS = "push_events";
    public static final String DB_TRIGGER_ACTIVITY_LOG = "delete_till_50_activity_log";
    public static final Uri DB_TABLE_SUBSCRIBERS_URI = Uri
            .parse("sqlite://" + Constants.ApplicationPackage + "/" + DB_TABLE_FRIENDS);

    // Subscriber table
    public static final String KEY_MOBILE_NUMBER = "mobile_number";
    public static final String KEY_VERIFICATION_STATUS = "verification_status";
    public static final String KEY_NAME = "name";
    public static final String KEY_ACCOUNT_TYPE = "account_type";
    public static final String KEY_PROFILE_PICTURE = "profile_picture";
    public static final String KEY_IS_MEMBER = "is_member";

    // Push events table
    public static final String KEY_TAG_NAME = "tag_name";
    public static final String KEY_JSON = "json";

    // Activity log table
    public static final String KEY_ACTIVITY_LOG_ID = "id";
    public static final String KEY_ACTIVITY_LOG_ACCOUNT_ID = "account_id";
    public static final String KEY_ACTIVITY_LOG_TYPE = "type";
    public static final String KEY_ACTIVITY_LOG_DESCRIPTION = "description";
    public static final String KEY_ACTIVITY_LOG_TIME = "time";

    public static final int VERIFIED_USER = 1;
    public static final int NOT_VERIFIED_USER = 0;

    public static final int IPAY_MEMBER = 1;
    public static final int NOT_IPAY_MEMBER = 0;

    public static final int MAXIMUM_NUMBER_OF_ENTRIES_IN_ACTIVITY_LOG = 50;
}
