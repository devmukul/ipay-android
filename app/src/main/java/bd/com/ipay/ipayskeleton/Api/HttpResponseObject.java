package bd.com.ipay.ipayskeleton.Api;

public class HttpResponseObject {
    private int status;
    private String apiCommand;
    private String jsonString;

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
}
