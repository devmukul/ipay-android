package bd.com.ipay.ipayskeleton.Api.ContactApi;

import android.content.Context;
import android.os.AsyncTask;

import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class AddContactAsyncTask extends HttpRequestPostAsyncTask implements HttpResponseListener {

    private Context context;

    public AddContactAsyncTask(String API_COMMAND, String mUri, String mJsonString, Context mContext) {
        super(API_COMMAND, mUri, mJsonString, mContext, true);
        this.context = mContext;
        mHttpResponseListener = this;
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {

        if (HttpErrorHandler.isErrorFound(result, context, null)) {
            return;
        }
        try {
            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                if (context != null) {
                    new GetContactsAsyncTask(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
