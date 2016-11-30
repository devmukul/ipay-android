package bd.com.ipay.ipayskeleton.Model.Security;

import java.util.List;

public class UpdateSecurityAnswerRequest {
    private List<UpdateSecurityQuestionAnswerClass> securityAnswers;
    private String password;

    public UpdateSecurityAnswerRequest(List<UpdateSecurityQuestionAnswerClass> securityAnswers, String password) {
        this.securityAnswers = securityAnswers;
        this.password = password;
    }
}
