package bd.com.ipay.ipayskeleton.Api.DocumentUploadApi;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;

import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.OkHttpResponse;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.MyApplication;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Logger;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.TokenManager;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
        File file = new File(selectedImagePath);
        GenericHttpResponse genericHttpResponse = new GenericHttpResponse();
        final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        try {
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);
            builder.addFormDataPart("file", file.getName(), okhttp3.RequestBody.create(MEDIA_TYPE_PNG, file))
                    .addFormDataPart(Constants.TICKET_COMMENT_ID, commentId + "");

            Request.Builder requestBuilder = new Request.Builder().
                    header("Accept", "application/json")
                    .header("Content-Type", "multipart/form-data");
            if (TokenManager.getToken() != null) {
                requestBuilder.header(Constants.TOKEN, TokenManager.getToken());
            }
            if (TokenManager.getOnAccountId() != null && TokenManager.getOnAccountId() != "") {
                requestBuilder.header(Constants.OPERATING_ON_ACCOUNT_ID, TokenManager.getOnAccountId());
            }
            RequestBody requestBody = builder.build();
            Request request = requestBuilder.url(Constants.BASE_URL_ADMIN + Constants.URL_UPLOAD_TICKET_ATTACHMENT)
                    .post(requestBody)
                    .build();
            OkHttpClient okHttpClient = new OkHttpClient();
            Response response = okHttpClient.newCall(request).execute();
            OkHttpResponse okHttpResponse = new OkHttpResponse();
            okHttpResponse.setResponse(response);
            String jsonString;
            jsonString = response.body().string();
            genericHttpResponse.setApiCommand(API_COMMAND);
            genericHttpResponse.setStatus(response.code());
            genericHttpResponse.setJsonString(jsonString);

        } catch (Exception e) {
            e.printStackTrace();

        }
        return genericHttpResponse;
    }

}
