package bd.com.ipay.ipayskeleton.Api.DocumentUploadApi;

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
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Logger;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.TokenManager;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class UploadTicketAttachmentAsyncTask extends AsyncTask<Void, Void, GenericHttpResponse> {

    private final Context mContext;
    private final String filePath;
    private final String API_COMMAND;
    private long ticketId;
    private long commentId;
    private String comment;

    public HttpResponseListener mHttpResponseListener;

    public UploadTicketAttachmentAsyncTask(String API_COMMAND, String filePath, long commentId, Context mContext) {
        this.mContext = mContext;
        this.filePath = filePath;
        this.API_COMMAND = API_COMMAND;
        this.commentId = commentId;
    }

    @Override
    protected GenericHttpResponse doInBackground(Void... params) {
        Logger.logW("Document Upload", "Started");

        GenericHttpResponse mGenericHttpResponse = new GenericHttpResponse();

        if (Utilities.isConnectionAvailable(mContext))
            mGenericHttpResponse = uploadDocument(filePath);
        else
            Toaster.makeText(mContext, "Please check your internet connection", Toast.LENGTH_LONG);

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

            post = new HttpPost(Constants.BASE_URL_ADMIN + Constants.URL_UPLOAD_TICKET_ATTACHMENT);

            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE,
                    Constants.BOUNDARY, Charset.defaultCharset());

            entity.addPart(Constants.TICKET_COMMENT_ID, new StringBody(commentId + ""));

            if (file.exists())
                entity.addPart(Constants.MULTIPART_FORM_DATA_NAME, new FileBody(file));
            post.setEntity(entity);

            Logger.logE("POST", entity.toString());

            if (TokenManager.isTokenExists())
                post.setHeader(Constants.TOKEN, TokenManager.getToken());
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
