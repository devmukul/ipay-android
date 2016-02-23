package bd.com.ipay.ipayskeleton.DatabaseHelper;

import android.net.Uri;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class DBConstants {

    public static final String TAG = "DataBaseOpenHelper";
    public static final String ACTION_DATABASE_CHANGED = "ACTION_DATABASE_CHANGED";
    public static final String DB_PATH = "/data/data/" + Constants.ApplicationPackage + "/databases/";
    public static final String DB_IPAY = "iPayDatabase";
    public static final String DB_TABLE_SUBSCRIBERS = "subscribers";
    public static final Uri DB_TABLE_SUBSCRIBERS_URI = Uri
            .parse("sqlite://" + Constants.ApplicationPackage + "/" + DB_TABLE_SUBSCRIBERS);

    public static final String KEY_MOBILE_NUMBER = "mobile_number";
    public static final String KEY_NAME = "name";
}
