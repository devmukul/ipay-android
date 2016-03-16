package bd.com.ipay.ipayskeleton.Api;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import bd.com.ipay.ipayskeleton.Activities.HomeActivity;
import bd.com.ipay.ipayskeleton.Model.FireBase.FriendNodeToUpload;
import bd.com.ipay.ipayskeleton.Model.FireBase.UpdateRequestToServer;
import bd.com.ipay.ipayskeleton.Model.FireBase.UserInfoToUpload;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class SyncContactsAsyncTask extends AsyncTask<String, Void, String> {

    private final int AUTH_IN_PROGRESS = 0;
    private final int AUTH_SUCCESS = 1;
    private final int AUTH_FAILED = 2;
    private int isReturnedFromServer = AUTH_IN_PROGRESS;

    private Context mContext;
    private Firebase ref;
    private HttpResponse mHttpResponse;
    private boolean updateSuccess = false;

    public SyncContactsAsyncTask(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            final ArrayList<FriendNodeToUpload> userFriends = getAllContacts();
            ref = new Firebase(Constants.PATH_TO_FIREBASE_DATABASE);

            // Create a handler to handle the result of the authentication
            Firebase.AuthResultHandler authResultHandler = new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {

                    for (int i = 0; i < userFriends.size(); i++) {

                        Gson gson = new Gson();
                        String json = gson.toJson(userFriends.get(i));

                        ref.child(Constants.FIREBASE_CONTACT_LIST).child(authData.getUid()).
                                child(Constants.FIREBASE_DIRTY).child(userFriends.get(i).getPhoneNumber())
                                .setValue(userFriends.get(i));
                    }

                    isReturnedFromServer = AUTH_SUCCESS;
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    isReturnedFromServer = AUTH_FAILED;
                }
            };

            // Authenticate users with a custom Firebase token
            ref.authWithCustomToken(HomeActivity.fireBaseToken, authResultHandler);

            while (isReturnedFromServer == AUTH_IN_PROGRESS) {
                // Wait until the auth is not processed
            }

            // Update request to server
            UpdateRequestToServer mUpdateRequestToServer = new UpdateRequestToServer();
            if (Utilities.isConnectionAvailable(mContext))
                mHttpResponse = makeRequest(mUpdateRequestToServer.getGeneratedUri());
            else
                Toast.makeText(mContext, R.string.no_internet_connection, Toast.LENGTH_LONG).show();

            try {
                String status = mHttpResponse.getStatusLine().getStatusCode() + "";

                if (status.equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    updateSuccess = true;
                } else if (mContext != null)
                    Log.d(Constants.ApplicationTag, mContext.getString(R.string.could_not_update_contacts));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (updateSuccess) return Constants.SUCCESS;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Constants.FAIL;
    }

    @Override
    protected void onPostExecute(String result) {

        if (result.equals(Constants.SUCCESS)) {
            if (mContext != null) {
                // Update subscriber table
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new UpdateSubscriberTableAsyncTask(mContext).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new UpdateSubscriberTableAsyncTask(mContext).execute();
                }
            } else
                Log.d(Constants.ApplicationTag, mContext.getString(R.string.could_not_update_table));
        } else Log.d(Constants.ApplicationTag, mContext.getString(R.string.could_not_update_table));
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }

    public static HttpResponse makeRequest(String uri) {
        try {
            HttpGet httpGet = new HttpGet(uri);

            if (HomeActivity.iPayToken.length() > 0)
                httpGet.setHeader("token", HomeActivity.iPayToken);
            return new DefaultHttpClient().execute(httpGet);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<FriendNodeToUpload> getAllContacts() {

        ArrayList<FriendNodeToUpload> userFriends = new ArrayList<FriendNodeToUpload>();

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

                        UserInfoToUpload mUserInfo = new UserInfoToUpload(name.replaceAll("[^a-zA-Z0-9]+", " "), false);
                        userFriends.add(new FriendNodeToUpload(mUserInfo, phoneNo));
                    }
                    pCur.close();
                }
            }
        }

        cur.close();

        return userFriends;
    }
}