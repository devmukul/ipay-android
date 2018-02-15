package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRoles;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class GetBusinessDetailsRequestBuilder {

    private long mBusinessAccountID;
    private String generatedUri;

    public GetBusinessDetailsRequestBuilder(long mBusinessAccountID) {
        this.mBusinessAccountID = mBusinessAccountID;
        generateUri();
    }

    public long getmBusinessAccountID() {
        return mBusinessAccountID;
    }

    public void setmBusinessAccountID(long mBusinessAccountID) {
        this.mBusinessAccountID = mBusinessAccountID;
    }

    public String getGeneratedUri() {
        return generatedUri;
    }

    public void setGeneratedUri(String generatedUri) {
        this.generatedUri = generatedUri;
    }

    private void generateUri() {
        generatedUri = Constants.BASE_URL_MM + Constants.URL_SWITCH_ACCOUNT + mBusinessAccountID;

    }
}
