package bd.com.ipay.ipayskeleton.Api.UploadApi;

import android.content.Context;
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

import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.MyApplication;
import bd.com.ipay.ipayskeleton.Utilities.TokenManager;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class UploadProfilePictureAsyncTask extends AsyncTask<Void, Void, GenericHttpResponse> {

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
    protected GenericHttpResponse doInBackground(Void... params) {

        GenericHttpResponse mGenericHttpResponse;

        if (Utilities.isConnectionAvailable(mContext))
            mGenericHttpResponse = uploadImage(imagePath);
        else
            return null;

        return mGenericHttpResponse;
    }

    @Override
    protected void onCancelled() {
        mHttpResponseListener.httpResponseReceiver(null);
    }

    @Override
    protected void onPostExecute(final GenericHttpResponse result) {

        if (result != null) {
            if (Constants.DEBUG)
                Log.w("Image Upload", result.toString());

            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_UNAUTHORIZED) {
                MyApplication myApplicationInstance = MyApplication.getMyApplicationInstance();
                myApplicationInstance.launchLoginPage(null);
            } else
                mHttpResponseListener.httpResponseReceiver(result);

        } else {
            if (Constants.DEBUG)
                Log.w("Image Upload", "NULL");

            mHttpResponseListener.httpResponseReceiver(null);
        }

    }

    private GenericHttpResponse uploadImage(String selectedImagePath) {
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

            GenericHttpResponse mGenericHttpResponse = new GenericHttpResponse();
            mGenericHttpResponse.setStatus(status);
            mGenericHttpResponse.setApiCommand(API_COMMAND);
            mGenericHttpResponse.setJsonString(EntityUtils.toString(httpEntity));

            return mGenericHttpResponse;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}