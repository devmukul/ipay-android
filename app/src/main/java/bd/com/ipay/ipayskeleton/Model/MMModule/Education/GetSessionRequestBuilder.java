package bd.com.ipay.ipayskeleton.Model.MMModule.Education;

import android.net.Uri;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class GetSessionRequestBuilder {

    private final static String PARAM_INSTITUTE_ID = "instituteID";

    private Long instituteID;

    public GetSessionRequestBuilder(Long instituteID) {
        this.instituteID = instituteID;
    }

    public String getGeneratedUri() {
        Uri.Builder uri = Uri.parse(Constants.BASE_URL_EDU + Constants.URL_GET_ALL_SESSIONS_LIST)
                .buildUpon();

        if (instituteID != null)
            uri.appendQueryParameter(PARAM_INSTITUTE_ID, Long.toString(instituteID));

        return uri.build().toString();
    }

}

