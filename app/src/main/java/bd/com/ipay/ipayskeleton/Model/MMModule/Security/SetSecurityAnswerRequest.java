package bd.com.ipay.ipayskeleton.Model.MMModule.Security;

import java.util.List;

public class SetSecurityAnswerRequest {
    private List<AddSecurityQuestionAnswerClass> securityAnswers;
    private String password;

    public SetSecurityAnswerRequest(List<AddSecurityQuestionAnswerClass> securityAnswers, String password) {
        this.securityAnswers = securityAnswers;
        this.password = password;
    }
}
