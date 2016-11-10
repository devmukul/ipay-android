package bd.com.ipay.ipayskeleton.Model.Security;

import java.util.List;

public class SetSecurityAnswerRequest {
    private List<SecurityAnswerClass> securityAnswers;
    private String password;

    public SetSecurityAnswerRequest(List<SecurityAnswerClass> securityAnswers, String password) {
        this.securityAnswers = securityAnswers;
        this.password = password;
    }
}
