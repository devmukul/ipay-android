package bd.com.ipay.ipayskeleton.Api.GenericApi;

import android.content.Context;

import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Logger;

public class HttpRequestGetAsyncTask extends HttpRequestAsyncTask {

    public HttpRequestGetAsyncTask(String API_COMMAND, String mUri, Context mContext) {
        this(API_COMMAND, mUri, mContext, null);
    }

    public HttpRequestGetAsyncTask(String API_COMMAND, String mUri, Context mContext, HttpResponseListener listener) {
        super(API_COMMAND, mUri, mContext, listener);
    }

    @Override
    protected okhttp3.Request getRequest() {
        Logger.logW(Constants.GET_URL, mUri);
        okhttp3.Request request = new okhttp3.Request.Builder().
                header(Constants.USER_AGENT, Constants.USER_AGENT_MOBILE_ANDROID)
                .header("Accept", "application/json")
                .header("Content-type", "application/json")
                .header(Constants.TOKEN,"")
                .header(Constants.OPERATING_ON_ACCOUNT_ID,"")
                .get()
                .url(mUri)
                .build();
        return request;
    }

}