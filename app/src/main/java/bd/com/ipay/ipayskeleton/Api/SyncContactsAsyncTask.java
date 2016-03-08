package bd.com.ipay.ipayskeleton.Api;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import bd.com.ipay.ipayskeleton.DatabaseHelper.DBConstants;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.GetUserInfoRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.SqLiteDatabase.SubscriberEntry;
import bd.com.ipay.ipayskeleton.Model.FireBase.UserFriend;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.GetProfilePictureRequest;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class SyncContactsAsyncTask extends AsyncTask<String, Void, String> {

    private Context mContext;

    public SyncContactsAsyncTask(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected String doInBackground(String... params) {

        try {

            // Add my contacts in Firebase database in user-contacts table
            SharedPreferences pref = mContext.getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
            final String mUserID = pref.getString(Constants.USERID, "");

            ArrayList<UserFriend> userFriends = getAllContacts();
            final HashMap<String, UserFriend> temp = new HashMap<>();

            for (int i = 0; i < userFriends.size(); i++) {
                temp.put(userFriends.get(i).getMobileNumber(), userFriends.get(i));
            }

            final Firebase userContactsRootRef = new Firebase(Constants.PATH_TO_USER_CONTACTS);
            final Firebase newUserRef = userContactsRootRef.child(mUserID);
            newUserRef.setValue(temp);

            newUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot newUserContactSnapshot : dataSnapshot.getChildren()) {
                        final UserFriend mContactOfNewUser = newUserContactSnapshot.getValue(UserFriend.class);

                        userContactsRootRef.child(mContactOfNewUser.getMobileNumber())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.exists()) {
                                            mContactOfNewUser.setIsFriend(true);

                                            // Set friend to this user
                                            newUserRef.child(mContactOfNewUser.getMobileNumber())
                                                    .setValue(mContactOfNewUser);

                                            // Create a database entry
                                            SubscriberEntry mSubscriberEntry = new SubscriberEntry(
                                                    mContactOfNewUser.getMobileNumber()
                                                    , mContactOfNewUser.getName());

                                            boolean exists = DataHelper.getInstance(mContext).
                                                    checkIfStringFieldExists(DBConstants.DB_TABLE_SUBSCRIBERS,
                                                            DBConstants.KEY_MOBILE_NUMBER, mSubscriberEntry.getMobileNumber());
                                            if (!exists) {
                                                DataHelper.getInstance(mContext).createSubscribers(mSubscriberEntry);
                                                Log.d("ipay users:", mSubscriberEntry.getMobileNumber());
                                            }

                                            // TODO: remove this profile pic download ..
//                                            GetProfilePictureRequest mGetProfilePictureRequest = new GetProfilePictureRequest(mSubscriberEntry.getMobileNumber());
                                            GetUserInfoRequestBuilder mGetUserInfoRequestBuilder = new GetUserInfoRequestBuilder(mSubscriberEntry.getMobileNumber());
                                            String uri = mGetUserInfoRequestBuilder.getGeneratedUri();
                                            new DownloadProfilePictureGetAsyncTask(Constants.COMMAND_DOWNLOAD_PROFILE_PICTURE_FRIEND, uri,
                                                    mSubscriberEntry.getMobileNumber(), mContext)
                                                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                                            // Set friend for the other user
                                            userContactsRootRef.child(mContactOfNewUser.getMobileNumber()).child(mUserID)
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot.exists()) {

                                                                UserFriend updateUser = dataSnapshot.getValue(UserFriend.class);
                                                                updateUser.setIsFriend(true);

                                                                // Update user
                                                                userContactsRootRef.
                                                                        child(mContactOfNewUser.getMobileNumber())
                                                                        .child(mUserID)
                                                                        .setValue(updateUser);
                                                            }

                                                        }

                                                        @Override
                                                        public void onCancelled(FirebaseError firebaseError) {

                                                        }
                                                    });

                                        }
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {

                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

            pref.edit().putBoolean(Constants.FIRST_LAUNCH, true).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Successful";
    }

    @Override
    protected void onPostExecute(String result) {
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }

    private ArrayList<UserFriend> getAllContacts() {

        ArrayList<UserFriend> userFriends = new ArrayList<UserFriend>();

        ContentResolver cr = mContext.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        // Processing phone number
                        phoneNo = phoneNo.replaceAll("[^0-9]", "");
                        if (phoneNo.length() == 11) phoneNo = "+88" + phoneNo;
                        else if (phoneNo.length() == 13) phoneNo = "+" + phoneNo;
                        else continue;

                        userFriends.add(new UserFriend(phoneNo, name.replaceAll("[^a-zA-Z0-9]+", " ")));
                    }
                    pCur.close();
                }
            }
        }

        cur.close();

        return userFriends;
    }
}