package bd.com.ipay.ipayskeleton.Model.MMModule.Education;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class GetSessionRequestBuilder {

    private Long instituteID;

    public GetSessionRequestBuilder(Long instituteID) {
        this.instituteID = instituteID;
    }

    public String getGeneratedUrl() {
        return Constants.BASE_URL_EDU + Constants.URL_GET_ALL_SESSIONS_LIST + "/" + instituteID;
    }
}

