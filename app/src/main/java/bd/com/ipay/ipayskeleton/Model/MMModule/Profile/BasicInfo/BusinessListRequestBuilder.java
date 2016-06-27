package bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo;

import android.net.Uri;

import java.net.URLEncoder;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class BusinessListRequestBuilder {

    private final String PARAM_STATUS = "status";

    public String getAllBusinessListUri() {
        return generateUri(null).toString();
    }

    public String getAcceptedBusinessListUri() {
        return generateUri(Constants.BUSINESS_INVITATION_ACCEPTED).toString();
    }

    public String getPendingBusinessListUri() {
        return generateUri(Constants.BUSINESS_STATUS_PENDING).toString();
    }

    private Uri generateUri(String status) {
        Uri.Builder uri = Uri.parse(Constants.BASE_URL_MM + Constants.URL_GET_BUSINESS_LIST)
                .buildUpon();

        if (status != null)
            uri.appendQueryParameter(PARAM_STATUS, status);

        return uri.build();
    }

}
