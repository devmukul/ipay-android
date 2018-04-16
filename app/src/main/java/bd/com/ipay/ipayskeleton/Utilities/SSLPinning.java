package bd.com.ipay.ipayskeleton.Utilities;

import android.content.Context;

import com.squareup.okhttp.CertificatePinner;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

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

            CertificatePinner certificatePinner = new CertificatePinner.Builder()
                    .add(Constants.HOST_NAME, PINS[0])
                    .add(Constants.HOST_NAME, PINS[1])
                    .build();

            OkHttpClient client = new OkHttpClient();
            client.setCertificatePinner(certificatePinner);

            Request request = new Request.Builder()
                    .url(Constants.BASE_URL_WEB)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    return responseString = context.getString(R.string.OK);
                } else return responseString = context.getString(R.string.service_not_available);
            } catch (IOException e) {
                if (e instanceof SocketTimeoutException) {
                    return responseString = context.getString(R.string.connection_time_out);
                } else if (e instanceof SocketException) {
                    return responseString = context.getString(R.string.network_unreachable);
                } else {
                    return responseString = context.getString(R.string.service_not_available);
                }
            }
        } else return context.getString(R.string.OK);
    }
}

