package bd.com.ipay.ipayskeleton.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Model.Friend.FriendInfo;
import bd.com.ipay.ipayskeleton.Model.Friend.FriendNode;

public class DataHelper {

    private static final int DATABASE_VERSION = 8;

    private final Context context;
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

    public void createFriend(FriendNode friendNode) {

        try {
            ContentValues values = new ContentValues();
            values.put(DBConstants.KEY_MOBILE_NUMBER, friendNode.getPhoneNumber());
            values.put(DBConstants.KEY_NAME, friendNode.getInfo().getName());
            values.put(DBConstants.KEY_ORIGINAL_NAME, friendNode.getInfo().getOriginalName());
            values.put(DBConstants.KEY_ACCOUNT_TYPE, friendNode.getInfo().getAccountType());
            values.put(DBConstants.KEY_PROFILE_PICTURE, friendNode.getInfo().getProfilePictureUrl());
            values.put(DBConstants.KEY_PROFILE_PICTURE_QUALITY_MEDIUM, friendNode.getInfo().getProfilePictureUrlMedium());
            values.put(DBConstants.KEY_PROFILE_PICTURE_QUALITY_HIGH, friendNode.getInfo().getProfilePictureUrlHigh());
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
                    values.put(DBConstants.KEY_ORIGINAL_NAME, friendNode.getInfo().getOriginalName());
                    values.put(DBConstants.KEY_ACCOUNT_TYPE, friendNode.getInfo().getAccountType());
                    values.put(DBConstants.KEY_PROFILE_PICTURE, friendNode.getInfo().getProfilePictureUrl());
                    values.put(DBConstants.KEY_PROFILE_PICTURE_QUALITY_MEDIUM, friendNode.getInfo().getProfilePictureUrlMedium());
                    values.put(DBConstants.KEY_PROFILE_PICTURE_QUALITY_HIGH, friendNode.getInfo().getProfilePictureUrlHigh());
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

            context.getContentResolver().notifyChange(DBConstants.DB_TABLE_FRIENDS_URI, null);

            Log.i("Friends", "Inserted into the database");
        }
    }

    public Cursor searchFriends(String query) {
        return searchFriends(query, false, false, false);
    }

    public Cursor searchFriends(String query, boolean memberOnly,boolean businessMemberOnly, boolean verifiedOnly) {
        return searchFriends(query, memberOnly, businessMemberOnly, false, verifiedOnly, false, false, null);
    }

