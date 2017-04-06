package bd.com.ipay.ipayskeleton.Api.GenericApi;

import android.content.Context;
import android.util.Log;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

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
    protected HttpRequestBase getRequest() {
        if (Constants.DEBUG) {
            Log.w("POST_URL", mUri);
            if (mJsonString != null)
                Log.w("json", mJsonString);
        }

        HttpPost httpPost = new HttpPost(mUri);

        try {
            if (mJsonString != null) {
                httpPost.setEntity(new StringEntity(mJsonString, HTTP.UTF_8));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return httpPost;
    }
}