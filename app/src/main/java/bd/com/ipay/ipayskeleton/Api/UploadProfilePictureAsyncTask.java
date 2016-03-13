package bd.com.ipay.ipayskeleton.Api;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

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
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.HomeActivity;
import bd.com.ipay.ipayskeleton.Activities.SignupOrLoginActivity;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class UploadProfilePictureAsyncTask extends AsyncTask<Void, Void, String> {

    private Context mContext;
    private String imagePath;
    private String API_COMMAND;
    public HttpResponseListener mHttpResponseListener;

    public UploadProfilePictureAsyncTask(String API_COMMAND, String imagePath, Context mContext) {
        this.mContext = mContext;
        this.imagePath = imagePath;
        this.API_COMMAND = API_COMMAND;
    }

    @Override
    protected String doInBackground(Void... params) {

        String result = null;

        if (Utilities.isConnectionAvailable(mContext))
            result = uploadImage(imagePath);
        else
            Toast.makeText(mContext, "Please check your internet connection", Toast.LENGTH_LONG).show();

        return result;
    }

    @Override
    protected void onCancelled() {
        mHttpResponseListener.httpResponseReceiver(null);
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

    private String uploadImage(String selectedImagePath) {

        try {
            HttpClient client = new DefaultHttpClient();
            File file = new File(selectedImagePath);
            HttpPost post = new HttpPost(Constants.BASE_URL_POST_MM + Constants.URL_SET_PROFILE_PICTURE);

            if (HomeActivity.iPayToken.length() > 0)
                post.setHeader(Constants.TOKEN, HomeActivity.iPayToken);
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE,
                    Constants.BOUNDARY, Charset.defaultCharset());

            entity.addPart(Constants.MULTIPART_FORM_DATA_NAME, new FileBody(file));
            post.setHeader("Accept", "application/json");
            post.setHeader("Content-Type", "multipart/form-data; boundary=" + Constants.BOUNDARY);

            post.setEntity(entity);

            HttpResponse response = client.execute(post);
            HttpEntity httpEntity = response.getEntity();

            int status = response.getStatusLine().getStatusCode();
            String result = null;

            result = API_COMMAND + ";" + status + ";" + EntityUtils.toString(httpEntity);

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}