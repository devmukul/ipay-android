package bd.com.ipay.ipayskeleton.Api;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.IOException;

import bd.com.ipay.ipayskeleton.Activities.SignupOrLoginActivity;
import bd.com.ipay.ipayskeleton.BuildConfig;
import bd.com.ipay.ipayskeleton.Model.MMModule.Configuration.ApiVersionResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.LoginResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.TokenManager;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public abstract class HttpRequestAsyncTask extends AsyncTask<Void, Void, HttpResponseObject> {

    public HttpResponseListener mHttpResponseListener;

    final String mUri;
    private final Context mContext;
    private final String API_COMMAND;
    private HttpResponse mHttpResponse;

    private boolean error = false;

    HttpRequestAsyncTask(String API_COMMAND, String mUri, Context mContext, HttpResponseListener listener) {
        this.API_COMMAND = API_COMMAND;
        this.mUri = mUri;
        this.mContext = mContext;
        this.mHttpResponseListener = listener;
    }

    @Override
    protected HttpResponseObject doInBackground(Void... params) {

        HttpResponseObject mHttpResponseObject = null;

        try {
            if (Utilities.isConnectionAvailable(mContext)) {
                if (Constants.IS_API_VERSION_CHECKED) {
                    mHttpResponse = makeRequest();
                    mHttpResponseObject = parseHttpResponse(mHttpResponse);
                    mHttpResponseObject.setUpdateNeeded(false);
                } else {
                    mHttpResponse = makeAPIVersionCheckRequest();
                    mHttpResponseObject = parseHttpResponse(mHttpResponse);

                    // Validate the API version and set whether the update is required or not
                    mHttpResponseObject = validateAPIVersion(mHttpResponseObject);
                }

            } else {
                if (Constants.DEBUG) Log.d(Constants.ERROR, API_COMMAND);
                error = true;
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mHttpResponseObject;
    }

    @Override
    protected void onPostExecute(final HttpResponseObject result) {
        if (error) {
            if (mContext != null)
                Toast.makeText(mContext, R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            return;
        }

        if (Constants.DEBUG) {
            if (result == null)
                Log.e(Constants.RESULT, API_COMMAND + " NULL");
            else
                Log.w(Constants.RESULT, Constants.GET_REQUEST + result.toString());
        }

        if (result != null) {

            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_UNAUTHORIZED) {
                String message = mContext.getString(R.string.please_log_in_again);

                try {
                    Gson gson = new Gson();
                    message = gson.fromJson(result.getJsonString(), LoginResponse.class).getMessage();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    // Stop the Token Timer here
                    CountDownTimer tokenTimer = TokenManager.getTokenTimer();
                    if (tokenTimer != null) {
                        tokenTimer.cancel();
                        TokenManager.setTokenTimer(null);
                    }

                    boolean loggedIn = ProfileInfoCacheManager.getLoggedInStatus(true);

                    if (loggedIn) {
                        // Set the preference first
                        ProfileInfoCacheManager.setLoggedInStatus(false);

                        // Switch back to login activity because the user is unauthorized
                        Intent intent = new Intent(mContext, SignupOrLoginActivity.class);
                        intent.putExtra(Constants.MESSAGE, message);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        mContext.startActivity(intent);

                    } else {
                        // Wrong user name or password returns HTTP_RESPONSE_STATUS_UNAUTHORIZED too
                        if (mHttpResponseListener != null)
                            mHttpResponseListener.httpResponseReceiver(result);
                    }
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

    private HttpResponse makeRequest() {
        try {
            HttpRequestBase httpRequest = getRequest();

            if (TokenManager.getToken().length() > 0)
                httpRequest.setHeader(Constants.TOKEN, TokenManager.getToken());
            if (TokenManager.isEmployerAccountActive())
                httpRequest.setHeader(Constants.OPERATING_ON_ACCOUNT_ID, TokenManager.getOperatingOnAccountId());
            httpRequest.setHeader(Constants.USER_AGENT, Constants.USER_AGENT_MOBILE_ANDROID);
            httpRequest.setHeader("Accept", "application/json");
            httpRequest.setHeader("Content-type", "application/json");

            HttpParams httpParams = new BasicHttpParams();
            HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);
            HttpProtocolParams.setHttpElementCharset(httpParams, HTTP.UTF_8);
            HttpClient client = new DefaultHttpClient(httpParams);

            return client.execute(httpRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private HttpResponse makeAPIVersionCheckRequest() {
        try {
            HttpRequestBase httpRequest = new HttpGet(Constants.BASE_URL_MM + Constants.URL_GET_MIN_API_VERSION_REQUIRED);

            httpRequest.setHeader(Constants.USER_AGENT, Constants.USER_AGENT_MOBILE_ANDROID);
            httpRequest.setHeader("Accept", "application/json");
            httpRequest.setHeader("Content-type", "application/json");

            HttpParams httpParams = new BasicHttpParams();
            HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);
            HttpProtocolParams.setHttpElementCharset(httpParams, HTTP.UTF_8);
            HttpClient client = new DefaultHttpClient(httpParams);

            return client.execute(httpRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private HttpResponseObject parseHttpResponse(HttpResponse mHttpResponse) {
        HttpResponseObject mHttpResponseObject = null;

        HttpResponseParser mHttpResponseParser = new HttpResponseParser();
        mHttpResponseParser.setAPI_COMMAND(API_COMMAND);
        mHttpResponseParser.setHttpResponse(mHttpResponse);
        mHttpResponseObject.setContext(mContext);

        mHttpResponseObject = mHttpResponseParser.parseHttpResponse();

        return mHttpResponseObject;
    }

    private HttpResponseObject validateAPIVersion(HttpResponseObject mHttpResponseObject) {

        Gson gson = new Gson();

        try {
            ApiVersionResponse mApiVersionResponse = gson.fromJson(mHttpResponseObject.getJsonString(), ApiVersionResponse.class);

            if (mHttpResponseObject.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                if (mApiVersionResponse != null) {
                    int requiredAPIVersion = mApiVersionResponse.getAndroid();
                    int availableAPIVersion = BuildConfig.VERSION_CODE;

                    if (availableAPIVersion < requiredAPIVersion) {
                        mHttpResponseObject.setUpdateNeeded(true);
                    } else {
                        Constants.IS_API_VERSION_CHECKED = true;
                        mHttpResponse = makeRequest();
                        mHttpResponseObject = parseHttpResponse(mHttpResponse);
                        mHttpResponseObject.setUpdateNeeded(false);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mHttpResponseObject;
    }

    Context getContext() {
        return mContext;
    }

    abstract protected HttpRequestBase getRequest();
}