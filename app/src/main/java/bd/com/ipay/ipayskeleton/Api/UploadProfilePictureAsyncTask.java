package bd.com.ipay.ipayskeleton.Api;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.nio.charset.Charset;

import bd.com.ipay.ipayskeleton.Activities.SignupOrLoginActivity;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.TokenManager;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class UploadProfilePictureAsyncTask extends AsyncTask<Void, Void, HttpResponseObject> {

    private final Context mContext;
    private final String imagePath;
    private final String API_COMMAND;
    public HttpResponseListener mHttpResponseListener;

    public UploadProfilePictureAsyncTask(String API_COMMAND, String imagePath, Context mContext) {
        this.mContext = mContext;
        this.imagePath = imagePath;
        this.API_COMMAND = API_COMMAND;
    }
    
    @Override
    protected HttpResponseObject doInBackground(Void... params) {

        HttpResponseObject mHttpResponseObject = null;

        if (Utilities.isConnectionAvailable(mContext))
            mHttpResponseObject = uploadImage(imagePath);
        else
            return null;

        return mHttpResponseObject;
    }

    @Override
    protected void onCancelled() {
        mHttpResponseListener.httpResponseReceiver(null);
    }

    @Override
    protected void onPostExecute(final HttpResponseObject result) {

        if (result != null) {
            if (Constants.DEBUG)
                Log.w("Image Upload", result.toString());

            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_UNAUTHORIZED) {
                // In case of un-authorization go to login activity
                Intent intent = new Intent(mContext, SignupOrLoginActivity.class);
                mContext.startActivity(intent);

            } else
                mHttpResponseListener.httpResponseReceiver(result);

        } else {
            if (Constants.DEBUG)
                Log.w("Image Upload", "NULL");

            mHttpResponseListener.httpResponseReceiver(null);
        }

    }

    private HttpResponseObject uploadImage(String selectedImagePath) {
        if (Constants.DEBUG)
            Log.w("Uploading image", selectedImagePath);

        try {
            HttpClient client = new DefaultHttpClient();
            File file = new File(selectedImagePath);
            HttpPost post = new HttpPost(Constants.BASE_URL_MM + Constants.URL_SET_PROFILE_PICTURE);

            if (TokenManager.isTokenExists())
                post.setHeader(Constants.TOKEN, TokenManager.getToken());
            if (TokenManager.isEmployerAccountActive())
                post.setHeader(Constants.OPERATING_ON_ACCOUNT_ID, TokenManager.getOperatingOnAccountId());

            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE,
                    Constants.BOUNDARY, Charset.defaultCharset());

            entity.addPart(Constants.MULTIPART_FORM_DATA_NAME, new FileBody(file));
            post.setHeader("Accept", "application/json");
            post.setHeader("Content-Type", "multipart/form-data; boundary=" + Constants.BOUNDARY);

            post.setEntity(entity);

            HttpResponse response = client.execute(post);
            HttpEntity httpEntity = response.getEntity();

            int status = response.getStatusLine().getStatusCode();

            HttpResponseObject mHttpResponseObject = new HttpResponseObject();
            mHttpResponseObject.setStatus(status);
            mHttpResponseObject.setApiCommand(API_COMMAND);
            mHttpResponseObject.setJsonString(EntityUtils.toString(httpEntity));

            return mHttpResponseObject;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}