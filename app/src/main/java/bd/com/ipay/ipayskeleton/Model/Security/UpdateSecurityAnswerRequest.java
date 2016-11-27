package bd.com.ipay.ipayskeleton.Model.Security;

import java.util.List;

public class UpdateSecurityAnswerRequest {
    private List<UpdateSecurityAnswerClass> securityAnswers;
    private String password;

    public UpdateSecurityAnswerRequest(List<UpdateSecurityAnswerClass> securityAnswers, String password) {
        this.securityAnswers = securityAnswers;
        this.password = password;
    }
}
