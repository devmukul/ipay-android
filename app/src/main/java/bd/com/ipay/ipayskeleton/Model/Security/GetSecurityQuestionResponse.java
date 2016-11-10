package bd.com.ipay.ipayskeleton.Model.Security;

import java.util.List;

public class GetSecurityQuestionResponse {
    private List<SecurityQuestionClass> list;
    private String message;
    private int required;

    public GetSecurityQuestionResponse() {
    }

    public List<SecurityQuestionClass> getList() {
        return list;
    }

    public String getMessage() {
        return message;
    }

    public int getRequired() {
        return required;
    }
}
