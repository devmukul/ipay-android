package bd.com.ipay.ipayskeleton.Api;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
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

public class HttpRequestGetAsyncTask extends AsyncTask<Void, Void, String> {

    public HttpResponseListener mHttpResponseListener;

    private String mUri;
    private Context mContext;
    private String API_COMMAND;
    private HttpResponse mHttpResponse;

    public HttpRequestGetAsyncTask(String API_COMMAND, String mUri, Context mContext) {
        this(API_COMMAND, mUri, mContext, null);
    }

    public HttpRequestGetAsyncTask(String API_COMMAND, String mUri, Context mContext, HttpResponseListener listener) {
        this.API_COMMAND = API_COMMAND;
        this.mUri = mUri;
        this.mContext = mContext;
        this.mHttpResponseListener = listener;
    }

    @Override
    protected String doInBackground(Void... params) {

        if (Utilities.isConnectionAvailable(mContext))
            mHttpResponse = makeRequest(mUri);
        else
            Toast.makeText(mContext, "Please check your internet connection", Toast.LENGTH_LONG).show();

        InputStream inputStream = null;
        String result = null;

        try {
            HttpEntity entity = mHttpResponse.getEntity();
            int status = mHttpResponse.getStatusLine().getStatusCode();
            Header[] headers = mHttpResponse.getAllHeaders();

            if (headers.length > 0) {
                for (Header header : headers) {
                    if (header.getName().equals(Constants.TOKEN)) {
                        HomeActivity.iPayToken = header.getValue();
                        break;
                    } else if (header.getName().equals(Constants.NEW_TOKEN)) {
                        HomeActivity.iPayToken = header.getValue();
                        break;
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
            result = API_COMMAND + ";" + status + ";" + sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (Exception squish) {
                squish.printStackTrace();
            }
        }

        return result;
    }

    @Override
    protected void onPostExecute(final String result) {

        if (result != null) {
            List<String> resultList = Arrays.asList(result.split(";"));

            if (resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_UNAUTHORIZED)) {
                String message = mContext.getString(R.string.please_log_in_again);
                if (resultList.size() > 2) {
                    try {
                        Gson gson = new Gson();
                        message = gson.fromJson(resultList.get(2), LoginResponse.class).getMessage();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }

                Intent intent = new Intent(mContext, SignupOrLoginActivity.class);
                intent.putExtra(SignupOrLoginActivity.MESSAGE, message);
                mContext.startActivity(intent);

            } else
                mHttpResponseListener.httpResponseReceiver(result);

        } else mHttpResponseListener.httpResponseReceiver(null);

    }

    @Override
    protected void onCancelled() {
        mHttpResponseListener.httpResponseReceiver(null);
    }

    public static HttpResponse makeRequest(String uri) {
        try {
            HttpGet httpGet = new HttpGet(uri);

            if (HomeActivity.iPayToken.length() > 0)
                httpGet.setHeader("token", HomeActivity.iPayToken);
            return new DefaultHttpClient().execute(httpGet);
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