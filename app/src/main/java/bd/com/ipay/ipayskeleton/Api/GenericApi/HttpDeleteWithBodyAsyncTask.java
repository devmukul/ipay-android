package bd.com.ipay.ipayskeleton.Api.GenericApi;

import android.content.Context;

import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Logger;
import bd.com.ipay.ipayskeleton.Utilities.TokenManager;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpDeleteWithBodyAsyncTask extends HttpRequestAsyncTask {

    private final String mJsonString;

    public HttpDeleteWithBodyAsyncTask(String API_COMMAND, String mUri, String mJsonString,
                                       Context mContext, HttpResponseListener listener,boolean isSilent) {
        super(API_COMMAND, mUri, mContext, listener,isSilent);
        this.mJsonString = mJsonString;
    }

    @Override
    protected okhttp3.Request getRequest() {
        Logger.logW(Constants.DELETE_URL, mUri);

        MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, mJsonString);
        if (mJsonString != null)
            Logger.logW("json", mJsonString);
        Request.Builder requestBuilder = new Request.Builder().
                header(Constants.USER_AGENT, Constants.USER_AGENT_MOBILE_ANDROID)
                .header("Accept", "application/json")
                .header("Content-type", "application/json")
                .header(Constants.TOKEN, TokenManager.getToken())
                .delete(requestBody)
                .url(mUri);
        if (TokenManager.getOnAccountId() != null && TokenManager.getOnAccountId() != "") {
            requestBuilder.header(Constants.OPERATING_ON_ACCOUNT_ID, TokenManager.getOnAccountId());
        }
        return requestBuilder.build();
    }
}