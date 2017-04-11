package bd.com.ipay.ipayskeleton.Api.HttpResponse;

import android.content.Context;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.MyApplication;
import bd.com.ipay.ipayskeleton.Utilities.ToastandLogger.Logger;
import bd.com.ipay.ipayskeleton.Utilities.TokenManager;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class HttpResponseParser {
    private GenericHttpResponse mGenericHttpResponse = null;
    private HttpResponse mHttpResponse;
    private String API_COMMAND = "";
    private Context mContext;

    public HttpResponseParser() {
    }

    public GenericHttpResponse parseHttpResponse() {

        setTokenTimerAndRefreshToken(mHttpResponse.getAllHeaders());
        int status = mHttpResponse.getStatusLine().getStatusCode();
        String responseJsonString = getResponseBody(mHttpResponse.getEntity());

        mGenericHttpResponse = new GenericHttpResponse();
        mGenericHttpResponse.setStatus(status);
        mGenericHttpResponse.setApiCommand(API_COMMAND);
        mGenericHttpResponse.setJsonString(responseJsonString);
        mGenericHttpResponse.setHeaders(Arrays.asList(mHttpResponse.getAllHeaders()));

        return mGenericHttpResponse;
    }

    private void setTokenTimerAndRefreshToken(Header[] headers) {
        if (headers.length > 0) {
            for (Header header : headers) {
                if (header.getName().equals(Constants.TOKEN)) {
                    TokenManager.setToken(header.getValue());
                    TokenManager.setiPayTokenTimeInMs(Utilities.getTimeFromBase64Token(TokenManager.getToken()));

                    // Start the timer for token.
                    MyApplication myApplicationInstance = MyApplication.getMyApplicationInstance();
                    myApplicationInstance.startTokenTimer();

                } else if (header.getName().equals(Constants.REFRESH_TOKEN)) {
                    TokenManager.setRefreshToken(header.getValue());
                    Logger.logDebug(Constants.REFRESH_TOKEN, TokenManager.getRefreshToken());
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

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }
}
