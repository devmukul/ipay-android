package bd.com.ipay.ipayskeleton.Api.GenericApi;

import android.content.Context;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpRequestBase;

import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ToastandLogger.Logger;

public class HttpRequestDeleteAsyncTask extends HttpRequestAsyncTask {

    public HttpRequestDeleteAsyncTask(String API_COMMAND, String mUri, Context mContext) {
        this(API_COMMAND, mUri, mContext, null);
    }

    public HttpRequestDeleteAsyncTask(String API_COMMAND, String mUri, Context mContext, HttpResponseListener listener) {
        super(API_COMMAND, mUri, mContext, listener);
    }

    @Override
    protected HttpRequestBase getRequest() {
        Logger.logW(Constants.DELETE_URL, mUri);
        return new HttpDelete(mUri);
    }
}