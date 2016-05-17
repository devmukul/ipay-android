package bd.com.ipay.ipayskeleton.Api;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.HomeActivity;
import bd.com.ipay.ipayskeleton.Activities.SignupOrLoginActivity;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.LoginResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class HttpRequestPostAsyncTask extends AsyncTask<Void, Void, HttpResponseObject> {

    public HttpResponseListener mHttpResponseListener;

    private String mJsonString;
    private String mUri;
    private Context mContext;
    private String API_COMMAND;
    private HttpResponse mHttpResponse;

    boolean error = false;

    public HttpRequestPostAsyncTask(String API_COMMAND, String mUri, String mJsonString,
                                    Context mContext, HttpResponseListener listener) {
        this.API_COMMAND = API_COMMAND;
        this.mUri = mUri;
        this.mJsonString = mJsonString;
        this.mContext = mContext;
        this.mHttpResponseListener = listener;
    }

    public HttpRequestPostAsyncTask(String API_COMMAND, String mUri, String mJsonString, Context mContext) {
        this(API_COMMAND, mUri, mJsonString, mContext, null);
    }

    @Override
    protected HttpResponseObject doInBackground(Void... params) {
        if (Constants.DEBUG) {
            Log.w("POST_URL", mUri);
            Log.w("json", mJsonString);
        }

        if (Utilities.isConnectionAvailable(mContext))
            mHttpResponse = makeRequest(mUri, mJsonString);
        else {
            error = true;
            return null;
        }

        InputStream inputStream = null;
        HttpResponseObject mHttpResponseObject = null;

        try {
            HttpEntity entity = mHttpResponse.getEntity();
            int status = mHttpResponse.getStatusLine().getStatusCode();
            Header[] headers = mHttpResponse.getAllHeaders();

            if (headers.length > 0) {
                for (Header header : headers) {
                    if (header.getName().equals(Constants.TOKEN)) {
                        HomeActivity.iPayToken = header.getValue();
                        HomeActivity.iPayTokenTimeInMs = Utilities.getTimeFromBase64Token(HomeActivity.iPayToken);

                        if (HomeActivity.tokenTimer != null) {
                            HomeActivity.tokenTimer.cancel();
                            HomeActivity.tokenTimer.start();
                        }
                    } else if (header.getName().equals(Constants.REFRESH_TOKEN)) {
                        HomeActivity.iPayRefreshToken = header.getValue();
                        Log.d(Constants.REFRESH_TOKEN, HomeActivity.iPayRefreshToken);
                    }
                }
            }

            inputStream = entity.getContent();
            // json is UTF-8 by default
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            mHttpResponseObject = new HttpResponseObject();
            mHttpResponseObject.setStatus(status);
            mHttpResponseObject.setApiCommand(API_COMMAND);
            mHttpResponseObject.setJsonString(sb.toString());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (Exception squish) {
                squish.printStackTrace();
            }
        }

        return mHttpResponseObject;
    }

    @Override
    protected void onPostExecute(final HttpResponseObject result) {
        if (error) {
            Toast.makeText(mContext, R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            return;
        }

        if (Constants.DEBUG) {
            if (result == null)
                Log.e(Constants.RESULT, API_COMMAND + " NULL");
            else
                Log.w(Constants.RESULT, Constants.POST_REQUEST + result.toString());
        }

        if (result != null) {
            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_UNAUTHORIZED) {
                // In case of un-authorization go to login activity
                String message = mContext.getString(R.string.please_log_in_again);

                try {
                    Gson gson = new Gson();
                    message = gson.fromJson(result.getJsonString(), LoginResponse.class).getMessage();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    // Switch back to login activity because the user is unauthorized
                    Intent intent = new Intent(mContext, SignupOrLoginActivity.class);
                    intent.putExtra(Constants.MESSAGE, message);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mContext.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                if (mHttpResponseListener != null)
                    mHttpResponseListener.httpResponseReceiver(result);
            }

        } else {
            if (mHttpResponseListener != null)
                mHttpResponseListener.httpResponseReceiver(null);
        }

    }

    @Override
    protected void onCancelled() {
        mHttpResponseListener.httpResponseReceiver(null);
    }

    public static HttpResponse makeRequest(String uri, String json) {
        try {
            HttpPost httpPost = new HttpPost(uri);
            httpPost.setEntity(new StringEntity(json));
            if (HomeActivity.iPayToken.length() > 0)
                httpPost.setHeader(Constants.TOKEN, HomeActivity.iPayToken);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("User-Agent", "mobile-android");
            httpPost.setHeader("Content-type", "application/json");
            return new DefaultHttpClient().execute(httpPost);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}