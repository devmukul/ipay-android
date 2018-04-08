package bd.com.ipay.ipayskeleton.Api.GenericApi;

import android.content.Context;

import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Logger;

public class HttpRequestPostAsyncTask extends HttpRequestAsyncTask {

    private final String mJsonString;


    public HttpRequestPostAsyncTask(String API_COMMAND, String mUri, String mJsonString,
                                      Context mContext, HttpResponseListener listener) {
        super(API_COMMAND, mUri, mContext, listener);
        this.mJsonString = mJsonString;
    }

    public HttpRequestPostAsyncTask(String API_COMMAND, String mUri, String mJsonString, Context mContext) {
        this(API_COMMAND, mUri, mJsonString, mContext, null);
    }

    @Override
    protected String getRequest() {
        Logger.logW("POST_URL", mUri);
        if (mJsonString != null)
            Logger.logW("json", mJsonString);
        return mJsonString;
    }
}