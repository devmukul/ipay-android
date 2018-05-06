package bd.com.ipay.ipayskeleton.Api.DocumentUploadApi;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.MyApplication;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Logger;
import bd.com.ipay.ipayskeleton.Utilities.TokenManager;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadMultipleIdentifierDocumentAsyncTask extends AsyncTask<Void, Void, GenericHttpResponse> {

    private final Context mContext;
    private final String API_COMMAND;
    private final String mUrl;
    private final String[] mImagePath;
    private final String documentIdNumber;
    private final String documentType;
    private final String documentName;
    private String socketTimeOutException;

    public HttpResponseListener mHttpResponseListener;


    public UploadMultipleIdentifierDocumentAsyncTask(String API_COMMAND, String url, Context context, String documentType, String documentIdNumber, String documentName, String[] imagePath, HttpResponseListener httpResponseListener) {
        this.mContext = context;
        this.mUrl = url;
        this.mHttpResponseListener = httpResponseListener;
        this.API_COMMAND = API_COMMAND;
        this.documentType = documentType;
        this.mImagePath = imagePath;
        this.documentIdNumber = documentIdNumber;
        this.documentName = documentName;
        socketTimeOutException = null;
    }

    @Override
    protected GenericHttpResponse doInBackground(Void... params) {
        Logger.logW("Document Upload", "Started");

        GenericHttpResponse mGenericHttpResponse = new GenericHttpResponse();

        if (Utilities.isConnectionAvailable(mContext)) {
            mGenericHttpResponse = uploadDocument(mImagePath);
        }
        else{
            mGenericHttpResponse=new GenericHttpResponse(mContext.getString(R.string.no_internet_connection));
        }

        Logger.logW("Document Upload", "Finished");

        return mGenericHttpResponse;
    }

    @Override
    protected void onCancelled() {
        mHttpResponseListener.httpResponseReceiver(null);
    }

    @Override
    protected void onPostExecute(final GenericHttpResponse result) {

        if (socketTimeOutException != null) {
            mHttpResponseListener.httpResponseReceiver(new GenericHttpResponse(socketTimeOutException));
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

    private GenericHttpResponse uploadDocument(String selectedImagePath[]) {
        File[] files = new File[selectedImagePath.length];
        GenericHttpResponse genericHttpResponse = new GenericHttpResponse();
        final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        for (int i = 0; i < selectedImagePath.length; i++) {
            files[i] = new File(selectedImagePath[i]);
        }
        try {
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);

            for (int i = 0; i < files.length; i++) {
                builder.addFormDataPart("files", files[i].getName(), okhttp3.RequestBody.create(MEDIA_TYPE_PNG, files[i]));
            }

            builder.addFormDataPart(Constants.DOCUMENT_ID_NUMBER, documentIdNumber)
                    .addFormDataPart(Constants.DOCUMENT_TYPE, documentType);

            if (documentName != null) {
                builder.addFormDataPart(Constants.DOCUMENT_NAME, documentName);
            }
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
            Request request = requestBuilder.url(mUrl)
                    .post(requestBody)
                    .build();
            OkHttpClient okHttpClient;
            if (MyApplication.getMyApplicationInstance().getOkHttpClient() != null) {
                okHttpClient = MyApplication.getMyApplicationInstance().getOkHttpClient();
            } else {
                okHttpClient = new OkHttpClient.Builder()
                        .readTimeout(15, TimeUnit.SECONDS)
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .build();
            }
            Response response = null;
            try {
                response = okHttpClient.newCall(request).execute();
                String jsonString;
                jsonString = response.body().string();
                genericHttpResponse.setApiCommand(API_COMMAND);
                genericHttpResponse.setStatus(response.code());
                genericHttpResponse.setJsonString(jsonString);
                genericHttpResponse.setSilent(false);
            } catch (IOException e) {
                if (e instanceof SocketTimeoutException || e instanceof SocketException) {
                    socketTimeOutException = mContext.getString(R.string.connection_time_out);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();

        }
        return genericHttpResponse;
    }
}