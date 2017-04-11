package bd.com.ipay.ipayskeleton.Api.GenericApi;

import android.content.Context;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.io.UnsupportedEncodingException;

import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.HttpDeleteWithBody;
import bd.com.ipay.ipayskeleton.Utilities.ToastandLogger.Logger;

public class HttpDeleteWithBodyAsyncTask extends HttpRequestAsyncTask {

    private final String mJsonString;

    public HttpDeleteWithBodyAsyncTask(String API_COMMAND, String mUri, String mJsonString,
                                       Context mContext, HttpResponseListener listener) {
        super(API_COMMAND, mUri, mContext, listener);
        this.mJsonString = mJsonString;
    }

    @Override
    protected HttpRequestBase getRequest() {
        Logger.logW(Constants.DELETE_URL, mUri);

        HttpDeleteWithBody httpDeleteWithBody = new HttpDeleteWithBody(mUri);
        try {
            httpDeleteWithBody.setEntity(new StringEntity(mJsonString, HTTP.UTF_8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return httpDeleteWithBody;
    }
}