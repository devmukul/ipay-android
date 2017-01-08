package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Education;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class GetEnabledPayablesRequestBuilder {

    private int instituteID;

    public GetEnabledPayablesRequestBuilder(int instituteID) {
        this.instituteID = instituteID;
    }

    public String getGeneratedUrl() {
        return Constants.BASE_URL_EDU + Constants.URL_GET_ENABLED_PAYABLES_LIST + "/" + instituteID;
    }
}