    public Cursor searchFriends(String query, boolean memberOnly, boolean businessMemberOnly, boolean nonMemberOnly,
                            boolean verifiedOnly, boolean invitedOnly, boolean nonInvitedOnly, List<String> invitees) {
        Cursor cursor = null;

        try {
            SQLiteDatabase db = dOpenHelper.getReadableDatabase();

            String queryString = "SELECT * FROM " + DBConstants.DB_TABLE_FRIENDS
                    + " WHERE (" + DBConstants.KEY_NAME + " LIKE '%" + query + "%'"
                    + " OR " + DBConstants.KEY_MOBILE_NUMBER + " LIKE '%" + query + "%')";

            // Get Verified Users
            if (verifiedOnly)
                queryString += " AND " + DBConstants.KEY_VERIFICATION_STATUS + " = " + DBConstants.VERIFIED_USER;

            // Get iPay Users
            if (memberOnly)
                queryString += " AND " + DBConstants.KEY_IS_MEMBER + " = " + DBConstants.IPAY_MEMBER;

            // Get iPay Users
            if (businessMemberOnly)
                queryString += " AND " + DBConstants.KEY_IS_MEMBER + " = " + DBConstants.IPAY_MEMBER + " AND " + DBConstants.KEY_ACCOUNT_TYPE + " = " + DBConstants.BUSINESS_USER;

            // Get Non-iPay Users
            if (nonMemberOnly)
                queryString += " AND " + DBConstants.KEY_IS_MEMBER + " != " + DBConstants.IPAY_MEMBER;

            if (invitees != null) {
                List<String> quotedInvitees = new ArrayList<>();
                for (String invitee : invitees) {
                    quotedInvitees.add("'" + invitee + "'");
                }

                String inviteeListStr = "(" + TextUtils.join(", ", quotedInvitees) + ")";

                if (invitedOnly) {
                    // Get invited users
                    queryString += " AND " + DBConstants.KEY_MOBILE_NUMBER + " IN " + inviteeListStr;
                }
                
                if (nonInvitedOnly) {
                    // Get invited users
                    queryString += " AND " + DBConstants.KEY_MOBILE_NUMBER + " NOT IN " + inviteeListStr;
                }
            }

            // If original name exists, then user original name as the sorting parameter.
            // Otherwise use normal name as the sorting parameter.
            queryString += " ORDER BY CASE "
                    + "WHEN (" + DBConstants.KEY_ORIGINAL_NAME + " IS NULL OR " + DBConstants.KEY_ORIGINAL_NAME + " = '')"
                    + " THEN " + DBConstants.KEY_NAME
                    + " ELSE "
                    + DBConstants.KEY_ORIGINAL_NAME + " END COLLATE NOCASE";

            Log.w("Query", queryString);

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
                String pushEvent = cursor.getString(cursor.getColumnIndex(DBConstants.KEY_JSON));
                cursor.close();
                return pushEvent;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    private List<FriendNode> getFriendList(String query, boolean memberOnly, boolean businessMemberOnly, boolean verifiedOnly) {
        Cursor cursor = searchFriends(query, memberOnly, businessMemberOnly, verifiedOnly);
        List<FriendNode> friends = new ArrayList<>();

        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(DBConstants.KEY_NAME);
            int originalNameIndex = cursor.getColumnIndex(DBConstants.KEY_ORIGINAL_NAME);
            int mobileNumberIndex = cursor.getColumnIndex(DBConstants.KEY_MOBILE_NUMBER);
            int profilePictureUrlIndex = cursor.getColumnIndex(DBConstants.KEY_PROFILE_PICTURE);
            int profilePictureUrlQualityMediumIndex = cursor.getColumnIndex(DBConstants.KEY_PROFILE_PICTURE_QUALITY_MEDIUM);
            int profilePictureUrlQualityHighIndex = cursor.getColumnIndex(DBConstants.KEY_PROFILE_PICTURE_QUALITY_HIGH);
            int verificationStatusIndex = cursor.getColumnIndex(DBConstants.KEY_VERIFICATION_STATUS);
            int accountTypeIndex = cursor.getColumnIndex(DBConstants.KEY_ACCOUNT_TYPE);
            int updateTimeIndex = cursor.getColumnIndex(DBConstants.KEY_UPDATE_TIME);
            int isMemberIndex = cursor.getColumnIndex(DBConstants.KEY_IS_MEMBER);

            do {
                String name = cursor.getString(nameIndex);
                String originalName = cursor.getString(originalNameIndex);
                String mobileNumber = cursor.getString(mobileNumberIndex);
                int verificationStatus = cursor.getInt(verificationStatusIndex);
                int accountType = cursor.getInt(accountTypeIndex);
                String profilePictureUrl = cursor.getString(profilePictureUrlIndex);
                String profilePictureUrlQualityMedium = cursor.getString(profilePictureUrlQualityMediumIndex);
                String profilePictureUrlQualityHigh = cursor.getString(profilePictureUrlQualityHighIndex);
                long updateTime = cursor.getLong(updateTimeIndex);
                int isMember = cursor.getInt(isMemberIndex);

                FriendNode friend = new FriendNode(mobileNumber, new FriendInfo(accountType, isMember,
                        verificationStatus, name, originalName, profilePictureUrl, profilePictureUrlQualityMedium, profilePictureUrlQualityHigh, updateTime));
                friends.add(friend);
            } while (cursor.moveToNext());
        }

        return friends;
    }

    public List<FriendNode> getFriendList() {
        return getFriendList("", false, false, false);
    }

}