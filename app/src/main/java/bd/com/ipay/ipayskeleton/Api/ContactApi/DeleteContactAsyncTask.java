package bd.com.ipay.ipayskeleton.Api.ContactApi;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPatchAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;

public class DeleteContactAsyncTask extends HttpRequestPatchAsyncTask implements HttpResponseListener {

    private Context context;

    public DeleteContactAsyncTask(String API_COMMAND, String mUri, String mJsonString, Context mContext) {
        super(API_COMMAND, mUri, mJsonString, mContext, false);
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
                    Toaster.makeText(context, R.string.delete_contact_successful, Toast.LENGTH_LONG);
                    new GetContactsAsyncTask(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
