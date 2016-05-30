package bd.com.ipay.ipayskeleton.Api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.Model.Friend.InfoAddFriend;
import bd.com.ipay.ipayskeleton.Model.Friend.AddFriendRequest;
import bd.com.ipay.ipayskeleton.Model.Friend.AddFriendResponse;
import bd.com.ipay.ipayskeleton.Model.Friend.FriendNode;
import bd.com.ipay.ipayskeleton.Model.Friend.InfoUpdateFriend;
import bd.com.ipay.ipayskeleton.Model.Friend.UpdateFriendRequest;
import bd.com.ipay.ipayskeleton.Model.Friend.UpdateFriendResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;

public class SyncContactsAsyncTask extends AsyncTask<String, Void, ContactEngine.ContactDiff> implements HttpResponseListener {

    private HttpRequestPostAsyncTask mAddFriendAsyncTask;
    private AddFriendResponse mAddFriendResponse;

    private HttpRequestPostAsyncTask mUpdateFriendAsyncTask;
    private UpdateFriendResponse mUpdateFriendResponse;

    private static boolean contactsSyncedOnce;

    private Context context;
    private List<FriendNode> serverContacts;

    public SyncContactsAsyncTask(Context context, List<FriendNode> serverContacts) {
        this.context = context;
        this.serverContacts = serverContacts;
    }

    @Override
    protected ContactEngine.ContactDiff doInBackground(String... params) {
        if (contactsSyncedOnce)
            return null;

        List<FriendNode> phoneContacts = ContactEngine.getAllContacts(context);

        ContactEngine.ContactDiff contactDiff = ContactEngine.getContactDiff(phoneContacts, serverContacts);

        Log.i("New Contacts", contactDiff.newFriends.toString());
        Log.i("Updated Contacts", contactDiff.updatedFriends.toString());

        DataHelper dataHelper = DataHelper.getInstance(context);
        dataHelper.createFriends(serverContacts);
        dataHelper.closeDbOpenHelper();

        contactsSyncedOnce = true;

        return contactDiff;
    }

    @Override
    protected void onPostExecute(ContactEngine.ContactDiff contactDiff) {
        if (contactDiff != null) {
            addFriends(contactDiff.newFriends);
            updateFriends(contactDiff.updatedFriends);
        }
    }

    private void addFriends(List<FriendNode> friends) {
        if (mAddFriendAsyncTask != null) {
            return;
        }

        if (friends.isEmpty())
            return;

        List<InfoAddFriend> newFriends = new ArrayList<>();
        for (FriendNode friendNode : friends) {
            newFriends.add(new InfoAddFriend(friendNode.getPhoneNumber(), friendNode.getInfo().getName()));
        }

        AddFriendRequest addFriendRequest = new AddFriendRequest(newFriends);
        Gson gson = new Gson();
        String json = gson.toJson(addFriendRequest);

        mAddFriendAsyncTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ADD_FRIENDS,
                Constants.BASE_URL_FRIEND + Constants.URL_ADD_FRIENDS, json, context, this);
        mAddFriendAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void updateFriends(List<FriendNode> friends) {
        if (mUpdateFriendResponse != null) {
            return;
        }

        List<InfoUpdateFriend> updateFriends = new ArrayList<>();
        for (FriendNode friend : friends) {
            InfoUpdateFriend infoUpdateFriend = new InfoUpdateFriend(
                    friend.getPhoneNumber(), friend.getInfo().getName());
            updateFriends.add(infoUpdateFriend);
        }

        UpdateFriendRequest updateFriendRequest = new UpdateFriendRequest(updateFriends);
        Gson gson = new Gson();
        String json = gson.toJson(updateFriendRequest);

        mUpdateFriendAsyncTask = new HttpRequestPostAsyncTask(Constants.COMMAND_UPDATE_FRIENDS,
                Constants.BASE_URL_FRIEND + Constants.URL_UPDATE_FRIENDS, json, context, this);
        mUpdateFriendAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        if (result == null) {
            mAddFriendAsyncTask = null;
            mUpdateFriendAsyncTask = null;
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_ADD_FRIENDS)) {
            try {
                mAddFriendResponse = gson.fromJson(result.getJsonString(), AddFriendResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    // Do nothing
                } else {
                    if (mAddFriendResponse != null)
                        Log.e(context.getString(R.string.failed_add_friend), mAddFriendResponse.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            mAddFriendAsyncTask = null;

        } else if (result.getApiCommand().equals(Constants.COMMAND_UPDATE_FRIENDS)) {
            try {
                mUpdateFriendResponse = gson.fromJson(result.getJsonString(), UpdateFriendResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    // Do nothing
                } else {
                    Log.e(context.getString(R.string.failed_update_friend), mUpdateFriendResponse.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            mUpdateFriendAsyncTask = null;
        }
    }
}