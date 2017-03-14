package bd.com.ipay.ipayskeleton.Api;

import android.content.Context;
import android.util.Log;

import org.apache.http.client.methods.HttpRequestBase;

import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.HttpDeleteWithBody;

public class HttpDeleteWithBodyAsyncTask extends HttpRequestAsyncTask {

    public HttpDeleteWithBodyAsyncTask(String API_COMMAND, String mUri, Context mContext) {
        this(API_COMMAND, mUri, mContext, null);
    }

    public HttpDeleteWithBodyAsyncTask(String API_COMMAND, String mUri, Context mContext, HttpResponseListener listener) {
        super(API_COMMAND, mUri, mContext, listener);
    }

    @Override
    protected HttpRequestBase getRequest() {
        if (Constants.DEBUG) {
            Log.w(Constants.DELETE_URL, mUri);
        }
        HttpDeleteWithBody httpDeleteWithBody = new HttpDeleteWithBody(mUri);
        // TODO: Set entity
        return httpDeleteWithBody;
    }
}