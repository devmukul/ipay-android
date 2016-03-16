package bd.com.ipay.ipayskeleton.Api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import bd.com.ipay.ipayskeleton.Activities.HomeActivity;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DBConstants;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.Model.FireBase.FriendNode;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.GetUserInfoRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.SqLiteDatabase.SubscriberEntry;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class UpdateSubscriberTableAsyncTask extends AsyncTask<String, Void, String> {
    
    private Context mContext;
    private Firebase ref;

    public HttpResponseListener mHttpResponseListener;

    public UpdateSubscriberTableAsyncTask(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            ref = new Firebase(Constants.PATH_TO_FIREBASE_DATABASE);

            // Create a handler to handle the result of the authentication
            Firebase.AuthResultHandler authResultHandler = new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {

                    ref.child(Constants.FIREBASE_CONTACT_LIST).child(authData.getUid()).
                            child(Constants.FIREBASE_SYNCED).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {

                                for (DataSnapshot friendNodeSnapshot : dataSnapshot.getChildren()) {

                                    final FriendNode mFriendNode = friendNodeSnapshot.getValue(FriendNode.class);

                                    if (mFriendNode.getInfo().isFriend()) {
                                        SubscriberEntry mSubscriberEntry = new SubscriberEntry(
                                                mFriendNode.phoneNumber, mFriendNode.info.getName());

                                        boolean exists = DataHelper.getInstance(mContext).
                                                checkIfStringFieldExists(DBConstants.DB_TABLE_SUBSCRIBERS,
                                                        DBConstants.KEY_MOBILE_NUMBER, mSubscriberEntry.getMobileNumber());

                                        if (!exists) {

                                            DataHelper.getInstance(mContext).createSubscribers(mSubscriberEntry);

                                            // Download profile picture for new user
                                            GetUserInfoRequestBuilder mGetUserInfoRequestBuilder = new GetUserInfoRequestBuilder(mSubscriberEntry.getMobileNumber());
                                            String uri = mGetUserInfoRequestBuilder.getGeneratedUri();
                                            new DownloadProfilePictureGetAsyncTask(Constants.COMMAND_DOWNLOAD_PROFILE_PICTURE_FRIEND, uri,
                                                    mSubscriberEntry.getMobileNumber(), mContext)
                                                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                            Log.d(Constants.iPay_USER, mSubscriberEntry.getMobileNumber());

                                        } else {

                                            // TODO: Since we do not have any API to check the changes of profile pictures
                                            // TODO: We need to download the profile pictures each time we login in the application
                                            // TODO: to see the updates of profile picture changes
                                            // TODO: WE'LL REMOVE THIS ELSE PART as soon as we get the API for the updates in profile pictures.

                                            GetUserInfoRequestBuilder mGetUserInfoRequestBuilder = new GetUserInfoRequestBuilder(mSubscriberEntry.getMobileNumber());
                                            String uri = mGetUserInfoRequestBuilder.getGeneratedUri();
                                            new DownloadProfilePictureGetAsyncTask(Constants.COMMAND_DOWNLOAD_PROFILE_PICTURE_FRIEND, uri,
                                                    mSubscriberEntry.getMobileNumber(), mContext)
                                                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                            Log.d(Constants.iPay_USER, mSubscriberEntry.getMobileNumber());

                                        }

                                    }
                                }

                                HomeActivity.contactsSyncedOnce = true;

                            } else
                                Log.d(Constants.ApplicationTag, mContext.getString(R.string.no_contacts_in_firebase));
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });

                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                }
            };

            // Authenticate users with a custom Firebase token
            ref.authWithCustomToken(HomeActivity.fireBaseToken, authResultHandler);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Constants.SUCCESS;
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
}