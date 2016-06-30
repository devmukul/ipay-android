package bd.com.ipay.ipayskeleton.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Model.Friend.FriendInfo;
import bd.com.ipay.ipayskeleton.Model.Friend.FriendNode;
import bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory.TransactionHistoryAdditionalInfo;
import bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory.TransactionHistoryClass;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class DataHelper {

    private static final int DATABASE_VERSION = 5;

    private Context context;
    private static DataHelper instance = null;
    private static DataBaseOpenHelper dOpenHelper;

    private DataHelper(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized DataHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DataHelper(context);
            dOpenHelper = new DataBaseOpenHelper(context, DBConstants.DB_IPAY,
                    DATABASE_VERSION);
        }
        return instance;
    }

    public void closeDbOpenHelper() {
        if (dOpenHelper != null) dOpenHelper.close();
        instance = null;
    }

    /**
     * Inserts a friends' information into the database.
     * If notifyChange is set to true, and if we use
     * registerContentObserver(cursor, DBConstants.DB_TABLE_SUBSCRIBERS_URI)
     * somewhere in the code where cursor points to the friends table, then the cursor will be
     * updated. As a rule of thumb, you should set it to true. But if you batch create friends,
     * then consider setting it to false and calling notifyChange after all friends have been
     * created.
     */
    public void createFriend(FriendNode friendNode) {

        try {
            ContentValues values = new ContentValues();
            values.put(DBConstants.KEY_MOBILE_NUMBER, friendNode.getPhoneNumber());
            values.put(DBConstants.KEY_NAME, friendNode.getInfo().getName());
            values.put(DBConstants.KEY_ACCOUNT_TYPE, friendNode.getInfo().getAccountType());
            values.put(DBConstants.KEY_PROFILE_PICTURE, friendNode.getInfo().getProfilePictureUrl());
            values.put(DBConstants.KEY_VERIFICATION_STATUS, friendNode.getInfo().isVerified() ?
                    DBConstants.VERIFIED_USER : DBConstants.NOT_VERIFIED_USER);
            values.put(DBConstants.KEY_UPDATE_TIME, friendNode.getInfo().getUpdateTime());
            values.put(DBConstants.KEY_IS_MEMBER, friendNode.getInfo().isMember() ?
                    DBConstants.IPAY_MEMBER : DBConstants.NOT_IPAY_MEMBER);

            SQLiteDatabase db = dOpenHelper.getWritableDatabase();
            db.insertWithOnConflict(DBConstants.DB_TABLE_FRIENDS, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createFriends(List<FriendNode> friendNodes) {
        if (friendNodes != null && !friendNodes.isEmpty()) {

            SQLiteDatabase db = dOpenHelper.getWritableDatabase();
            db.beginTransaction();

            try {
                for (FriendNode friendNode : friendNodes) {
                    ContentValues values = new ContentValues();
                    values.put(DBConstants.KEY_MOBILE_NUMBER, friendNode.getPhoneNumber());
                    values.put(DBConstants.KEY_NAME, friendNode.getInfo().getName());
                    values.put(DBConstants.KEY_ACCOUNT_TYPE, friendNode.getInfo().getAccountType());
                    values.put(DBConstants.KEY_PROFILE_PICTURE, friendNode.getInfo().getProfilePictureUrl());
                    values.put(DBConstants.KEY_VERIFICATION_STATUS, friendNode.getInfo().isVerified() ?
                            DBConstants.VERIFIED_USER : DBConstants.NOT_VERIFIED_USER);
                    values.put(DBConstants.KEY_UPDATE_TIME, friendNode.getInfo().getUpdateTime());
                    values.put(DBConstants.KEY_IS_MEMBER, friendNode.getInfo().isMember() ?
                            DBConstants.IPAY_MEMBER : DBConstants.NOT_IPAY_MEMBER);

                    db.insertWithOnConflict(DBConstants.DB_TABLE_FRIENDS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            db.setTransactionSuccessful();
            db.endTransaction();

            context.getContentResolver().notifyChange(DBConstants.DB_TABLE_SUBSCRIBERS_URI, null);

            Log.i("Friends", "Inserted into the database");
        }
    }

    public Cursor searchSubscribers(String query) {
        return searchSubscribers(query, false);
    }

    public Cursor searchSubscribers(String query, boolean verifiedOnly) {
        Cursor cursor = null;

        try {
            SQLiteDatabase db = dOpenHelper.getReadableDatabase();

            String queryString = "SELECT * FROM " + DBConstants.DB_TABLE_FRIENDS
                    + " WHERE " + DBConstants.KEY_NAME + " LIKE '%" + query + "%'";
            if (verifiedOnly)
                queryString += " AND " + DBConstants.KEY_VERIFICATION_STATUS + " = " + DBConstants.VERIFIED_USER;
            queryString += " ORDER BY " + DBConstants.KEY_IS_MEMBER + " DESC, "
                    + DBConstants.KEY_NAME + " COLLATE NOCASE";

            cursor = db.rawQuery(queryString, null);

            if (cursor != null) {
                cursor.getCount();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cursor;
    }

    public void updatePushEvents(String tagName, String jsonString) {

        try {
            SQLiteDatabase db = dOpenHelper.getReadableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(DBConstants.KEY_TAG_NAME, tagName);
            contentValues.put(DBConstants.KEY_JSON, jsonString);

            db.replace(DBConstants.DB_TABLE_PUSH_EVENTS, null, contentValues);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPushEvent(String tag) {
        Cursor cursor = null;

        try {
            SQLiteDatabase db = dOpenHelper.getReadableDatabase();

            String queryString = "SELECT * FROM " + DBConstants.DB_TABLE_PUSH_EVENTS
                    + " WHERE " + DBConstants.KEY_TAG_NAME + " = '" + tag + "'";
            cursor = db.rawQuery(queryString, null);

            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex(DBConstants.KEY_JSON));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public List<FriendNode> getFriendList(String query, boolean verifiedOnly) {
        Cursor cursor = searchSubscribers(query, verifiedOnly);
        List<FriendNode> friends = new ArrayList<>();

        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(DBConstants.KEY_NAME);
            int mobileNumberIndex = cursor.getColumnIndex(DBConstants.KEY_MOBILE_NUMBER);
            int profilePictureUrlIndex = cursor.getColumnIndex(DBConstants.KEY_PROFILE_PICTURE);
            int verificationStatusIndex = cursor.getColumnIndex(DBConstants.KEY_VERIFICATION_STATUS);
            int accountTypeIndex = cursor.getColumnIndex(DBConstants.KEY_ACCOUNT_TYPE);
            int updateTimeIndex = cursor.getColumnIndex(DBConstants.KEY_UPDATE_TIME);
            int isMemberIndex = cursor.getColumnIndex(DBConstants.KEY_IS_MEMBER);

            File dir = new File(Environment.getExternalStorageDirectory().getPath()
                    + Constants.PICTURE_FOLDER);
            if (!dir.exists()) dir.mkdir();

            do {
                String name = cursor.getString(nameIndex);
                String mobileNumber = cursor.getString(mobileNumberIndex);
                int verificationStatus = cursor.getInt(verificationStatusIndex);
                int accountType = cursor.getInt(accountTypeIndex);
                String profilePictureUrl = cursor.getString(profilePictureUrlIndex);
                long updateTime = cursor.getLong(updateTimeIndex);
                int isMember = cursor.getInt(isMemberIndex);

                FriendNode friend = new FriendNode(mobileNumber, new FriendInfo(accountType, isMember,
                        verificationStatus, name, updateTime, profilePictureUrl));
                friends.add(friend);
            } while (cursor.moveToNext());
        }

        return friends;
    }

    public List<FriendNode> getFriendList() {
        return getFriendList("", false);
    }

    public long getLastTransactionTime() {
        Cursor cursor = null;
        SQLiteDatabase db = null;

        try {

            db = dOpenHelper.getReadableDatabase();
            cursor = db.query(DBConstants.DB_TABLE_TRANSACTION_HISTORY, new String[] {DBConstants.KEY_TRANSACTION_HISTORY_RESPONSE_TIME}, null, null,
                    null, null, DBConstants.KEY_TRANSACTION_HISTORY_RESPONSE_TIME + " DESC", "1");
            if (cursor.moveToFirst()) {
                long time = cursor.getLong(cursor.getColumnIndex(DBConstants.KEY_TRANSACTION_HISTORY_RESPONSE_TIME));
                return time;
            }

        } finally {
            if (cursor != null)
                cursor.close();
        }

        return 0;
    }

    public List<TransactionHistoryClass> getAllTransactionHistory() {

        List<TransactionHistoryClass> transactionHistoryClasses = new ArrayList<>();
        Cursor cursor = null;
        SQLiteDatabase db = null;

        try {

            db = dOpenHelper.getReadableDatabase();
            cursor = db.query(DBConstants.DB_TABLE_TRANSACTION_HISTORY, null, null, null,
                    null, null, DBConstants.KEY_TRANSACTION_HISTORY_RESPONSE_TIME + " DESC");

            int transactionIdIndex = cursor.getColumnIndex(DBConstants.KEY_TRANSACTION_HISTORY_TRANSACTION_ID);
            int originatingMobileNumberIndex = cursor.getColumnIndex(DBConstants.KEY_TRANSACTION_HISTORY_ORIGINATING_MOBILE_NUMBER);
            int receiverInfoIndex = cursor.getColumnIndex(DBConstants.KEY_TRANSACTION_HISTORY_RECEIVER_INFO);
            int amountIndex = cursor.getColumnIndex(DBConstants.KEY_TRANSACTION_HISTORY_AMOUNT);
            int feeIndex = cursor.getColumnIndex(DBConstants.KEY_TRANSACTION_HISTORY_FEE);
            int netAmountIndex = cursor.getColumnIndex(DBConstants.KEY_TRANSACTION_HISTORY_NET_AMOUNT);
            int balanceIndex = cursor.getColumnIndex(DBConstants.KEY_TRANSACTION_HISTORY_BALANCE);
            int serviceIdIndex = cursor.getColumnIndex(DBConstants.KEY_TRANSACTION_HISTORY_SERVICE_ID);
            int statusCodeIndex = cursor.getColumnIndex(DBConstants.KEY_TRANSACTION_HISTORY_STATUS_CODE);
            int purposeIndex = cursor.getColumnIndex(DBConstants.KEY_TRANSACTION_HISTORY_PURPOSE);
            int statusDescriptionIndex = cursor.getColumnIndex(DBConstants.KEY_TRANSACTION_HISTORY_STATUS_DESCRIPTION);
            int descriptionIndex = cursor.getColumnIndex(DBConstants.KEY_TRANSACTION_HISTORY_DESCRIPTION);
            int timeIndex = cursor.getColumnIndex(DBConstants.KEY_TRANSACTION_HISTORY_TIME);
            int requestTimeIndex = cursor.getColumnIndex(DBConstants.KEY_TRANSACTION_HISTORY_REQUEST_TIME);
            int responseTimeIndex = cursor.getColumnIndex(DBConstants.KEY_TRANSACTION_HISTORY_RESPONSE_TIME);

            int userNameIndex = cursor.getColumnIndex(DBConstants.KEY_TRANSACTION_HISTORY_USER_NAME);
            int userMobileNumberIndex = cursor.getColumnIndex(DBConstants.KEY_TRANSACTION_HISTORY_USER_MOBILE_NUMBER);
            int userProfilePicIndex = cursor.getColumnIndex(DBConstants.KEY_TRANSACTION_HISTORY_USER_PROFILE_PIC);
            int bankAccountNumberIndex = cursor.getColumnIndex(DBConstants.KEY_TRANSACTION_HISTORY_BANK_ACCOUNT_NUMBER);
            int bankAccountNameIndex = cursor.getColumnIndex(DBConstants.KEY_TRANSACTION_HISTORY_BANK_ACCOUNT_NAME);
            int bankNameIndex = cursor.getColumnIndex(DBConstants.KEY_TRANSACTION_HISTORY_BANK_NAME);
            int bankBranchNameIndex = cursor.getColumnIndex(DBConstants.KEY_TRANSACTION_HISTORY_BRANCH_NAME);

            if (cursor.moveToFirst()) {
                do {
                    TransactionHistoryAdditionalInfo additionalInfo = new TransactionHistoryAdditionalInfo(
                            cursor.getString(userNameIndex),
                            cursor.getString(userMobileNumberIndex),
                            cursor.getString(userProfilePicIndex),
                            cursor.getString(bankAccountNumberIndex),
                            cursor.getString(bankAccountNameIndex),
                            cursor.getString(bankNameIndex),
                            cursor.getString(bankBranchNameIndex)
                    );

                    TransactionHistoryClass transactionHistoryClass = new TransactionHistoryClass(
                            cursor.getString(originatingMobileNumberIndex),
                            cursor.getString(receiverInfoIndex),
                            cursor.getDouble(amountIndex),
                            cursor.getDouble(feeIndex),
                            cursor.getDouble(netAmountIndex),
                            cursor.getDouble(balanceIndex),
                            cursor.getInt(serviceIdIndex),
                            cursor.getInt(statusCodeIndex),
                            cursor.getString(purposeIndex),
                            cursor.getString(statusDescriptionIndex),
                            cursor.getString(descriptionIndex),
                            cursor.getString(transactionIdIndex),
                            cursor.getLong(timeIndex),
                            cursor.getLong(requestTimeIndex),
                            cursor.getLong(responseTimeIndex),
                            additionalInfo
                    );

                    transactionHistoryClasses.add(transactionHistoryClass);
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return transactionHistoryClasses;
    }

    public void createTransactionHistories(List<TransactionHistoryClass> transactionHistoryClasses) {
        if (transactionHistoryClasses == null)
            return;

        try {

            SQLiteDatabase db = dOpenHelper.getWritableDatabase();
            db.beginTransaction();

            for (TransactionHistoryClass transactionHistoryClass : transactionHistoryClasses) {
                ContentValues contentValues = new ContentValues();

                contentValues.put(DBConstants.KEY_TRANSACTION_HISTORY_TRANSACTION_ID, transactionHistoryClass.getTransactionID());
                contentValues.put(DBConstants.KEY_TRANSACTION_HISTORY_ORIGINATING_MOBILE_NUMBER, transactionHistoryClass.getOriginatingMobileNumber());
                contentValues.put(DBConstants.KEY_TRANSACTION_HISTORY_RECEIVER_INFO, transactionHistoryClass.getReceiverInfo());
                contentValues.put(DBConstants.KEY_TRANSACTION_HISTORY_AMOUNT, transactionHistoryClass.getAmount());
                contentValues.put(DBConstants.KEY_TRANSACTION_HISTORY_FEE, transactionHistoryClass.getFee());
                contentValues.put(DBConstants.KEY_TRANSACTION_HISTORY_NET_AMOUNT, transactionHistoryClass.getNetAmount());
                contentValues.put(DBConstants.KEY_TRANSACTION_HISTORY_BALANCE, transactionHistoryClass.getBalance());
                contentValues.put(DBConstants.KEY_TRANSACTION_HISTORY_SERVICE_ID, transactionHistoryClass.getServiceID());
                contentValues.put(DBConstants.KEY_TRANSACTION_HISTORY_STATUS_CODE, transactionHistoryClass.getStatusCode());
                contentValues.put(DBConstants.KEY_TRANSACTION_HISTORY_PURPOSE, transactionHistoryClass.getPurpose());
                contentValues.put(DBConstants.KEY_TRANSACTION_HISTORY_STATUS_DESCRIPTION, transactionHistoryClass.getStatusDescription());
                contentValues.put(DBConstants.KEY_TRANSACTION_HISTORY_DESCRIPTION, transactionHistoryClass.getDescription());
                contentValues.put(DBConstants.KEY_TRANSACTION_HISTORY_TIME, transactionHistoryClass.getTime());
                contentValues.put(DBConstants.KEY_TRANSACTION_HISTORY_REQUEST_TIME, transactionHistoryClass.getTime());
                contentValues.put(DBConstants.KEY_TRANSACTION_HISTORY_RESPONSE_TIME, transactionHistoryClass.getResponseTime());

                contentValues.put(DBConstants.KEY_TRANSACTION_HISTORY_USER_NAME, transactionHistoryClass.getAdditionalInfo().getUserName());
                contentValues.put(DBConstants.KEY_TRANSACTION_HISTORY_USER_MOBILE_NUMBER, transactionHistoryClass.getAdditionalInfo().getUserMobileNumber());
                contentValues.put(DBConstants.KEY_TRANSACTION_HISTORY_USER_PROFILE_PIC, transactionHistoryClass.getAdditionalInfo().getUserProfilePic());
                contentValues.put(DBConstants.KEY_TRANSACTION_HISTORY_BANK_ACCOUNT_NUMBER, transactionHistoryClass.getAdditionalInfo().getBankAccountNumber());
                contentValues.put(DBConstants.KEY_TRANSACTION_HISTORY_BANK_ACCOUNT_NAME, transactionHistoryClass.getAdditionalInfo().getBankAccountName());
                contentValues.put(DBConstants.KEY_TRANSACTION_HISTORY_BANK_NAME, transactionHistoryClass.getAdditionalInfo().getBankName());
                contentValues.put(DBConstants.KEY_TRANSACTION_HISTORY_BRANCH_NAME, transactionHistoryClass.getAdditionalInfo().getBranchName());

                db.insertWithOnConflict(DBConstants.DB_TABLE_TRANSACTION_HISTORY, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            }

            db.setTransactionSuccessful();
            db.endTransaction();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkIfStringFieldExists(String tableName,
                                            String DBField, String fieldValue) {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
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