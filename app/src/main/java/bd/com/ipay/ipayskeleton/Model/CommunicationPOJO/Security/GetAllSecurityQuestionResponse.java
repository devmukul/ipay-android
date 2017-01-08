package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Security;

import java.util.List;

public class GetAllSecurityQuestionResponse {
    private List<String> list;
    private String message;
    private int required;

    public GetAllSecurityQuestionResponse() {
    }

    public List<String> getList() {
        return list;
    }

    public String getMessage() {
        return message;
    }

    public int getRequired() {
        return required;
    }
}
