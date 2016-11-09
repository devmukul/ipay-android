package bd.com.ipay.ipayskeleton.Api;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class AddFriendAsyncTask extends HttpRequestPostAsyncTask implements HttpResponseListener {

    private Context context;

    public AddFriendAsyncTask(String API_COMMAND, String mUri, String mJsonString, Context mContext) {
        super(API_COMMAND, mUri, mJsonString, mContext);
        this.context = mContext;
        mHttpResponseListener = this;
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            if (getContext() != null) {
                return;
            }
        }
        try {
            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                if (getContext() != null) {
                    new GetFriendsAsyncTask(getContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
