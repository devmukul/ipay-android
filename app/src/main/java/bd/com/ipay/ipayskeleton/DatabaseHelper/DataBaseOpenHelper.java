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
        createBusinessAccountsTable(db);
    }

    private void createFriendsTable(SQLiteDatabase db) {
        db.execSQL("create table if not exists " +
                DBConstants.DB_TABLE_FRIENDS +
                "(_id integer primary key autoincrement, " +
                DBConstants.KEY_MOBILE_NUMBER + " text unique not null, " +
                DBConstants.KEY_NAME + " text, " +
                DBConstants.KEY_ORIGINAL_NAME + " text, " +
                DBConstants.KEY_ACCOUNT_TYPE + " integer default 1, " +
                DBConstants.KEY_PROFILE_PICTURE + " text, " +
                DBConstants.KEY_PROFILE_PICTURE_QUALITY_MEDIUM + " text, " +
                DBConstants.KEY_PROFILE_PICTURE_QUALITY_HIGH + " text, " +
                DBConstants.KEY_RELATIONSHIP + " text, " +
                DBConstants.KEY_VERIFICATION_STATUS + " integer default 0, " +
                DBConstants.KEY_UPDATE_TIME + " long, " +
                DBConstants.KEY_IS_MEMBER + " integer default 0)");
    }

    private void updateFriendsTable(SQLiteDatabase db) {
        db.execSQL("alter table " +
                DBConstants.DB_TABLE_FRIENDS +" add column "+
                DBConstants.KEY_IS_ACTIVE + " integer default 0");
    }

    private void createBusinessAccountsTable(SQLiteDatabase db) {
        db.execSQL("create table if not exists " +
                DBConstants.DB_TABLE_BUSINESS_ACCOUNTS +
                "(_id integer primary key autoincrement, " +
                DBConstants.KEY_BUSINESS_MOBILE_NUMBER + " text unique not null, " +
                DBConstants.KEY_BUSINESS_NAME + " text, " +
                DBConstants.BUSINESS_EMAIL + " text, " +
                DBConstants.KEY_BUSINESS_TYPE + " integer default 0, " +
                DBConstants.KEY_PROFILE_PICTURE + " text, " +
                DBConstants.KEY_PROFILE_PICTURE_QUALITY_MEDIUM + " text, " +
                DBConstants.KEY_PROFILE_PICTURE_QUALITY_HIGH + " text, " +
                DBConstants.KEY_BUSINESS_ACCOUNT_ID + " integer default 0)");
    }

    private void createPushNotificationTable(SQLiteDatabase db) {
        db.execSQL("create table if not exists "
                + DBConstants.DB_TABLE_PUSH_EVENTS
                + "(" + DBConstants.KEY_TAG_NAME + " text unique not null, "
                + DBConstants.KEY_JSON + " text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // The last case will contain the break statement only. As the migration will take place one by one.
        // Here's a nice explanation - http://stackoverflow.com/a/26916986/3145960
        switch (oldVersion) {
            case 8:
                db.execSQL("drop table if exists " + DBConstants.DB_TABLE_FRIENDS);
                createFriendsTable(db);
            case 9:
                createBusinessAccountsTable(db);
            case 10:
                updateFriendsTable(db);
                break;
        }
    }
}