package bd.com.ipay.ipayskeleton.DatabaseHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseOpenHelper extends SQLiteOpenHelper {

    private int newVersion;
    private String name;

    public DataBaseOpenHelper(Context context, String name, int version) {
        super(context, name, null, version);
        this.newVersion = version;
        this.name = name;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createFriendsTable(db);
        createPushNotificationTable(db);
        createActivityLogTable(db);
        createTransactionHistoryTable(db);

        // Run Triggers here
        createActivityLogTableTrigger(db);
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
                DBConstants.KEY_IS_MEMBER + " integer default 0)");
    }

    private void createPushNotificationTable(SQLiteDatabase db) {
        db.execSQL("create table if not exists "
                + DBConstants.DB_TABLE_PUSH_EVENTS
                + "(" + DBConstants.KEY_TAG_NAME + " text unique not null, "
                + DBConstants.KEY_JSON + " text)");
    }

    private void createActivityLogTable(SQLiteDatabase db) {
        db.execSQL("create table if not exists " +
                DBConstants.DB_TABLE_ACTIVITY_LOG +
                "(" +
                DBConstants.KEY_ACTIVITY_LOG_ID + " integer unique not null, " +
                DBConstants.KEY_ACTIVITY_LOG_ACCOUNT_ID + " integer, " +
                DBConstants.KEY_ACTIVITY_LOG_DESCRIPTION + " text, " +
                DBConstants.KEY_ACTIVITY_LOG_TYPE + " integer, " +
                DBConstants.KEY_ACTIVITY_LOG_TIME + " integer)");
    }

    private void createActivityLogTableTrigger(SQLiteDatabase db) {
        db.execSQL("CREATE TRIGGER " + DBConstants.DB_TRIGGER_ACTIVITY_LOG + " INSERT ON "
                + DBConstants.DB_TABLE_ACTIVITY_LOG + " WHEN (select count(*) from "
                + DBConstants.DB_TABLE_ACTIVITY_LOG + ")>"
                + DBConstants.MAXIMUM_NUMBER_OF_ENTRIES_IN_ACTIVITY_LOG +
                " BEGIN DELETE FROM "
                + DBConstants.DB_TABLE_ACTIVITY_LOG
                + " WHERE " + DBConstants.DB_TABLE_ACTIVITY_LOG + "." + DBConstants.KEY_ACTIVITY_LOG_ID
                + " IN (SELECT " + DBConstants.DB_TABLE_ACTIVITY_LOG + "." + DBConstants.KEY_ACTIVITY_LOG_ID
                + " FROM " + DBConstants.DB_TABLE_ACTIVITY_LOG
                + " ORDER BY " + DBConstants.DB_TABLE_ACTIVITY_LOG + "." + DBConstants.KEY_ACTIVITY_LOG_TIME
                + " desc limit(select count(*) - " + DBConstants.MAXIMUM_NUMBER_OF_ENTRIES_IN_ACTIVITY_LOG
                + " from " + DBConstants.DB_TABLE_ACTIVITY_LOG + ")); END;");

    }

    private void createTransactionHistoryTable(SQLiteDatabase db) {
        db.execSQL("create table if not exists " +
                DBConstants.DB_TABLE_TRANSACTION_HISTORY + "(" +
                DBConstants.KEY_TRANSACTION_HISTORY_TRANSACTION_ID + " text primary key, " +
                DBConstants.KEY_TRANSACTION_HISTORY_ORIGINATING_MOBILE_NUMBER + " text not null, " +
                DBConstants.KEY_TRANSACTION_HISTORY_RECEIVER_INFO + " text," +
                DBConstants.KEY_TRANSACTION_HISTORY_AMOUNT + " text, " +
                DBConstants.KEY_TRANSACTION_HISTORY_FEE + " double, " +
                DBConstants.KEY_TRANSACTION_HISTORY_NET_AMOUNT + " double, " +
                DBConstants.KEY_TRANSACTION_HISTORY_BALANCE + " double, " +
                DBConstants.KEY_TRANSACTION_HISTORY_SERVICE_ID + " integer, " +
                DBConstants.KEY_TRANSACTION_HISTORY_STATUS_CODE + " integer, " +
                DBConstants.KEY_TRANSACTION_HISTORY_PURPOSE + " text, " +
                DBConstants.KEY_TRANSACTION_HISTORY_STATUS_DESCRIPTION + " text, " +
                DBConstants.KEY_TRANSACTION_HISTORY_DESCRIPTION + " text, " +
                DBConstants.KEY_TRANSACTION_TIME + " long, " +
                DBConstants.KEY_TRANSACTION_HISTORY_REQUEST_TIME + " long, " +
                DBConstants.KEY_TRANSACTION_HISTORY_RESPONSE_TIME + " long, " +
                DBConstants.KEY_TRANSACTION_HISTORY_USER_NAME + " text, " +
                DBConstants.KEY_TRANSACTION_HISTORY_USER_MOBILE_NUMBER + " text, " +
                DBConstants.KEY_TRANSACTION_HISTORY_USER_PROFILE_PIC + " text, " +
                DBConstants.KEY_TRANSACTION_HISTORY_BANK_ACCOUNT_NUMBER + " text, " +
                DBConstants.KEY_TRANSACTION_HISTORY_BANK_ACCOUNT_NAME + " text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO migration code - remove this later
        if (oldVersion < 2) {
            db.execSQL("drop table if exists " + DBConstants.DB_TABLE_FRIENDS);
            createFriendsTable(db);
        }

        if (oldVersion < 3) {
            createTransactionHistoryTable(db);
        }
    }
}