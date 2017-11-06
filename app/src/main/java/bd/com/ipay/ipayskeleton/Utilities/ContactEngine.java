package bd.com.ipay.ipayskeleton.Utilities;


import android.annotation.SuppressLint;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.RawContacts;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bd.com.ipay.ipayskeleton.BuildConfig;
import bd.com.ipay.ipayskeleton.Model.Contact.ContactNode;
import bd.com.ipay.ipayskeleton.Model.Contact.PhoneName;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefManager;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Logger;

public class ContactEngine {
    private static final String TAG = "ContactEngine";

    public static ArrayList<String> getContactNames(Context context) {
        // Run query
        Uri uri = Contacts.CONTENT_URI;
        String[] projection = new String[]{Contacts._ID,
                Contacts.DISPLAY_NAME};
        String selection = Contacts.HAS_PHONE_NUMBER
                + " = '1'";
        String[] selectionArgs = null;
        String sortOrder = Contacts.DISPLAY_NAME
                + " COLLATE LOCALIZED ASC";

        ArrayList<String> names = new ArrayList<>();
        if (context == null)
            return names;
        Cursor cursor = context.getContentResolver().query(uri, projection,
                selection, selectionArgs, sortOrder);
        if (cursor != null) {
            int nameIndex = cursor
                    .getColumnIndex(Contacts.DISPLAY_NAME);
            while (cursor.moveToNext()) {
                names.add(cursor.getString(nameIndex));
            }
            cursor.close();
        }
        return names;
    }

