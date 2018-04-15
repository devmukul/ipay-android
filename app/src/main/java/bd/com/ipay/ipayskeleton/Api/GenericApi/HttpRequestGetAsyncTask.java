package bd.com.ipay.ipayskeleton.Api.GenericApi;

import android.content.Context;

import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Logger;
import bd.com.ipay.ipayskeleton.Utilities.TokenManager;

public class HttpRequestGetAsyncTask extends HttpRequestAsyncTask {

    public HttpRequestGetAsyncTask(String API_COMMAND, String mUri, Context mContext, boolean isSilent) {
        this(API_COMMAND, mUri, mContext, null, isSilent);
    }

    public HttpRequestGetAsyncTask(String API_COMMAND, String mUri, Context mContext, HttpResponseListener listener, boolean isSilent) {
        super(API_COMMAND, mUri, mContext, listener, isSilent);
    }

    @Override
    protected okhttp3.Request getRequest() {
        Logger.logW(Constants.GET_URL, mUri);
        okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder().
                header(Constants.USER_AGENT, Constants.USER_AGENT_MOBILE_ANDROID)
                .header("Accept", "application/json")
                .header("Content-type", "application/json")
                .header(Constants.TOKEN, TokenManager.getToken())
                .get()
                .url(mUri);
        if (TokenManager.getOnAccountId() != null && TokenManager.getOnAccountId() != "") {
            requestBuilder.header(Constants.OPERATING_ON_ACCOUNT_ID, TokenManager.getOnAccountId());
        }
        return requestBuilder.build();
    }

}