package bd.com.ipay.ipayskeleton.SecuritySettingsFragments;

public class DeleteTrustedPersonRequest {
    private final long personId;
    private final String password;

    public DeleteTrustedPersonRequest(long personId, String passsword) {
        this.personId = personId;
        this.password = passsword;
    }
}
