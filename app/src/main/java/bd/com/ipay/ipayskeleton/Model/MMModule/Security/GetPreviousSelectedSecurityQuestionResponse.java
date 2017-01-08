package bd.com.ipay.ipayskeleton.Model.MMModule.Security;

import java.util.List;

public class GetPreviousSelectedSecurityQuestionResponse {
    private List<PreviousSecurityQuestionClass> securityAnswers;
    private String message;

    public GetPreviousSelectedSecurityQuestionResponse() {
    }

    public List<PreviousSecurityQuestionClass> getList() {
        return securityAnswers;
    }

    public String getMessage() {
        return message;
    }

}
