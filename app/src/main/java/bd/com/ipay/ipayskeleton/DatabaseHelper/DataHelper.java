package bd.com.ipay.ipayskeleton.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Model.Friend.FriendInfo;
import bd.com.ipay.ipayskeleton.Model.Friend.FriendNode;
import bd.com.ipay.ipayskeleton.Model.SqLiteDatabase.SubscriberEntry;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

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

            ContentValues values = new ContentValues();
            values.put(DBConstants.KEY_MOBILE_NUMBER, mSubscriberEntry.getMobileNumber());
            values.put(DBConstants.KEY_NAME, mSubscriberEntry.getName());
            values.put(DBConstants.KEY_ACCOUNT_TYPE, mSubscriberEntry.getAccountType());
            values.put(DBConstants.KEY_PROFILE_PICTURE, mSubscriberEntry.getProfilePicture());
            values.put(DBConstants.KEY_VERIFICATION_STATUS, mSubscriberEntry.getIsVerified());

            db = dOpenHelper.getWritableDatabase();
            db.insert(DBConstants.DB_TABLE_SUBSCRIBERS, null, values);
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

    public Cursor searchSubscribers(String query) {
        return searchSubscribers(query, false);
    }

    public Cursor searchSubscribers(String query, boolean verifiedOnly) {
        Cursor cursor = null;

        try {
            dOpenHelper = new DataBaseOpenHelper(context, DBConstants.DB_IPAY,
                    DATABASE_VERSION);
            SQLiteDatabase db = dOpenHelper.getReadableDatabase();

            String queryString = "SELECT * FROM " + DBConstants.DB_TABLE_SUBSCRIBERS;
            if (query != null && !query.isEmpty())
                    queryString += " WHERE " + DBConstants.KEY_NAME + " LIKE '%" + query + "%'";
            if (verifiedOnly)
                queryString += " AND " + DBConstants.KEY_VERIFICATION_STATUS + " = " + DBConstants.VERIFIED_USER;
            queryString += " ORDER BY " + DBConstants.KEY_NAME + " COLLATE NOCASE";
            cursor = db.rawQuery(queryString, null);

            if (cursor != null) {
                cursor.getCount();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cursor;
    }

    public List<FriendNode> getSubscriberList(String query, boolean verifiedOnly) {
        Cursor cursor = searchSubscribers(query, verifiedOnly);
        List<FriendNode> friends = new ArrayList<>();

        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(DBConstants.KEY_NAME);
            int mobileNumberIndex = cursor.getColumnIndex(DBConstants.KEY_MOBILE_NUMBER);

            int verificationStatusIndex = cursor.getColumnIndex(DBConstants.KEY_VERIFICATION_STATUS);
            int accountTypeIndex = cursor.getColumnIndex(DBConstants.KEY_ACCOUNT_TYPE);

            File dir = new File(Environment.getExternalStorageDirectory().getPath()
                    + Constants.PICTURE_FOLDER);
            if (!dir.exists()) dir.mkdir();

            while (cursor.moveToNext()) {
                String name = cursor.getString(nameIndex);
                String mobileNumber = cursor.getString(mobileNumberIndex);
                int verificationStatus = cursor.getInt(verificationStatusIndex);
                int accountType = cursor.getInt(accountTypeIndex);
                String profilePictureUrl = null;

                File file = new File(dir, mobileNumber.replaceAll("[^0-9]", "") + ".jpg");
                if (file.exists()) {
                    profilePictureUrl = file.getAbsolutePath();
                }

                FriendNode friend = new FriendNode(mobileNumber, new FriendInfo(accountType, true,
                        verificationStatus, name, profilePictureUrl));
                friends.add(friend);
            }
        }

        return friends;
    }

    public List<FriendNode> getSubscriberList() {
        return getSubscriberList("", false);
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