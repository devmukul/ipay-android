package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRoles;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class LeaveOrRemoveBusinessAccountRequestBuilder {

    private long accountID;
    private String generatedUri;



    public LeaveOrRemoveBusinessAccountRequestBuilder(long accountID) {
        this.accountID = accountID;
        generateUri();
    }

    public long getAccountID() {
        return accountID;
    }

    public void setAccountID(long accountID) {
        this.accountID = accountID;
    }

    public String getGeneratedUri() {
        return generatedUri;
    }

    public void setGeneratedUri(String generatedUri) {
        this.generatedUri = generatedUri;
    }

    private void generateUri() {
        generatedUri = Constants.BASE_URL_MM + Constants.URL_REMOVE_AN_EMPLOYEE_FIRST_PART + accountID;

    }
}
