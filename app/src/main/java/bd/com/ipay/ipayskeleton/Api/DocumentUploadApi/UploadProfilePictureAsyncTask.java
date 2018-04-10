package bd.com.ipay.ipayskeleton.Api.DocumentUploadApi;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.OkHttpResponse;
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

public class UploadProfilePictureAsyncTask extends AsyncTask<Void, Void, GenericHttpResponse> {

    private final Context mContext;
    private final String imagePath;
    private final String API_COMMAND;
    private final String API_URL;
    public HttpResponseListener mHttpResponseListener;
    private String socketTimeOutException;

    public UploadProfilePictureAsyncTask(String API_COMMAND, String URL, String imagePath, Context mContext) {
        this.mContext = mContext;
        this.imagePath = imagePath;
        this.API_COMMAND = API_COMMAND;
        this.API_URL = URL;
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
        if (socketTimeOutException != null) {
            Toast.makeText(mContext, socketTimeOutException, Toast.LENGTH_LONG).show();
        }

        if (result != null) {
            Logger.logW("Image Upload", result.toString());

            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_UNAUTHORIZED) {
                MyApplication myApplicationInstance = MyApplication.getMyApplicationInstance();
                myApplicationInstance.launchLoginPage(null);
            } else
                mHttpResponseListener.httpResponseReceiver(result);

        } else {
            Logger.logW("Image Upload", "NULL");

            mHttpResponseListener.httpResponseReceiver(null);
        }

    }

    private GenericHttpResponse uploadImage(String selectedImagePath) {
        File file = new File(selectedImagePath);
        GenericHttpResponse genericHttpResponse = new GenericHttpResponse();
        final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        try {
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);
            builder.addFormDataPart("file", file.getName(), okhttp3.RequestBody.create(MEDIA_TYPE_PNG, file));

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
            Request request = requestBuilder.url(Constants.BASE_URL_MM + API_URL)
                    .post(requestBody)
                    .build();
            OkHttpClient okHttpClient;
            if (MyApplication.getMyApplicationInstance().getOkHttpClient() != null) {
                okHttpClient = MyApplication.getMyApplicationInstance().getOkHttpClient();

            } else {
                okHttpClient = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();
            }
            try {
                Response response = okHttpClient.newCall(request).execute();
                OkHttpResponse okHttpResponse = new OkHttpResponse();
                okHttpResponse.setResponse(response);
                String jsonString;
                jsonString = response.body().string();
                genericHttpResponse.setApiCommand(API_COMMAND);
                genericHttpResponse.setStatus(response.code());
                genericHttpResponse.setJsonString(jsonString);
            } catch (IOException e) {
                if (e instanceof SocketTimeoutException) {
                    socketTimeOutException = mContext.getString(R.string.connection_time_out);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
        return genericHttpResponse;
    }
}