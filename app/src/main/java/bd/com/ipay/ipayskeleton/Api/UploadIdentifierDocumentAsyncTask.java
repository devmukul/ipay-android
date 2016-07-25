package bd.com.ipay.ipayskeleton.Api;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.nio.charset.Charset;

import bd.com.ipay.ipayskeleton.Activities.SignupOrLoginActivity;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.TokenManager;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class UploadIdentifierDocumentAsyncTask extends AsyncTask<Void, Void, HttpResponseObject> {

    private final Context mContext;
    private final String imagePath;
    private final String API_COMMAND;
    private final String documentIdNumber;
    private final String documentType;

    public HttpResponseListener mHttpResponseListener;

    public UploadIdentifierDocumentAsyncTask(String API_COMMAND, String imagePath, Context mContext,
                                             String documentIdNumber, String documentType) {
        this.mContext = mContext;
        this.imagePath = imagePath;
        this.API_COMMAND = API_COMMAND;
        this.documentIdNumber = documentIdNumber;
        this.documentType = documentType;
    }

    @Override
    protected HttpResponseObject doInBackground(Void... params) {

        Log.w("Document Upload", "Started");

        HttpResponseObject mHttpResponseObject = new HttpResponseObject();

        if (Utilities.isConnectionAvailable(mContext))
            mHttpResponseObject = uploadDocument(imagePath);
        else
            Toast.makeText(mContext, "Please check your internet connection", Toast.LENGTH_LONG).show();

        Log.w("Document Upload", "Finished");

        return mHttpResponseObject;
    }

    @Override
    protected void onCancelled() {
        mHttpResponseListener.httpResponseReceiver(null);
    }

    @Override
    protected void onPostExecute(final HttpResponseObject result) {

        if (result != null) {
            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_UNAUTHORIZED) {
                // In case of un-authorization go to login activity
                Intent intent = new Intent(mContext, SignupOrLoginActivity.class);
                mContext.startActivity(intent);

            } else
                mHttpResponseListener.httpResponseReceiver(result);

        } else mHttpResponseListener.httpResponseReceiver(null);

    }

    private HttpResponseObject uploadDocument(String selectedImagePath) {

        try {
            HttpClient client = new DefaultHttpClient();
            File file = new File(selectedImagePath);
            HttpPost post = new HttpPost(Constants.BASE_URL_MM + Constants.URL_UPLOAD_DOCUMENTS);

            if (TokenManager.isTokenExists())
                post.setHeader(Constants.TOKEN, TokenManager.getToken());
            if (TokenManager.isEmployerAccountActive())
                post.setHeader(Constants.OPERATING_ON_ACCOUNT_ID, TokenManager.getOperatingOnAccountId());

            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE,
                    Constants.BOUNDARY, Charset.defaultCharset());

            entity.addPart(Constants.MULTIPART_FORM_DATA_NAME, new FileBody(file));
            entity.addPart(Constants.DOCUMENT_ID_NUMBER, new StringBody(documentIdNumber));
            entity.addPart(Constants.DOCUMENT_TYPE, new StringBody(documentType));
            post.setEntity(entity);

            post.setHeader("Accept", "application/json");
            post.setHeader("Content-Type", "multipart/form-data; boundary=" + Constants.BOUNDARY);

            HttpResponse response = client.execute(post);
            HttpEntity httpEntity = response.getEntity();

            Log.e("POST", post.toString());

            int status = response.getStatusLine().getStatusCode();

            HttpResponseObject mHttpResponseObject = new HttpResponseObject();
            mHttpResponseObject.setStatus(status);
            mHttpResponseObject.setApiCommand(API_COMMAND);
            mHttpResponseObject.setJsonString(EntityUtils.toString(httpEntity));

            Log.e("Result", mHttpResponseObject.toString());

            return mHttpResponseObject;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}