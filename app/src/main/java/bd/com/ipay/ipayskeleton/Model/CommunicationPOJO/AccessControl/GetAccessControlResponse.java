package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.AccessControl;

public class GetAccessControlResponse {
    private String message;
    private int[] accessControlList;

    public GetAccessControlResponse() {
    }

    public String getMessage() {
        return message;
    }

    public int[] getAccessControlList() {
        return accessControlList;
    }
}
