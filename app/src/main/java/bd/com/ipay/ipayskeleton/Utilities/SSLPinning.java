package bd.com.ipay.ipayskeleton.Utilities;

import android.content.Context;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import bd.com.ipay.ipayskeleton.R;

public class SSLPinning {

    private static String responseString = null;

    static final String[] PINS =
            new String[]{
                    "sha1/AZWfAo2G1fHZcKNXPHeRmK4ZeR0=",
                    "sha1/RcLRLpszlmNF23hE+iOoPN+iH0E="};

    public static String validatePinning() {
        Context context = MyApplication.getMyApplicationInstance().getApplicationContext();
        if (Constants.SERVER_TYPE == Constants.SERVER_TYPE_LIVE) {

            okhttp3.CertificatePinner certificatePinner = new okhttp3.CertificatePinner.Builder()
                    .add(Constants.HOST_NAME, PINS[0])
                    .add(Constants.HOST_NAME, PINS[1])
                    .build();

            okhttp3.OkHttpClient okHttpClient = new okhttp3.OkHttpClient.Builder().
                    certificatePinner(certificatePinner).build();

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(Constants.BASE_URL_WEB)
                    .build();
            try {
                okhttp3.Response response = okHttpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    responseString = context.getString(R.string.OK);
                    return responseString;
                } else {
                    responseString = context.getString(R.string.service_not_available);
                    return responseString;
                }
            } catch (IOException e) {
                if (e instanceof SocketTimeoutException) {
                    responseString = context.getString(R.string.connection_time_out);
                    return responseString;
                } else if (e instanceof SocketException) {
                    responseString = context.getString(R.string.network_unreachable);
                    return responseString;
                } else {
                    responseString = context.getString(R.string.service_not_available);
                    return responseString;
                }
            }
        } else return context.getString(R.string.OK);
    }
}

