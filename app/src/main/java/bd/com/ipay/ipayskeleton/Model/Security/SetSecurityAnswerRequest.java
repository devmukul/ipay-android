package bd.com.ipay.ipayskeleton.Model.Security;

import java.util.List;

public class SetSecurityAnswerRequest {
    private List<AddSecurityAnswerClass> securityAnswers;
    private String password;

    public SetSecurityAnswerRequest(List<AddSecurityAnswerClass> securityAnswers, String password) {
        this.securityAnswers = securityAnswers;
        this.password = password;
    }
}
