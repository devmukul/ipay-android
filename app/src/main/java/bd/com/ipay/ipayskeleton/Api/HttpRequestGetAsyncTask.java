package bd.com.ipay.ipayskeleton.Api;

import android.content.Context;
import android.util.Log;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class HttpRequestGetAsyncTask extends HttpRequestAsyncTask {

    public HttpRequestGetAsyncTask(String API_COMMAND, String mUri, Context mContext) {
        this(API_COMMAND, mUri, mContext, null);
    }

    public HttpRequestGetAsyncTask(String API_COMMAND, String mUri, Context mContext, HttpResponseListener listener) {
        super(API_COMMAND, mUri, mContext, listener);
    }

    @Override
    protected HttpRequestBase getRequest() {
        if (Constants.DEBUG) {
            Log.w(Constants.GET_URL, mUri);
        }
        return new HttpGet(mUri);
    }


}