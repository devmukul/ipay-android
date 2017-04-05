package bd.com.ipay.ipayskeleton.Api;

import android.content.Context;
import android.util.Log;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.HttpPatchRequestBase;

public class HttpRequestPatchAsyncTask extends HttpRequestAsyncTask {

    private final String mJsonString;

    public HttpRequestPatchAsyncTask(String API_COMMAND, String mUri, String mJsonString,
                                    Context mContext, HttpResponseListener listener) {
        super(API_COMMAND, mUri, mContext, listener);
        this.mJsonString = mJsonString;
    }

    public HttpRequestPatchAsyncTask(String API_COMMAND, String mUri, String mJsonString, Context mContext) {
        this(API_COMMAND, mUri, mJsonString, mContext, null);
    }

    @Override
    protected HttpRequestBase getRequest() {
        if (Constants.DEBUG) {
            Log.w("POST_URL", mUri);
            if (mJsonString != null)
                Log.w("json", mJsonString);
        }

        HttpPatchRequestBase httpPatch = new HttpPatchRequestBase(mUri);

        try {
            if (mJsonString != null) {
                httpPatch.setEntity(new StringEntity(mJsonString, HTTP.UTF_8));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return httpPatch;
    }
}
