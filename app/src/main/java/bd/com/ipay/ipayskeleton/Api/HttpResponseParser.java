package bd.com.ipay.ipayskeleton.Api;

import android.os.CountDownTimer;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.TokenManager;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class HttpResponseParser {
    private HttpResponseObject mHttpResponseObject = null;
    private HttpResponse mHttpResponse;
    private String API_COMMAND = "";

    public HttpResponseParser() {
    }

    public HttpResponseObject parseHttpResponse() {

        setTokenTimerAndRefreshToken(mHttpResponse.getAllHeaders());
        int status = mHttpResponse.getStatusLine().getStatusCode();
        String responseJsonString = getResponseBody(mHttpResponse.getEntity());

        mHttpResponseObject = new HttpResponseObject();
        mHttpResponseObject.setStatus(status);
        mHttpResponseObject.setApiCommand(API_COMMAND);
        mHttpResponseObject.setJsonString(responseJsonString);
        mHttpResponseObject.setHeaders(Arrays.asList(mHttpResponse.getAllHeaders()));

        return mHttpResponseObject;
    }

    private void setTokenTimerAndRefreshToken(Header[] headers) {
        if (headers.length > 0) {
            for (Header header : headers) {
                if (header.getName().equals(Constants.TOKEN)) {
                    TokenManager.setToken(header.getValue());
                    TokenManager.setiPayTokenTimeInMs(Utilities.getTimeFromBase64Token(TokenManager.getToken()));

                    CountDownTimer tokenTimer = TokenManager.getTokenTimer();

                    if (tokenTimer != null) {
                        if (Constants.DEBUG)
                            Log.w("Token_Timer", "Starting... " + TokenManager.getiPayTokenTimeInMs());

                        tokenTimer.cancel();
                        tokenTimer.start();
                    }
                } else if (header.getName().equals(Constants.REFRESH_TOKEN)) {
                    TokenManager.setRefreshToken(header.getValue());
                    if (Constants.DEBUG)
                        Log.d(Constants.REFRESH_TOKEN, TokenManager.getRefreshToken());
                }
            }
        }
    }

    private String getResponseBody(HttpEntity entity) {
        InputStream inputStream = null;
        StringBuilder sb = new StringBuilder();

        try {
            inputStream = entity.getContent();
            // json is UTF-8 by default
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (Exception squish) {
                squish.printStackTrace();
            }
        }

        return sb.toString();
    }

    public void setAPI_COMMAND(String API_COMMAND) {
        this.API_COMMAND = API_COMMAND;
    }

    public void setHttpResponse(HttpResponse mHttpResponse) {
        this.mHttpResponse = mHttpResponse;
    }
}
