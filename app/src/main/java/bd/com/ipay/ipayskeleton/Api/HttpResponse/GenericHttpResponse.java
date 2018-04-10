package bd.com.ipay.ipayskeleton.Api.HttpResponse;

import android.content.Context;

import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Logger;
import okhttp3.Headers;

public class GenericHttpResponse {
    private int status;
    private String apiCommand;
    private String jsonString;
    private boolean isUpdateNeeded;
    private Context context;
    private Headers headers;

    public String getErrorMessage() {
        return errorMessage;
    }

    public GenericHttpResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    private String errorMessage;

    public GenericHttpResponse() {
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public boolean isUpdateNeeded() {
        return isUpdateNeeded;
    }

    public void setUpdateNeeded(boolean updateNeeded) {
        isUpdateNeeded = updateNeeded;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getApiCommand() {
        return apiCommand;
    }

    public void setApiCommand(String apiCommand) {
        this.apiCommand = apiCommand;
    }

    public String getJsonString() {
        return jsonString;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }

    public Headers getHeaders() {
        return headers;
    }

    public void setHeaders(Headers headers) {
        this.headers = headers;
    }

    private String getHeaderValue(String headerName) {
        for (int i=0;i<headers.size();i++) {
            Logger.logW(headers.name(i), headers.value(i));
            if (headers.value(i).equals(headerName))
                return headers.value(i);
        }

        return null;
    }

    public String getResourceToken() {
        return getHeaderValue(Constants.RESOURCE_TOKEN);
    }

    @Override
    public String toString() {
        return "{" +
                "status=" + status +
                ", apiCommand='" + apiCommand + '\'' +
                ", jsonString='" + jsonString + '\'' +
                '}';
    }
}
