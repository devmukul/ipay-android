package bd.com.ipay.ipayskeleton.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import bd.com.ipay.ipayskeleton.Model.SqLiteDatabase.SubscriberEntry;

public class DataHelper {

    private static final int DATABASE_VERSION = 1;

    private Context context;
    private static DataHelper instance = null;
    DataBaseOpenHelper dOpenHelper;

    private DataHelper(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized DataHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DataHelper(context);
        }
        return instance;
    }

    public void closeDbOpenHelper() {
        if (dOpenHelper != null) dOpenHelper.close();
        instance = null;
    }

    public void createSubscribers(SubscriberEntry mSubscriberEntry) {
        SQLiteDatabase db = null;

        try {
            dOpenHelper = new DataBaseOpenHelper(context, DBConstants.DB_IPAY,
                    DATABASE_VERSION);
            dOpenHelper.getSubscriberEntryDao().create(mSubscriberEntry);

//            ContentValues values = new ContentValues();
//            values.put(DBConstants.KEY_MOBILE_NUMBER, mSubscriberEntry.getMobileNumber());
//            values.put(DBConstants.KEY_NAME, mSubscriberEntry.getName());
//            db = dOpenHelper.getWritableDatabase();
//            db.insert(DBConstants.DB_TABLE_SUBSCRIBERS, null, values);
            context.getContentResolver().notifyChange(DBConstants.DB_TABLE_SUBSCRIBERS_URI, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Cursor getSubscribers() {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            dOpenHelper = new DataBaseOpenHelper(context, DBConstants.DB_IPAY,
                    DATABASE_VERSION);
            db = dOpenHelper.getReadableDatabase();

            cursor = db.rawQuery("SELECT * FROM " + DBConstants.DB_TABLE_SUBSCRIBERS, null);

            if (cursor != null) {
                cursor.getCount();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cursor;
    }

    public boolean checkIfStringFieldExists(String tableName,
                                            String DBField, String fieldValue) {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            dOpenHelper = new DataBaseOpenHelper(context, DBConstants.DB_IPAY,
                    DATABASE_VERSION);
            db = dOpenHelper.getReadableDatabase();
            String query = "SELECT * FROM " + tableName + " WHERE " + DBField + " = '" + fieldValue + "'";
            cursor = db.rawQuery(query, null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return false;
            }
        } catch (Exception e) {
            cursor.close();
        }
        return true;
    }

    public boolean checkIfIntegerFieldExists(String TableName,
                                             String DBField, String fieldValue) {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            dOpenHelper = new DataBaseOpenHelper(context, DBConstants.DB_IPAY,
                    DATABASE_VERSION);
            db = dOpenHelper.getReadableDatabase();
            String query = "Select * from " + TableName + " where " + DBField + " = " + fieldValue;
            cursor = db.rawQuery(query, null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return false;
            }
        } catch (Exception e) {
            cursor.close();
        }
        return true;
    }

}