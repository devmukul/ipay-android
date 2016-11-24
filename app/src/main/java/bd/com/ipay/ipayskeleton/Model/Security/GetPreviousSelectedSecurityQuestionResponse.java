package bd.com.ipay.ipayskeleton.Model.Security;

import java.util.List;

public class GetPreviousSelectedSecurityQuestionResponse {
    private List<SelectedSecurityQuestionClass> securityAnswers;
    private String message;

    public GetPreviousSelectedSecurityQuestionResponse() {
    }

    public List<SelectedSecurityQuestionClass> getList() {
        return securityAnswers;
    }

    public String getMessage() {
        return message;
    }

}
