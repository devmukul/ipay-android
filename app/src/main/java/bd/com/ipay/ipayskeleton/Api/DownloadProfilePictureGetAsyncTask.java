package bd.com.ipay.ipayskeleton.Api;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.HomeActivity;
import bd.com.ipay.ipayskeleton.Activities.SignupOrLoginActivity;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo.GetUserInfoResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo.UserProfilePictureClass;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class DownloadProfilePictureGetAsyncTask extends AsyncTask<Void, Void, String> {

    private String mUri;
    private String mUserID;
    private Context mContext;
    private String API_COMMAND;
    private HttpResponse mHttpResponse;

    public DownloadProfilePictureGetAsyncTask(String API_COMMAND, String mUri, String mUserID, Context mContext) {
        this.API_COMMAND = API_COMMAND;
        this.mUri = mUri;
        this.mUserID = mUserID;
        this.mContext = mContext;
    }

    @Override
    protected String doInBackground(Void... params) {

        if (Utilities.isConnectionAvailable(mContext))
            mHttpResponse = makeRequest(mUri);
        else
            Toast.makeText(mContext, R.string.no_internet_connection, Toast.LENGTH_LONG).show();

        InputStream input = null;
        InputStream response = null;
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

            response = entity.getContent();
            // json is UTF-8 by default
            BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            Gson gson = new Gson();
            String profilePictureUrl = "";
            GetUserInfoResponse mGetUserInfoResponse = gson.fromJson(sb.toString(), GetUserInfoResponse.class);
            List<UserProfilePictureClass> profilePictures = mGetUserInfoResponse.getProfilePictures();

            if (profilePictures.size() > 0) {
                for (Iterator<UserProfilePictureClass> it = profilePictures.iterator(); it.hasNext(); ) {
                    UserProfilePictureClass userProfilePictureClass = it.next();
                    profilePictureUrl = userProfilePictureClass.getUrl();
                    break;
                }
            }

            // json is UTF-8 by default
            try {
                if (profilePictureUrl.length() > 0) {
                    File dir = new File(Environment.getExternalStorageDirectory().getPath()
                            + Constants.PICTURE_FOLDER);
                    if (!dir.exists()) dir.mkdir();

                    File file = new File(dir, mUserID.replaceAll("[^0-9]", "") + ".jpg");

                    URL url = new URL(Constants.BASE_URL_IMAGE_SERVER + profilePictureUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    input = connection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(input);

                    FileOutputStream stream = new FileOutputStream(file);

                    ByteArrayOutputStream outstream = new ByteArrayOutputStream();
                    myBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outstream);
                    byte[] byteArray = outstream.toByteArray();

                    stream.write(byteArray);
                    stream.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            result = API_COMMAND + ";" + status;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (input != null) input.close();
                if (response != null) response.close();
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
            }
        }

    }

    public static HttpResponse makeRequest(String uri) {
        try {
            HttpGet httpGet = new HttpGet(uri);
            if (HomeActivity.iPayToken.length() > 0)
                httpGet.setHeader(Constants.TOKEN, HomeActivity.iPayToken);
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