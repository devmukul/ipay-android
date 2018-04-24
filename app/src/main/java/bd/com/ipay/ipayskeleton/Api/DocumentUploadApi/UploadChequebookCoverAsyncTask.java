package bd.com.ipay.ipayskeleton.Api.DocumentUploadApi;

import android.content.Context;
import android.os.AsyncTask;

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
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.MyApplication;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Logger;
import bd.com.ipay.ipayskeleton.Utilities.TokenManager;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class UploadChequebookCoverAsyncTask extends AsyncTask<Void, Void, GenericHttpResponse> {

    private final Context mContext;
    private final String API_COMMAND;
    private final String mUrl;
    private final String[] mImagePath;
    private final String documentType;
    private final long bankId;


    public HttpResponseListener mHttpResponseListener;

    public UploadChequebookCoverAsyncTask(String API_COMMAND, String url, Context context, String documentType, String[] imagePath, HttpResponseListener httpResponseListener, long bankId) {
        this.mContext = context;
        this.mUrl = url;
        this.mHttpResponseListener = httpResponseListener;
        this.API_COMMAND = API_COMMAND;
        this.documentType = documentType;

        this.mImagePath = imagePath;
        this.bankId = bankId;
    }

    @Override
    protected GenericHttpResponse doInBackground(Void... params) {
        Logger.logW("Document Upload", "Started");

        GenericHttpResponse mGenericHttpResponse = new GenericHttpResponse();

        if (Utilities.isConnectionAvailable(mContext))
            mGenericHttpResponse = uploadDocument(mImagePath);

        Logger.logW("Document Upload", "Finished");

        return mGenericHttpResponse;
    }

    @Override
    protected void onCancelled() {
        mHttpResponseListener.httpResponseReceiver(null);
    }

    @Override
    protected void onPostExecute(final GenericHttpResponse result) {

        if (HttpErrorHandler.isErrorFound(result, mContext, null)) {
            return;
        }

        if (result != null) {
            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_UNAUTHORIZED) {
                MyApplication myApplicationInstance = MyApplication.getMyApplicationInstance();
                myApplicationInstance.launchLoginPage(null);
            } else
                mHttpResponseListener.httpResponseReceiver(result);

        } else mHttpResponseListener.httpResponseReceiver(null);

    }

    private GenericHttpResponse uploadDocument(String[] selectedImagePath) {

        try {
            Logger.logE("CHECK_POST", mUrl);
            HttpClient client = new DefaultHttpClient();
            File[] files = new File[selectedImagePath.length];
            for (int i = 0; i < selectedImagePath.length; i++) {
                files[i] = new File(selectedImagePath[i]);
            }
            HttpPost post = new HttpPost(mUrl);
            if (TokenManager.isTokenExists())
                post.setHeader(Constants.TOKEN, TokenManager.getToken());
            if (TokenManager.isEmployerAccountActive())
                post.setHeader(Constants.OPERATING_ON_ACCOUNT_ID, TokenManager.getOnAccountId());

            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE,
                    Constants.BOUNDARY, Charset.defaultCharset());

            for (File file : files) {
                entity.addPart("files", new FileBody(file));
            }
            entity.addPart(Constants.DOCUMENT_TYPE, new StringBody(documentType));
            entity.addPart(Constants.BANK_ID, new StringBody(String.valueOf(bankId)));
            post.setEntity(entity);

            post.setHeader("Accept", "application/json");
            post.setHeader("Content-Type", "multipart/form-data; boundary=" + Constants.BOUNDARY);

            HttpResponse response = client.execute(post);
            HttpEntity httpEntity = response.getEntity();

            Logger.logE("CHECK_POST", post.toString());

            int status = response.getStatusLine().getStatusCode();

            GenericHttpResponse mGenericHttpResponse = new GenericHttpResponse();
            mGenericHttpResponse.setStatus(status);
            mGenericHttpResponse.setApiCommand(API_COMMAND);
            mGenericHttpResponse.setJsonString(EntityUtils.toString(httpEntity));
            mGenericHttpResponse.setSilent(false);

            Logger.logE("CHECK_Result", mGenericHttpResponse.toString());

            return mGenericHttpResponse;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}