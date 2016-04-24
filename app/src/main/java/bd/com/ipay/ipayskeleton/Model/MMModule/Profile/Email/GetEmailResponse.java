package bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Email;

import java.util.List;

public class GetEmailResponse {
    private String message;
    private List<Email> emailAdressList;

    public String getMessage() {
        return message;
    }

    public List<Email> getEmailAdressList() {
        return emailAdressList;
    }
}
