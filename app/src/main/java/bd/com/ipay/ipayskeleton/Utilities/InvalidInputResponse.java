package bd.com.ipay.ipayskeleton.Utilities;

public class InvalidInputResponse {
    private String[] errorFieldNames;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public InvalidInputResponse(String[] errorFieldNames, String message) {
        this.errorFieldNames = errorFieldNames;
        this.message = message;
    }

    public String[] getErrorFieldNames() {
        return errorFieldNames;
    }

    public void setErrorFieldNames(String[] errorFieldNames) {
        this.errorFieldNames = errorFieldNames;
    }
}
