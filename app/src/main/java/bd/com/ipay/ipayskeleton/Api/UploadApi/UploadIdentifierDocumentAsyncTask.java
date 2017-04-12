package bd.com.ipay.ipayskeleton.Api.UploadApi;

import android.content.Context;
import android.os.AsyncTask;
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

import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.MyApplication;
import bd.com.ipay.ipayskeleton.Utilities.ToastandLogger.Logger;
import bd.com.ipay.ipayskeleton.Utilities.ToastandLogger.ToastWrapper;
import bd.com.ipay.ipayskeleton.Utilities.TokenManager;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class UploadIdentifierDocumentAsyncTask extends AsyncTask<Void, Void, GenericHttpResponse> {

    private final Context mContext;
    private final String imagePath;
    private final String API_COMMAND;
    private final String documentIdNumber;
    private final String documentType;
    private int uploadType;

    public HttpResponseListener mHttpResponseListener;

    private static final int OPTION_UPLOAD_TYPE_PERSONAL_DOCUMENT = 1;
    private static final int OPTION_UPLOAD_TYPE_BUSINESS_DOCUMENT = 2;

    public UploadIdentifierDocumentAsyncTask(String API_COMMAND, String imagePath, Context mContext,
                                             String documentIdNumber, String documentType) {
        this.mContext = mContext;
        this.imagePath = imagePath;
        this.API_COMMAND = API_COMMAND;
        this.documentIdNumber = documentIdNumber;
        this.documentType = documentType;
    }

    public UploadIdentifierDocumentAsyncTask(String API_COMMAND, String imagePath, Context mContext,
                                             String documentIdNumber, String documentType, int uploadType) {
        this.mContext = mContext;
        this.imagePath = imagePath;
        this.API_COMMAND = API_COMMAND;
        this.documentIdNumber = documentIdNumber;
        this.documentType = documentType;
        this.uploadType = uploadType;
    }

    @Override
    protected GenericHttpResponse doInBackground(Void... params) {
        Logger.logW("Document Upload", "Started");

        GenericHttpResponse mGenericHttpResponse = new GenericHttpResponse();

        if (Utilities.isConnectionAvailable(mContext))
            mGenericHttpResponse = uploadDocument(imagePath);
        else
            ToastWrapper.makeText(mContext, "Please check your internet connection", Toast.LENGTH_LONG);

        Logger.logW("Document Upload", "Finished");

        return mGenericHttpResponse;
    }

    @Override
    protected void onCancelled() {
        mHttpResponseListener.httpResponseReceiver(null);
    }

    @Override
    protected void onPostExecute(final GenericHttpResponse result) {

        if (result != null) {
            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_UNAUTHORIZED) {
                MyApplication myApplicationInstance = MyApplication.getMyApplicationInstance();
                myApplicationInstance.launchLoginPage(null);
            } else
                mHttpResponseListener.httpResponseReceiver(result);

        } else mHttpResponseListener.httpResponseReceiver(null);

    }

    private GenericHttpResponse uploadDocument(String selectedImagePath) {

        try {
            HttpClient client = new DefaultHttpClient();
            File file = new File(selectedImagePath);
            HttpPost post = null;
            if (uploadType == OPTION_UPLOAD_TYPE_PERSONAL_DOCUMENT)
                post = new HttpPost(Constants.BASE_URL_MM + Constants.URL_UPLOAD_DOCUMENTS);

            else if (uploadType == OPTION_UPLOAD_TYPE_BUSINESS_DOCUMENT) {
                post = new HttpPost(Constants.BASE_URL_MM + Constants.URL_UPLOAD_BUSINESS_DOCUMENTS);
            }

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

            Logger.logE("POST", post.toString());

            int status = response.getStatusLine().getStatusCode();

            GenericHttpResponse mGenericHttpResponse = new GenericHttpResponse();
            mGenericHttpResponse.setStatus(status);
            mGenericHttpResponse.setApiCommand(API_COMMAND);
            mGenericHttpResponse.setJsonString(EntityUtils.toString(httpEntity));

            Logger.logE("Result", mGenericHttpResponse.toString());

            return mGenericHttpResponse;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}