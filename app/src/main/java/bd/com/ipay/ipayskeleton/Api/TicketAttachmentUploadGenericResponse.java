package bd.com.ipay.ipayskeleton.Api;

import android.content.Context;
import android.util.Log;

import org.apache.http.Header;

import java.util.List;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class TicketAttachmentUploadGenericResponse {
    private int status;
    private String apiCommand;
    private String response;
    private boolean isUpdateNeeded;
    private Context context;
    private List<Header> headers;

    public TicketAttachmentUploadGenericResponse () {
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

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public void setHeaders(List<Header> headers) {
        this.headers = headers;
    }

    private String getHeaderValue(String headerName) {
        for (Header header : headers) {
            if (Constants.DEBUG)
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
                ", jsonString='" + response + '\'' +
                '}';
    }
}
