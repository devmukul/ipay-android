package bd.com.ipay.ipayskeleton.Api.HttpResponse;

import android.content.Context;

import org.apache.http.HttpEntity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.MyApplication;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Logger;
import bd.com.ipay.ipayskeleton.Utilities.TokenManager;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;
import okhttp3.Headers;
import okhttp3.Response;

public class HttpResponseParser {
    private GenericHttpResponse mGenericHttpResponse = null;
    private Response mHttpResponse;
    private String API_COMMAND = "";
    private Context mContext;

    public HttpResponseParser() {

    }

    public GenericHttpResponse parseHttpResponse() {
        setTokenTimerAndRefreshToken(mHttpResponse.headers());
        mGenericHttpResponse = new GenericHttpResponse();
        String jsonString="";
        try {
            jsonString = mHttpResponse.body().string();
        }catch(Exception e){

        }
        int status=mHttpResponse.code();
        mGenericHttpResponse.setJsonString(jsonString);
        mGenericHttpResponse.setApiCommand(API_COMMAND);
        mGenericHttpResponse.setHeaders(mHttpResponse.headers());
        mGenericHttpResponse.setStatus(status);
        return mGenericHttpResponse;
    }


    private void setTokenTimerAndRefreshToken(Headers headers) {
        if (headers.size() > 0) {
            for (int i = 0; i < headers.size(); i++) {
                if (headers.name(i).equals(Constants.TOKEN)) {
                    String k=headers.name(i);
                    String l=headers.value(i);
                    String p=k;
                    String o=l;
                    TokenManager.setToken(l);
                    TokenManager.setiPayTokenTimeInMs(Utilities.getTimeFromBase64Token(TokenManager.getToken()));

                    // Start the timer for token.
                    MyApplication myApplicationInstance = MyApplication.getMyApplicationInstance();

                    if (!SharedPrefManager.isRememberMeActive()) {
                        myApplicationInstance.startTokenTimer();
                    }

                } else if (headers. name(i).equals(Constants.REFRESH_TOKEN)) {
                    TokenManager.setLastRefreshTokenFetchTime(Utilities.currentTime());
                    TokenManager.setRefreshToken(headers.value(i));
                    Logger.logD(Constants.REFRESH_TOKEN, TokenManager.getRefreshToken());
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

    public void setHttpResponse(Response mHttpResponse) {
        this.mHttpResponse = mHttpResponse;
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }
}
