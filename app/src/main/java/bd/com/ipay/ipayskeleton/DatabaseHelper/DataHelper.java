package bd.com.ipay.ipayskeleton.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Model.Contact.ContactNode;
import bd.com.ipay.ipayskeleton.Model.SqLiteDatabase.BusinessAccountEntry;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class DataHelper {

    private static final int DATABASE_VERSION = 11;

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

    public void createContacts(List<ContactNode> contactList) {
        if (contactList != null && !contactList.isEmpty()) {

            SQLiteDatabase db = dOpenHelper.getWritableDatabase();
            db.beginTransaction();

            try {
                for (ContactNode contactNode : contactList) {
                    ContentValues values = new ContentValues();
                    values.put(DBConstants.KEY_MOBILE_NUMBER, contactNode.getMobileNumber());
                    values.put(DBConstants.KEY_NAME, contactNode.getName());
                    values.put(DBConstants.KEY_ORIGINAL_NAME, contactNode.getOriginalName());
                    values.put(DBConstants.KEY_ACCOUNT_TYPE, contactNode.getAccountType());
                    values.put(DBConstants.KEY_PROFILE_PICTURE_QUALITY_MEDIUM, contactNode.getProfilePictureUrlMedium());
                    values.put(DBConstants.KEY_PROFILE_PICTURE_QUALITY_HIGH, contactNode.getProfilePictureUrlHigh());
                    values.put(DBConstants.KEY_RELATIONSHIP, contactNode.getRelationship());
                    values.put(DBConstants.KEY_VERIFICATION_STATUS, contactNode.isVerified() ?
                            DBConstants.VERIFIED_USER : DBConstants.NOT_VERIFIED_USER);
                    values.put(DBConstants.KEY_UPDATE_TIME, contactNode.getUpdateTime());
                    values.put(DBConstants.KEY_IS_MEMBER, contactNode.isMember() ?
                            DBConstants.IPAY_MEMBER : DBConstants.NOT_IPAY_MEMBER);
                    values.put(DBConstants.KEY_IS_ACTIVE, contactNode.isActive() ?
                            DBConstants.ACTIVE : DBConstants.INACTIVE);

                    db.insertWithOnConflict(DBConstants.DB_TABLE_CONTACTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            db.setTransactionSuccessful();
            db.endTransaction();

            context.getContentResolver().notifyChange(DBConstants.DB_TABLE_CONTACTS_URI, null);

            if (Constants.DEBUG) Log.i("Contacts", "Inserted into the database");
        }
    }

    public void createBusinessAccountsList(List<BusinessAccountEntry> businessAccountEntries) {
        if (businessAccountEntries != null && !businessAccountEntries.isEmpty()) {

            SQLiteDatabase db = dOpenHelper.getWritableDatabase();
            db.beginTransaction();

            try {
                for (BusinessAccountEntry mBusinessAccountEntry : businessAccountEntries) {
                    ContentValues values = new ContentValues();
                    values.put(DBConstants.KEY_BUSINESS_MOBILE_NUMBER, mBusinessAccountEntry.getMobileNumber());
                    values.put(DBConstants.KEY_BUSINESS_NAME, mBusinessAccountEntry.getBusinessName());
                    values.put(DBConstants.BUSINESS_EMAIL, mBusinessAccountEntry.getEmail());
                    values.put(DBConstants.KEY_BUSINESS_TYPE, mBusinessAccountEntry.getBusinessType());
                    values.put(DBConstants.KEY_BUSINESS_PROFILE_PICTURE, mBusinessAccountEntry.getProfilePictureUrl());
                    values.put(DBConstants.KEY_BUSINESS_PROFILE_PICTURE_QUALITY_MEDIUM, mBusinessAccountEntry.getProfilePictureUrlMedium());
                    values.put(DBConstants.KEY_BUSINESS_PROFILE_PICTURE_QUALITY_HIGH, mBusinessAccountEntry.getProfilePictureUrlHigh());
                    values.put(DBConstants.KEY_BUSINESS_ACCOUNT_ID, mBusinessAccountEntry.getBusinessId());

                    db.insertWithOnConflict(DBConstants.DB_TABLE_BUSINESS_ACCOUNTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            db.setTransactionSuccessful();
            db.endTransaction();

            context.getContentResolver().notifyChange(DBConstants.DB_TABLE_BUSINESS_URI, null);

            if (Constants.DEBUG) Log.i("Business", "Inserted into the database");
        }
    }

    public Cursor searchContacts(String query) {
        return searchContacts(query, false, false, false);
    }

    public Cursor searchContacts(String query, boolean memberOnly, boolean businessMemberOnly, boolean verifiedOnly) {
        return searchContacts(query, memberOnly, businessMemberOnly, false, verifiedOnly, false, false, null);
    }

    public Cursor searchContacts(String query, boolean memberOnly, boolean businessMemberOnly, boolean nonMemberOnly,
                                 boolean verifiedOnly, boolean invitedOnly, boolean nonInvitedOnly, List<String> invitees) {
        Cursor cursor = null;

        try {
            SQLiteDatabase db = dOpenHelper.getReadableDatabase();

            String queryString = "SELECT * FROM " + DBConstants.DB_TABLE_CONTACTS
                    + " WHERE (" + DBConstants.KEY_NAME + " LIKE '%" + query + "%'"
                    + " OR " + DBConstants.KEY_MOBILE_NUMBER + " LIKE '%" + query + "%'"
                    + " OR " + DBConstants.KEY_ORIGINAL_NAME + " LIKE '%" + query + "%')";

            // Get Verified Users
            if (verifiedOnly)
                queryString += " AND " + DBConstants.KEY_VERIFICATION_STATUS + " = " + DBConstants.VERIFIED_USER;

            // Get iPay Users
            if (memberOnly)
                queryString += " AND " + DBConstants.KEY_IS_MEMBER + " = " + DBConstants.IPAY_MEMBER;

            // Get iPay Business Users
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
            // Select only active contacts
            queryString += " AND " + DBConstants.KEY_IS_ACTIVE + " = " + DBConstants.ACTIVE;

            // If original name exists, then user original name as the sorting parameter.
            // Otherwise use normal name as the sorting parameter.
            queryString += " ORDER BY CASE "
                    + "WHEN (" + DBConstants.KEY_ORIGINAL_NAME + " IS NULL OR " + DBConstants.KEY_ORIGINAL_NAME + " = '')"
                    + " THEN " + DBConstants.KEY_NAME
                    + " ELSE "
                    + DBConstants.KEY_ORIGINAL_NAME + " END COLLATE NOCASE";

            if (Constants.DEBUG)
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

    public Cursor searchBusinessContacts(String query) {
        Cursor cursor = null;

        try {
            SQLiteDatabase db = dOpenHelper.getReadableDatabase();

            String queryString = "SELECT * FROM " + DBConstants.DB_TABLE_BUSINESS_ACCOUNTS
                    + " WHERE (" + DBConstants.KEY_BUSINESS_NAME + " LIKE '%" + query + "%'"
                    + " OR " + DBConstants.KEY_BUSINESS_MOBILE_NUMBER + " LIKE '%"
                    + query + "%'" + ")" + " ORDER BY " + DBConstants.KEY_BUSINESS_NAME
                    + " COLLATE NOCASE";

            if (Constants.DEBUG)
                Log.w("Query", queryString);

            cursor = db.rawQuery(queryString, null);

            if (cursor != null) {
                cursor.getCount();
                if (Constants.DEBUG)
                    Log.w("Query", cursor.getCount() + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cursor;
    }

    public int getLastAddedBusinessId() {
        Cursor cursor = null;
        int columnIndexForMaxBusinessId = 0;

        try {
            SQLiteDatabase db = dOpenHelper.getReadableDatabase();

            String queryString = "SELECT MAX(" + DBConstants.KEY_BUSINESS_ACCOUNT_ID +
                    ") FROM " + DBConstants.DB_TABLE_BUSINESS_ACCOUNTS;

            if (Constants.DEBUG)
                Log.w("Query", queryString);

            cursor = db.rawQuery(queryString, null);

            if (cursor != null) {
                cursor.moveToFirst();
                return cursor.getInt(columnIndexForMaxBusinessId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
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
        Cursor cursor;

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


    private List<ContactNode> getContactList(String query, boolean memberOnly, boolean businessMemberOnly, boolean verifiedOnly) {
        Cursor cursor = searchContacts(query, memberOnly, businessMemberOnly, verifiedOnly);
        List<ContactNode> contacts = new ArrayList<>();

        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(DBConstants.KEY_NAME);
            int originalNameIndex = cursor.getColumnIndex(DBConstants.KEY_ORIGINAL_NAME);
            int mobileNumberIndex = cursor.getColumnIndex(DBConstants.KEY_MOBILE_NUMBER);
            int profilePictureUrlIndex = cursor.getColumnIndex(DBConstants.KEY_PROFILE_PICTURE);
            int profilePictureUrlQualityMediumIndex = cursor.getColumnIndex(DBConstants.KEY_PROFILE_PICTURE_QUALITY_MEDIUM);
            int profilePictureUrlQualityHighIndex = cursor.getColumnIndex(DBConstants.KEY_PROFILE_PICTURE_QUALITY_HIGH);
            int verificationStatusIndex = cursor.getColumnIndex(DBConstants.KEY_VERIFICATION_STATUS);
            int relationshipIndex = cursor.getColumnIndex(DBConstants.KEY_RELATIONSHIP);
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
                String relationship = cursor.getString(relationshipIndex);
                long updateTime = cursor.getLong(updateTimeIndex);
                int isMember = cursor.getInt(isMemberIndex);

                ContactNode contactNode = new ContactNode(accountType, isMember,
                        verificationStatus, name, originalName, mobileNumber, profilePictureUrl, profilePictureUrlQualityMedium, profilePictureUrlQualityHigh, relationship, updateTime);
                contacts.add(contactNode);
            } while (cursor.moveToNext());
        }

        return contacts;
    }

    public List<ContactNode> getContactList() {
        return getContactList("", false, false, false);
    }

}