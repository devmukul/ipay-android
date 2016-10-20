package bd.com.ipay.ipayskeleton.Model.MMModule.Business.Employee;

import android.net.Uri;

import java.net.URLEncoder;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class GetAllBusinessContactRequestBuilder {

    private final String PARAM_BUSINESS_NAME = "businessName";

    private final String businessName;
    private String generatedUri;

    public GetAllBusinessContactRequestBuilder(String businessName) {
        this.businessName = businessName;
        generateUri(businessName);
    }

    private void generateUri(String businessName) {
        Uri uri = Uri.parse(Constants.BASE_URL_MM + Constants.URL_GET_BUSINESS_LIST_ALL)
                .buildUpon()
                .appendQueryParameter(PARAM_BUSINESS_NAME, businessName)
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
