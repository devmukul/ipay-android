package bd.com.ipay.ipayskeleton.Api;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Model.Friend.FriendInfo;
import bd.com.ipay.ipayskeleton.Model.Friend.FriendNode;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;

public class SyncContactsAsyncTask extends AsyncTask<String, Void, Void> {

    private static boolean contactsSyncedOnce;

    private Context context;
    private List<FriendNode> serverContacts;

    public SyncContactsAsyncTask(Context context, List<FriendNode> serverContacts) {
        this.context = context;
        this.serverContacts = serverContacts;
    }

    @Override
    protected Void doInBackground(String... params) {
        if (contactsSyncedOnce)
            return null;

        Cursor phoneContactsCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);

        List<FriendNode> phoneContacts = new ArrayList<>();

        if (phoneContactsCursor != null && phoneContactsCursor.moveToFirst()) {
            int nameIndex = phoneContactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int phoneNumberIndex = phoneContactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

            do {
                String name = phoneContactsCursor.getString(nameIndex);
                String phoneNumber = phoneContactsCursor.getString(phoneNumberIndex).replaceAll("[^\\d]", "");

                if (ContactEngine.isValidNumber(phoneNumber)) {
                    FriendNode contact = new FriendNode(ContactEngine.formatMobileNumberBD(phoneNumber),
                            new FriendInfo(name));
                    phoneContacts.add(contact);
                }
            } while (phoneContactsCursor.moveToNext());
        }

        phoneContactsCursor.close();

        ContactEngine.ContactDiff contactDiff = ContactEngine.getContactDiff(phoneContacts, serverContacts);

        System.out.println(contactDiff.newFriends.toString());
        System.out.println(contactDiff.updatedFriends.toString());

        contactsSyncedOnce = true;

        return null;
    }
}