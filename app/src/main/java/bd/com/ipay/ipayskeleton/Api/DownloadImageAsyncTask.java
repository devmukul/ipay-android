package bd.com.ipay.ipayskeleton.Api;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.HomeActivity;
import bd.com.ipay.ipayskeleton.Activities.SignupOrLoginActivity;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class DownloadImageAsyncTask extends AsyncTask<Void, Void, String> {

    public HttpResponseListener mHttpResponseListener;

    private String mJsonString;
    private String mUri;
    private String mUserID;
    private Context mContext;
    private String API_COMMAND;
    private HttpResponse mHttpResponse;

    public DownloadImageAsyncTask(String API_COMMAND, String mUri, String mUserID, String mJsonString, Context mContext) {
        this.API_COMMAND = API_COMMAND;
        this.mUri = mUri;
        this.mUserID = mUserID;
        this.mJsonString = mJsonString;
        this.mContext = mContext;
    }

    @Override
    protected String doInBackground(Void... params) {

        if (Utilities.isConnectionAvailable(mContext))
            mHttpResponse = makeRequest(mUri, mJsonString);
        else
            Toast.makeText(mContext, R.string.no_internet_connection, Toast.LENGTH_LONG).show();

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
            try {
                File dir = new File(Environment.getExternalStorageDirectory().getPath()
                        + Constants.PICTURE_FOLDER);
                if (!dir.exists()) dir.mkdir();

                File file = new File(dir, mUserID.replaceAll("[^0-9]", "") + ".jpg");

                FileOutputStream out = new FileOutputStream(file);
                byte[] buf = new byte[1024];
                int len;
                while ((len = inputStream.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            result = API_COMMAND + ";" + status;

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
                // In case of un-authorization go to login activity
                Intent intent = new Intent(mContext, SignupOrLoginActivity.class);
                mContext.startActivity(intent);

            } else
                mHttpResponseListener.httpResponseReceiver(result);

        } else mHttpResponseListener.httpResponseReceiver(null);

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