package bd.com.ipay.ipayskeleton.DatabaseHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DataBaseOpenHelper extends SQLiteOpenHelper {

    private final int newVersion;
    private final String name;

    public DataBaseOpenHelper(Context context, String name, int version) {
        super(context, name, null, version);
        this.newVersion = version;
        this.name = name;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createFriendsTable(db);
        createPushNotificationTable(db);
    }

    private void createFriendsTable(SQLiteDatabase db) {
        db.execSQL("create table if not exists " +
                DBConstants.DB_TABLE_FRIENDS +
                "(_id integer primary key autoincrement, " +
                DBConstants.KEY_MOBILE_NUMBER + " text unique not null, " +
                DBConstants.KEY_NAME + " text, " +
                DBConstants.KEY_ACCOUNT_TYPE + " integer default 1, " +
                DBConstants.KEY_PROFILE_PICTURE + " text, " +
                DBConstants.KEY_VERIFICATION_STATUS + " integer default 0, " +
                DBConstants.KEY_UPDATE_TIME + " long, " +
                DBConstants.KEY_IS_MEMBER + " integer default 0)");
    }

    private void createPushNotificationTable(SQLiteDatabase db) {
        db.execSQL("create table if not exists "
                + DBConstants.DB_TABLE_PUSH_EVENTS
                + "(" + DBConstants.KEY_TAG_NAME + " text unique not null, "
                + DBConstants.KEY_JSON + " text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}