package bd.com.ipay.ipayskeleton.Model.BusinessContact;

import android.net.Uri;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class GetAllBusinessContactRequestBuilder {

    private String generatedUri;
    private String PARAM_LAST_BUSINESS_ID = "lastBusinessId";

    public GetAllBusinessContactRequestBuilder(int lastBusinessId) {
        generateUri(lastBusinessId);
    }

    private void generateUri(int lastBusinessId) {
        Uri uri = Uri.parse(Constants.BASE_URL_MM + Constants.URL_GET_BUSINESS_LIST_ALL)
                .buildUpon()
                .appendQueryParameter(PARAM_LAST_BUSINESS_ID, lastBusinessId + "")
                .build();

        setGeneratedUri(uri.toString());
    }

    public String getGeneratedUri() {
        return generatedUri;
    }

    private void setGeneratedUri(String generatedUri) {
        this.generatedUri = generatedUri;
    }

}
