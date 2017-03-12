package bd.com.ipay.ipayskeleton.Api;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Model.Friend.FriendInfo;
import bd.com.ipay.ipayskeleton.Model.Friend.FriendNode;
import bd.com.ipay.ipayskeleton.Model.Friend.GetContactsResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class GetFriendsAsyncTask extends HttpRequestGetAsyncTask implements HttpResponseListener {

    GetContactsResponse mGetContactsResponse;
    public GetFriendsAsyncTask(Context context) {
        super(Constants.COMMAND_GET_FRIENDS, Constants.BASE_URL_FRIEND + Constants.URL_GET_CONTACTS, context);
        mHttpResponseListener = this;
    }


    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            if (getContext() != null) {
                Toast.makeText(getContext(), R.string.contacts_sync_failed, Toast.LENGTH_LONG).show();
                return;
            }
        }

        try {
            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                Gson gson = new Gson();
                mGetContactsResponse = gson.fromJson(result.getJsonString(), GetContactsResponse.class);
              //  FriendNode[] friendNodeArray = gson.fromJson(result.getJsonString(), FriendNode[].class);

                List<FriendInfo> mGetAllContactsResponse = mGetContactsResponse.getContactList();
                Toast.makeText(getContext(),mGetAllContactsResponse.toString(), Toast.LENGTH_LONG).show();

                SyncContactsAsyncTask syncContactsAsyncTask = new SyncContactsAsyncTask(getContext(), mGetAllContactsResponse);
                syncContactsAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                if (getContext() != null) {
                    Toast.makeText(getContext(), R.string.contacts_sync_failed, Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

            if (getContext() != null) {
                Toast.makeText(getContext(), R.string.contacts_sync_failed, Toast.LENGTH_LONG).show();
            }
        }

    }
}