    public static ArrayList<String> getContactNumbers(Context context) {
        ArrayList<String> numbers = new ArrayList<>();
        if (context == null)
            return numbers;
        Cursor cursor = context.getContentResolver().query(Phone.CONTENT_URI, null, null, null, null);

        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(Phone.NUMBER);
            while (cursor.moveToNext()) {
                numbers.add(cursor.getString(nameIndex).replaceAll("\\D", ""));
            }
            cursor.close();
        }
        return numbers;
    }


    public static Cursor getContactNamesCursor(Context context) {
        // Run query
        Uri uri = Contacts.CONTENT_URI;
        String[] projection = new String[]{Contacts._ID,
                Contacts.DISPLAY_NAME};
        String selection = Contacts.HAS_PHONE_NUMBER
                + " = '1'";
        String[] selectionArgs = null;
        String sortOrder = Contacts.DISPLAY_NAME
                + " COLLATE LOCALIZED ASC";
        return context.getContentResolver().query(uri, projection,
                selection, selectionArgs, sortOrder);
    }

    public static Cursor SearchContactNamesCursor(Context context,
                                                  String contact) {
        // Run query
        Uri uri = Uri.withAppendedPath(
                Contacts.CONTENT_FILTER_URI, contact);
        String[] projection = new String[]{Contacts._ID,
                Contacts.DISPLAY_NAME};
        String selection = Contacts.HAS_PHONE_NUMBER
                + " = '1'";
        String[] selectionArgs = null;
        String sortOrder = Contacts.DISPLAY_NAME
                + " COLLATE LOCALIZED ASC";

        return context.getContentResolver().query(uri, projection,
                selection, selectionArgs, sortOrder);
    }

    public static Cursor getAllFavourite(Context context, String searchStr) {

        Uri uri = Uri.withAppendedPath(
                Contacts.CONTENT_FILTER_URI, searchStr);
        String[] projection = new String[]{
                Contacts.LOOKUP_KEY,
                Contacts.DISPLAY_NAME,
                Contacts._ID};

        String selection = "starred=1";

        return context.getContentResolver().query(uri, projection,
                selection, null, null);
    }

    public static Cursor getAllFavourite(Context context) {
        String[] projection = new String[]{
                Contacts.LOOKUP_KEY,
                Contacts.DISPLAY_NAME,
                Contacts._ID};

        return context.getContentResolver().query(
                Contacts.CONTENT_URI, projection, "starred=?",
                new String[]{"1"}, null);
    }

    public static boolean isFavourite(Context context, String ContactID) {
        boolean result = false;
        String[] projection = new String[]{Contacts._ID,
                Contacts.DISPLAY_NAME};
        try {
            Cursor cursor = context.getContentResolver().query(
                    Contacts.CONTENT_URI, projection, "starred=?",
                    new String[]{"1"}, null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        if (cursor.getString(
                                cursor.getColumnIndex(Contacts._ID))
                                .equals(ContactID)) {
                            result = true;
                            break;
                        }
                    }
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static void addContactToAccount1(Context context,
                                             String accountName, String accountType, String name, String number) {
        if (BuildConfig.DEBUG)
            Logger.logI(TAG, "Adding contact: " + name);
        ArrayList<ContentProviderOperation> operationList = new ArrayList<>();

        ContentProviderOperation.Builder builder = ContentProviderOperation
                .newInsert(RawContacts.CONTENT_URI);
        builder.withValue(RawContacts.ACCOUNT_NAME, accountName);
        builder.withValue(RawContacts.ACCOUNT_TYPE, accountType);
        builder.withValue(RawContacts.SYNC1, number);
        operationList.add(builder.build());

        builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
        builder.withValueBackReference(ContactsContract.CommonDataKinds.StructuredName.RAW_CONTACT_ID, 0);
        builder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        builder.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name);
        operationList.add(builder.build());

        builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
        builder.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0);
        builder.withValue(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
        builder.withValue(ContactsContract.Data.DATA1, "+" + number);
        builder.withValue(ContactsContract.Data.DATA2, Phone.TYPE_MOBILE);
        operationList.add(builder.build());

        builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
        builder.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0);
        builder.withValue(ContactsContract.Data.MIMETYPE, "vnd.android.cursor.item/com.zipt.zipt.mimetype_call");
        builder.withValue(ContactsContract.Data.DATA1, number);
        builder.withValue(ContactsContract.Data.DATA2, "Free Call");
        builder.withValue(ContactsContract.Data.DATA3, "Free Call (" + number + ")");
        operationList.add(builder.build());

        builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
        builder.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0);
        builder.withValue(ContactsContract.Data.MIMETYPE, "vnd.android.cursor.item/com.zipt.zipt.mimetype_im");
        builder.withValue(ContactsContract.Data.DATA1, number);
        builder.withValue(ContactsContract.Data.DATA2, "iTel IM");
        builder.withValue(ContactsContract.Data.DATA3, "IM (" + number + ")");
        operationList.add(builder.build());

        try {
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY,
                    operationList);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void updateOrInsertContact(Context context, String accountName, String accountType, String name, String number) {
        if (BuildConfig.DEBUG)
            Logger.logI("ContactEngine", "Searching DBContactNode: Name: " + name + " Number: " + number);

        int id = -1;
        Cursor cursor = context.getContentResolver().query(
                RawContacts.CONTENT_URI,

                new String[]{RawContacts._ID,
                        RawContacts.ACCOUNT_NAME,
                        RawContacts.ACCOUNT_TYPE,
                        RawContacts.CONTACT_ID,
                        RawContacts.SYNC1},

                RawContacts.ACCOUNT_NAME + "= ? and "
                        + RawContacts.ACCOUNT_TYPE + "= ? and "
                        + RawContacts.SYNC1 + "= ?",

                new String[]{accountName, accountType, number},

                null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor
                        .getColumnIndex(RawContacts.CONTACT_ID));
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        if (id != -1) {
            if (BuildConfig.DEBUG)
                Logger.logI("ContactEngine", "DBContactNode Already exists!! RawContactID: " + id);
            /*
             * ArrayList<ContentProviderOperation> operationList = new
			 * ArrayList<ContentProviderOperation>();
			 *
			 * ContentProviderOperation.Builder builder =
			 * ContentProviderOperation
			 * .newInsert(ContactsContract.Data.CONTENT_URI);
			 * builder.withValueBackReference(
			 * ContactsContract.Data.RAW_CONTACT_ID, id);
			 * builder.withValue(ContactsContract.Data.MIMETYPE,
			 * Phone.CONTENT_ITEM_TYPE);
			 * builder.withValue(ContactsContract.Data.DATA1, number);
			 * builder.withValue(ContactsContract.Data.DATA2,
			 * ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
			 * operationList.add(builder.build());
			 *
			 * builder = ContentProviderOperation
			 * .newInsert(ContactsContract.Data.CONTENT_URI);
			 * builder.withValueBackReference(
			 * ContactsContract.Data.RAW_CONTACT_ID, id);
			 * builder.withValue(ContactsContract.Data.MIMETYPE,
			 * "vnd.android.cursor.item/com.revesoft.itelmobiledialer.mimetype_call"
			 * ); builder.withValue(ContactsContract.Data.DATA1, number);
			 * builder.withValue(ContactsContract.Data.DATA2,
			 * ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM);
			 * builder.withValue(ContactsContract.Data.DATA3, "Free Call (" +
			 * number + ")"); operationList.add(builder.build());
			 *
			 * builder = ContentProviderOperation
			 * .newInsert(ContactsContract.Data.CONTENT_URI);
			 * builder.withValueBackReference(
			 * ContactsContract.Data.RAW_CONTACT_ID, id);
			 * builder.withValue(ContactsContract.Data.MIMETYPE,
			 * "vnd.android.cursor.item/com.revesoft.itelmobiledialer.mimetype_im"
			 * ); builder.withValue(ContactsContract.Data.DATA1, number);
			 * builder.withValue(ContactsContract.Data.DATA2,
			 * ContactsContract.CommonDataKinds.Im.TYPE_CUSTOM);
			 * builder.withValue(ContactsContract.Data.DATA3, "Chat free (" +
			 * number + ")"); operationList.add(builder.build());
			 *
			 *
			 * try { context.getContentResolver().applyBatch(
			 * ContactsContract.AUTHORITY, operationList); } catch (Exception e)
			 * { e.printStackTrace(); }
			 */
        } else {
            if (BuildConfig.DEBUG)
                Logger.logI("ContactEngine", "DBContactNode not found!! inserting new contact");
            addContactToAccount1(context, accountName, accountType, name, number);
        }
    }

    public static CustomContactData getCustomContactData(Context context,
                                                         Uri uri) {
        if (uri == null)
            return null;
        try {
            CustomContactData ccd = new CustomContactData();
            Cursor cursor = context.getContentResolver().query(uri, null, null,
                    null, null);
            if (cursor != null && cursor.moveToFirst()) {
                ccd.contentUri = uri;
                ccd.mimeType = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.Data.MIMETYPE));
                ccd.number = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.Data.DATA1));
                ccd.summery = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.Data.DATA2));
                ccd.details = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.Data.DATA3));
                ccd.name = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
            }
            if (cursor != null)
                cursor.close();
            return ccd;
        } catch (Exception e) {
            return null;
        }
    }

    public static int deleteContact(Context context, String name) {
        Uri url = RawContacts.CONTENT_URI;
        String where = Contacts.DISPLAY_NAME + " = '" + name
                + "'";
        String[] selectionArgs = null;
        return context.getContentResolver().delete(url, where, selectionArgs);
    }

    public static ArrayList<PhoneName> loadAllPhones(Context context) {
        ArrayList<PhoneName> listAllPhones = new ArrayList<>();
        Uri uri = Phone.CONTENT_URI;
        String[] projection = new String[]{
                Phone._ID,
                Phone.CONTACT_ID,
                Phone.DISPLAY_NAME,
                Phone.NUMBER,
                Phone.STARRED,
                Phone.TYPE};
        String selection = null;// ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER
        // + " = 1";
        String[] selectionArgs = null;
        String sortOrder = Phone.DISPLAY_NAME
                + " COLLATE LOCALIZED ASC";

        Cursor cursor = context.getContentResolver().query(uri, projection,
                selection, selectionArgs, sortOrder);
        if (cursor != null) {
            if (BuildConfig.DEBUG)
                Logger.logD("result found", "" + cursor.getCount());
            int numberIndex = cursor
                    .getColumnIndex(Phone.NUMBER);
            int nameIndex = cursor
                    .getColumnIndex(Phone.DISPLAY_NAME);
            int typeIndex = cursor
                    .getColumnIndex(Phone.TYPE);
            int favIndex = cursor
                    .getColumnIndex(Phone.STARRED);
            int idIndex = cursor
                    .getColumnIndex(Phone.CONTACT_ID);

            while (cursor.moveToNext()) {
                PhoneName pn = new PhoneName();
                pn.id = cursor.getInt(idIndex);
                pn.name = cursor.getString(nameIndex);
                // Logger.logD("Name",pn.name);
                pn.number = cursor.getString(numberIndex);
                pn.starred = cursor.getString(favIndex);
                // Logger.logD("Number",pn.number);
                int type = cursor.getInt(typeIndex);
                if (type == Phone.TYPE_HOME) {
                    pn.type = "Home";
                } else if (type == Phone.TYPE_MOBILE) {
                    pn.type = "Mobile";
                } else if (type == Phone.TYPE_WORK) {
                    pn.type = "work";
                } else {
                    pn.type = "other";
                }
                listAllPhones.add(pn);
            }
            cursor.close();
        }
        return listAllPhones;
    }

    public static Bitmap loadContactPhotoOLD(Context c, long contactId) {
        Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI,
                contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri,
                Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = c.getContentResolver().query(photoUri,
                new String[]{Contacts.Photo.DATA15}, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    return BitmapFactory.decodeStream(new ByteArrayInputStream(
                            data));
                }
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    public static Bitmap loadContactPhotoHighResolution(Context c, String number) {
        if (number == null || number.equals(""))
            return null;
        String contactID = fetchContactIdFromPhoneNumber(c, number);
        if (contactID != null && !contactID.equals(""))
            return loadContactPhotoHighResolution(c, Long.parseLong(contactID));
        else
            return null;
    }

    private static Bitmap loadContactPhotoHighResolution(Context c,
                                                         long contactId) {

        Bitmap bitmap = null;

        if (contactId < 0)
            return null;

        Uri photoUri = ContactEngine.getPhotoUriNew(c, contactId);

        if (photoUri != null) {
            InputStream in = null;
            try {
                in = new BufferedInputStream(c.getContentResolver()
                        .openInputStream(photoUri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            bitmap = BitmapFactory.decodeStream(in);
        }

        return bitmap;
    }

    public static Bitmap loadContactPhoto(Context c, String number) {
        if (number == null || number.equals(""))
            return null;
        String contactID = fetchContactIdFromPhoneNumber(c, number);
        if (contactID != null && !contactID.equals(""))
            return loadContactPhoto(c, Long.parseLong(contactID));
        else
            return null;
    }

    private static Bitmap loadContactPhoto(Context c, long contactId) {
        if (contactId < 0)
            return null;
        InputStream is = Contacts.openContactPhotoInputStream(
                c.getContentResolver(), ContentUris.withAppendedId(
                        Contacts.CONTENT_URI, contactId));
        if (is != null)
            return BitmapFactory.decodeStream(is);
        else
            return null;
    }

    public static Bitmap loadContactPhotoUsingLookupKey(Context c, String lookup) {
        Uri lookupUri = Uri.withAppendedPath(
                Contacts.CONTENT_LOOKUP_URI, lookup);
        InputStream is = null;
        try {
            is = Contacts.openContactPhotoInputStream(
                    c.getContentResolver(), lookupUri);
        } catch (Exception e) {
            // TODO: handle exception
        }
        if (is != null)
            return BitmapFactory.decodeStream(is);
        else
            return null;
    }

    private static String fetchContactIdFromPhoneNumber(Context c,
                                                        String phoneNumber) {
        if (phoneNumber == null || phoneNumber.equals(""))
            return "-1";
        Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber));
        Cursor cursor = c.getContentResolver().query(uri,
                new String[]{PhoneLookup.DISPLAY_NAME, PhoneLookup._ID},
                null, null, null);

        String contactId = "";

        if (cursor != null && cursor.moveToFirst()) {
            do {
                contactId = cursor.getString(cursor
                        .getColumnIndex(PhoneLookup._ID));
            } while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();

        return contactId;
    }

    private static Uri getPhotoUri(Context context, long _id) {
        try {
            Cursor cur = context
                    .getContentResolver()
                    .query(ContactsContract.Data.CONTENT_URI,
                            null,
                            ContactsContract.Data.CONTACT_ID
                                    + "="
                                    + _id
                                    + " AND "
                                    + ContactsContract.Data.MIMETYPE
                                    + "='"
                                    + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE
                                    + "'", null, null);
            if (cur != null) {
                if (!cur.moveToFirst()) {
                    cur.close();
                    return null; // no photo
                }
                cur.close();
            } else {
                return null; // error in cursor process
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Uri person = ContentUris.withAppendedId(
                Contacts.CONTENT_URI, _id);
        return Uri.withAppendedPath(person,
                Contacts.Photo.CONTENT_DIRECTORY);
    }

    public static String getPhotoUri(Context context, String number) {
        if (number == null || number.isEmpty())
            return null;

        String photoUri;
        try {
            Cursor contactLookupCursor = context.getContentResolver().query(
                    Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number)),
                    new String[]{PhoneLookup.PHOTO_URI},
                    null, null, null);
            if (contactLookupCursor != null) {
                if (contactLookupCursor.moveToFirst()) {
                    photoUri = contactLookupCursor.getString(contactLookupCursor
                            .getColumnIndexOrThrow(PhoneLookup.PHOTO_URI));
                    return photoUri;
                }
                contactLookupCursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static long getContactIDFromNumber2(Context context,
                                               String contactNumber) {
        // contactNumber = Uri.encode(contactNumber);
        if (contactNumber == null || contactNumber.equals(""))
            return -1;
        long phoneContactID = -1;
        Cursor contactLookupCursor = context.getContentResolver().query(
                Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
                        Uri.encode(contactNumber)),
                new String[]{PhoneLookup.DISPLAY_NAME, PhoneLookup._ID},
                PhoneLookup.DISPLAY_NAME + " = " + contactNumber + " or "
                        + PhoneLookup.DISPLAY_NAME + " = +" + contactNumber,
                null, null);

        if (contactLookupCursor != null && contactLookupCursor.moveToFirst()) {
            do {
                phoneContactID = contactLookupCursor.getInt(contactLookupCursor
                        .getColumnIndexOrThrow(PhoneLookup._ID));
            } while (contactLookupCursor.moveToNext());
        }
        if (contactLookupCursor != null)
            contactLookupCursor.close();

        return phoneContactID;
    }

    private static long getContactIDFromNumber(Context context,
                                               String contactNumber) {
        // contactNumber = Uri.encode(contactNumber);
        if (contactNumber == null || contactNumber.equals(""))
            return -1;
        long phoneContactID = -1;
        Cursor contactLookupCursor = context.getContentResolver().query(
                Phone.CONTENT_URI,
                new String[]{
                        Phone.DISPLAY_NAME,
                        Phone.CONTACT_ID},
                Phone.NUMBER + " = '"
                        + contactNumber + "' or "
                        + Phone.NUMBER
                        + " = '+" + contactNumber + "'", null, null);
        if (contactLookupCursor != null && contactLookupCursor.moveToFirst()) {
            do {
                phoneContactID = contactLookupCursor.getInt(contactLookupCursor.getColumnIndexOrThrow(Phone.CONTACT_ID));
            } while (contactLookupCursor.moveToNext());
        }

        if (contactLookupCursor != null)
            contactLookupCursor.close();

        return phoneContactID;
    }

    public static String getContactNameFromNumber(Context context, String contactNumber) {
        if (contactNumber == null || contactNumber.equals(""))
            return null;
        String name = null;
        try {
            Cursor contactLookupCursor = context.getContentResolver().query(
                    Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(contactNumber)),
                    new String[]{PhoneLookup.DISPLAY_NAME, PhoneLookup._ID},
                    null, null, null);
            if (contactLookupCursor != null) {
                if (contactLookupCursor.moveToFirst()) {
                    do {
                        name = contactLookupCursor.getString(contactLookupCursor
                                .getColumnIndexOrThrow(PhoneLookup.DISPLAY_NAME));
                    } while (contactLookupCursor.moveToNext());
                }
                contactLookupCursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return name;
    }

    public static ContactData getContactInfoFromNumber(Context context, String contactNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(contactNumber));
        Cursor cursor = cr.query(uri, new String[]{
                PhoneLookup._ID,
                PhoneLookup.DISPLAY_NAME,
                PhoneLookup.PHOTO_ID,
        }, null, null, null);

        ContactData contactData = null;
        if (cursor != null && cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndex(PhoneLookup._ID));
            String name = cursor.getString(cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME));
            String photoUri = cursor.getString(cursor.getColumnIndex(PhoneLookup.PHOTO_URI));

            contactData = new ContactData(id, name, contactNumber, photoUri);

            cursor.close();
        }

        return contactData;
    }

    public static String getContactNumberFromId(Context context, long _id) {
        String number = null;
        Uri uri = Phone.CONTENT_URI;
        String[] projection = new String[]{
                Phone._ID,
                Phone.CONTACT_ID,
                Phone.NUMBER,
                Phone.TYPE};
        String selection = Phone.CONTACT_ID
                + " = '" + _id + "'";
        String[] selectionArgs = null;
        String sortOrder = Phone.NUMBER
                + " COLLATE LOCALIZED ASC";

        Cursor cursor = context.getContentResolver().query(uri, projection,
                selection, selectionArgs, sortOrder);
//        if (BuildConfig.DEBUG)
//            Logger.logD("result found", "" + cursor.getCount());

        if (cursor != null && cursor.moveToNext()) {
            int numberIndex = cursor.getColumnIndex(Phone.NUMBER);
            number = cursor.getString(numberIndex);
        }
        if (cursor != null)
            cursor.close();
        return number;
    }

    public static String getContactEmailFromId(Context context, long _id) {
        String email = null;
        Uri uri = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Email._ID,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID,
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.TYPE};
        String selection = ContactsContract.CommonDataKinds.Email.CONTACT_ID
                + " = '" + _id + "'";
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.CommonDataKinds.Email.ADDRESS
                + " COLLATE LOCALIZED ASC";

        Cursor cursor = context.getContentResolver().query(uri, projection,
                selection, selectionArgs, sortOrder);
//        if (BuildConfig.DEBUG)
//            Logger.logD("result found", "" + cursor.getCount());

        if (cursor != null && cursor.moveToNext()) {
            int mailIndex = cursor
                    .getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
            email = cursor.getString(mailIndex);
        }
        if (cursor != null)
            cursor.close();
        return email;
    }

    public static String getContactLookupKey(Context context, String number) {
        String lookupKey = "";
        if (number == null || number.equals(""))
            return lookupKey;
        Uri uri = Uri.withAppendedPath(
                PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] projection = new String[]{PhoneLookup.DISPLAY_NAME,
                PhoneLookup.LOOKUP_KEY};
        Cursor cursor = context.getContentResolver().query(uri, projection,
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            lookupKey = cursor.getString(cursor
                    .getColumnIndex(PhoneLookup.LOOKUP_KEY));
        }
        if (cursor != null)
            cursor.close();
        return lookupKey;
    }

    public static Cursor getContactLookupKeyCursor(Context context, String number) {
        Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] projection = new String[]{PhoneLookup.DISPLAY_NAME,
                PhoneLookup.LOOKUP_KEY};
        return context.getContentResolver().query(uri, projection,
                null, null, null);
    }

    private static Uri getPhotoUriNew(Context context, long _id) {
        String[] CONTACTS_SUMMARY_PROJECTION;
        if (android.os.Build.VERSION.SDK_INT > 10) {
            CONTACTS_SUMMARY_PROJECTION = new String[]{
                    Contacts._ID,
                    Contacts.PHOTO_URI};
        } else {
            CONTACTS_SUMMARY_PROJECTION = new String[]{
                    Contacts._ID,
                    Contacts.PHOTO_ID};
        }

        Cursor cursor = context.getContentResolver().query(
                Contacts.CONTENT_URI,
                CONTACTS_SUMMARY_PROJECTION,
                Contacts._ID + "=" + _id, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Uri photoUri = null;
            if (android.os.Build.VERSION.SDK_INT > 10) {
                String photoPath = cursor.getString(cursor
                        .getColumnIndex(Contacts.PHOTO_URI));
                if (photoPath != null)
                    photoUri = Uri.parse(photoPath);
            } else {
                String photoID = cursor.getString(cursor
                        .getColumnIndex(Contacts.PHOTO_ID));
                if (photoID != null) {
                    photoUri = ContentUris.withAppendedId(
                            ContactsContract.Data.CONTENT_URI,
                            Long.parseLong(photoID));
                }
            }
            cursor.close();
            return photoUri;
        } else {
            if (cursor != null)
                cursor.close();
            return null;
        }
    }

    @SuppressLint("NewApi")
    public static Uri getOwnerPhotouri(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= 14) {
            String[] PROJECTION = {ContactsContract.CommonDataKinds.Photo.PHOTO_URI,

            };

            Cursor cursor = context.getContentResolver().query(
                    Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                            Contacts.Data.CONTENT_DIRECTORY),
                    PROJECTION, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                Uri photoUri = null;
                String photoUriString = cursor
                        .getString(cursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO_URI));
                if (photoUriString != null) {
                    photoUri = Uri.parse(photoUriString);
                }
                cursor.close();
                return photoUri;
            } else {
                if (cursor != null)
                    cursor.close();
                return null;
            }

        } else {
            return null;
        }

    }

    public static Cursor getContactCursorFromLookUpList(Context context, Set<String> lookUps, String[] projection, String selection, String order) {
        Cursor cursor;
        if (context == null || lookUps == null)
            return null;

        String lookupSelection = " ";
        String[] lookupKeys = lookUps.toArray(new String[lookUps.size()]);
        for (int i = 0; i < lookupKeys.length; i++) {
            if (i == 0)
                lookupSelection += "( ";
            lookupSelection += PhoneLookup.LOOKUP_KEY + " = '" + lookupKeys[i] + "' ";
            if (i < lookupKeys.length - 1)
                lookupSelection += " OR ";
            if (i == lookupKeys.length - 1)
                lookupSelection += " )";
        }

        if (lookupKeys.length > 0 && selection != null)
            selection += " AND " + lookupSelection;
        else if (lookupKeys.length > 0)
            selection = lookupSelection;

//        Logger.logI("ContactEngine", "getContactCursorFromLookUpList generatedSelection: "+selection);

        Uri queryUri = ContactsContract.Contacts.CONTENT_URI;

        cursor = context.getContentResolver().query(
                queryUri,
                projection,
                selection,
                null,
                order);

        return cursor;
    }

    public static Cursor getZiptOutCursorFromLookUpList(Context context, Set<String> lookUps, String[] projection, String selection, String order) {
        Cursor cursor;
        if (context == null || lookUps == null)
            return null;

        String lookupSelection = " ";
        String[] lookupKeys = lookUps.toArray(new String[lookUps.size()]);
        for (int i = 0; i < lookupKeys.length; i++) {
            if (i == 0)
                lookupSelection += "( ";
            lookupSelection += PhoneLookup.LOOKUP_KEY + " <> '" + lookupKeys[i] + "' ";
            if (i < lookupKeys.length - 1)
                lookupSelection += " AND ";
            if (i == lookupKeys.length - 1)
                lookupSelection += " )";
        }

        if (lookupKeys.length > 0 && selection != null)
            selection += " AND " + lookupSelection;
        else if (lookupKeys.length > 0)
            selection = lookupSelection;

//        Logger.logI("ContactEngine", "getContactCursorFromLookUpList generatedSelection: "+selection);

        Uri queryUri = ContactsContract.Contacts.CONTENT_URI;

        cursor = context.getContentResolver().query(
                queryUri,
                projection,
                selection,
                null,
                order);

        return cursor;
    }

    /**
     * Read all contacts from the phone. If phone number already exists in the iPay contacts database,
     * fetch the corresponding info from the database.
     */
    public static List<ContactNode> getAllContacts(Context context) {
        List<ContactNode> phoneContacts = new ArrayList<>();

        final String[] projection = new String[]{
                Phone._ID,
                Phone.NUMBER,
                Phone.HAS_PHONE_NUMBER,
                Phone.PHOTO_URI,
                Phone.DISPLAY_NAME,
        };

        final String selection = Phone.HAS_PHONE_NUMBER + "=1";
        final String order = Phone.DISPLAY_NAME + " COLLATE NOCASE ASC";
        Uri queryUri = Phone.CONTENT_URI;

        Cursor phoneContactsCursor = context.getContentResolver().query(queryUri, projection, selection, null, order);
        if (phoneContactsCursor == null)
            return null;

        if (phoneContactsCursor.moveToFirst()) {
            int nameIndex = phoneContactsCursor.getColumnIndex(Phone.DISPLAY_NAME);
            int photoUrlIndex = phoneContactsCursor.getColumnIndex(Phone.PHOTO_URI);
            int numberIndex = phoneContactsCursor.getColumnIndex(Phone.NUMBER);

            do {
                String name = phoneContactsCursor.getString(nameIndex);
                String phoneNumber = phoneContactsCursor.getString(numberIndex);
                String photoUrl = phoneContactsCursor.getString(photoUrlIndex);

                if (phoneNumber != null) phoneNumber = phoneNumber.replaceAll("[^\\d]", "");

                if (InputValidator.isValidNumber(phoneNumber)) {
                    phoneNumber = formatMobileNumberBD(phoneNumber);

                    ContactNode contactNode;
                    contactNode = new ContactNode(name, phoneNumber, photoUrl);
                    phoneContacts.add(contactNode);
                }
            } while (phoneContactsCursor.moveToNext());
        }

        phoneContactsCursor.close();

        return phoneContacts;
    }


    public static String formatMobileNumberBD(String bdNumber) {

        String bdNumberStr = bdNumber;
        Phonenumber.PhoneNumber bdNumberProto = null;
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

        try {
            bdNumberProto = phoneUtil.parse(bdNumberStr, "BD"); // We have a mapping for country code vs country ISO code in the CountryList class
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
        }

        if (bdNumberProto != null)
            bdNumberStr = phoneUtil.format(bdNumberProto, PhoneNumberUtil.PhoneNumberFormat.E164);

        return bdNumberStr;
    }

    public static String formatMobileNumberInternational(String number, String countryCode) {

        String numberStr = number;
        Phonenumber.PhoneNumber numberProto = null;
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

        try {
            numberProto = phoneUtil.parse(numberStr, countryCode); // We have a mapping for country code vs country ISO code in the CountryList class
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
        }

        if (numberProto != null)
            numberStr = phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.E164);

        return numberStr;
    }

    public static String formatLocalMobileNumber(String mobileNumber) {
        String numberStr = mobileNumber;
        Phonenumber.PhoneNumber numberProto = null;
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

        try {
            numberProto = phoneUtil.parse(numberStr, "BD");

        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
        }

        if (numberProto != null)
            numberStr = String.valueOf(numberProto.getNationalNumber());

        return numberStr;
    }

    /**
     * Pass phone contacts in the newContacts list and server contacts in the oldContacts list
     * to get a list of newly added/updated contacts in the phone book
     */
    public static ContactDiff getContactDiff(List<ContactNode> phoneContacts, List<ContactNode> serverContacts) {
        ContactDiff contactDiff = new ContactDiff();

        try {
            Map<String, ContactNode> serverContactMap = new HashMap<>();
            for (ContactNode serverContact : serverContacts) {
                serverContactMap.put(serverContact.getMobileNumber(), serverContact);
            }

            for (ContactNode phoneContact : phoneContacts) {
                if (serverContactMap.containsKey(phoneContact.getMobileNumber())) {
                    String serverName = serverContactMap.get(phoneContact.getMobileNumber()).getName();
                    String phoneName = phoneContact.getName();

                    if (!serverName.equals(phoneName)) {
                        contactDiff.updatedContacts.add(phoneContact);
                    }
                } else {
                    contactDiff.newContacts.add(phoneContact);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return contactDiff;
    }

    /**
     * Trim +880 (if exists) from the mobile number
     */
    public static String trimPrefix(String number) {
        number = number.trim();
        return number.substring(
                Math.max(0, number.length() - 10),
                number.length());
    }

    public static class CustomContactData {
        public Uri contentUri;
        public String mimeType;
        public String number;
        public String summery;
        public String details;
        public String name;
    }

    public static class ContactData {
        public final long contactID;
        public final String name;
        public final String mobileNumber;
        public final String photoUri;

        public ContactData(long contactID, String name, String mobileNumber, String photoUri) {
            this.contactID = contactID;
            this.name = name;
            this.mobileNumber = mobileNumber;
            this.photoUri = photoUri;
        }
    }

    public static class ContactDiff {
        public final List<ContactNode> newContacts;
        public final List<ContactNode> updatedContacts;

        public ContactDiff() {
            newContacts = new ArrayList<>();
            updatedContacts = new ArrayList<>();
        }

        public ContactDiff(List<ContactNode> newContacts, List<ContactNode> updatedContacts) {
            this.newContacts = newContacts;
            this.updatedContacts = updatedContacts;
        }
    }

}