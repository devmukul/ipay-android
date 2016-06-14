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
    public static final String DB_TABLE_TRANSACTION_HISTORY = "transaction_history";

    public static final String DB_TRIGGER_ACTIVITY_LOG = "delete_till_50_activity_log";
    public static final String DB_TRIGGER_TRANSACTION_HISTORY = "delete_till_50_transaction_history";
    public static final Uri DB_TABLE_SUBSCRIBERS_URI = Uri
            .parse("sqlite://" + Constants.ApplicationPackage + "/" + DB_TABLE_FRIENDS);

    // Subscriber table
    public static final String KEY_MOBILE_NUMBER = "mobile_number";
    public static final String KEY_VERIFICATION_STATUS = "verification_status";
    public static final String KEY_NAME = "name";
    public static final String KEY_ACCOUNT_TYPE = "account_type";
    public static final String KEY_PROFILE_PICTURE = "profile_picture";
    public static final String KEY_IS_MEMBER = "is_member";
    public static final String KEY_UPDATE_TIME = "update_at";

    // Push events table
    public static final String KEY_TAG_NAME = "tag_name";
    public static final String KEY_JSON = "json";

    // Activity log table
    public static final String KEY_ACTIVITY_LOG_ID = "id";
    public static final String KEY_ACTIVITY_LOG_ACCOUNT_ID = "account_id";
    public static final String KEY_ACTIVITY_LOG_TYPE = "type";
    public static final String KEY_ACTIVITY_LOG_DESCRIPTION = "description";
    public static final String KEY_ACTIVITY_LOG_TIME = "time";

    // Transaction history table
    public static final String KEY_TRANSACTION_HISTORY_TRANSACTION_ID = "transaction_id";
    public static final String KEY_TRANSACTION_HISTORY_ORIGINATING_MOBILE_NUMBER = "originating_mobile_number";
    public static final String KEY_TRANSACTION_HISTORY_RECEIVER_INFO = "receiver_info";
    public static final String KEY_TRANSACTION_HISTORY_AMOUNT = "amount";
    public static final String KEY_TRANSACTION_HISTORY_FEE = "fee";
    public static final String KEY_TRANSACTION_HISTORY_NET_AMOUNT = "net_amount";
    public static final String KEY_TRANSACTION_HISTORY_BALANCE = "balance";
    public static final String KEY_TRANSACTION_HISTORY_SERVICE_ID = "service_id";
    public static final String KEY_TRANSACTION_HISTORY_STATUS_CODE = "status_code";
    public static final String KEY_TRANSACTION_HISTORY_PURPOSE = "purpose";
    public static final String KEY_TRANSACTION_HISTORY_STATUS_DESCRIPTION = "status_description";
    public static final String KEY_TRANSACTION_HISTORY_DESCRIPTION = "description";
    public static final String KEY_TRANSACTION_HISTORY_TIME = "time";
    public static final String KEY_TRANSACTION_HISTORY_REQUEST_TIME = "request_time";
    public static final String KEY_TRANSACTION_HISTORY_RESPONSE_TIME = "response_time";

    public static final String KEY_TRANSACTION_HISTORY_USER_NAME = "user_name";
    public static final String KEY_TRANSACTION_HISTORY_USER_MOBILE_NUMBER = "user_mobile_number";
    public static final String KEY_TRANSACTION_HISTORY_USER_PROFILE_PIC = "user_profile_pic";
    public static final String KEY_TRANSACTION_HISTORY_BANK_ACCOUNT_NUMBER = "bank_account_number";
    public static final String KEY_TRANSACTION_HISTORY_BANK_ACCOUNT_NAME = "bank_account_name";
    public static final String KEY_TRANSACTION_HISTORY_BANK_NAME = "bank_name";
    public static final String KEY_TRANSACTION_HISTORY_BRANCH_NAME = "branch_name";


    public static final int VERIFIED_USER = 1;
    public static final int NOT_VERIFIED_USER = 0;

    public static final int IPAY_MEMBER = 1;
    public static final int NOT_IPAY_MEMBER = 0;

    public static final int MAXIMUM_NUMBER_OF_ENTRIES_IN_ACTIVITY_LOG = 50;
    public static final int MAXIMUM_NUMBER_OF_ENTRIES_IN_TRANSACTION_HISTORY = 51;
}
