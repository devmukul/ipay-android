package bd.com.ipay.ipayskeleton.Utilities;

import com.squareup.okhttp.CertificatePinner;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class SSLPinning {

    static final String[] PINS =
            new String[]{
                    "sha1/AZWfAo2G1fHZcKNXPHeRmK4ZeR0=",
                    "sha1/RcLRLpszlmNF23hE+iOoPN+iH0E="};

    public static boolean validatePinning() {
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
                    return true;
                } else return false;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        } else return true;
    }
}

