package bd.com.ipay.ipayskeleton.Api;

import android.util.Log;

import org.apache.http.Header;

import java.util.List;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class HttpResponseObject {
    private int status;
    private String apiCommand;
    private String jsonString;
    private List<Header> headers;

    public HttpResponseObject() {
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

    public List<Header> getHeaders() {
        return headers;
    }

    public void setHeaders(List<Header> headers) {
        this.headers = headers;
    }

    private String getHeaderValue(String headerName) {
        for (Header header : headers) {
            Log.w(header.getName(), header.getValue());
            if (header.getName().equals(headerName))
                return header.getValue();
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
