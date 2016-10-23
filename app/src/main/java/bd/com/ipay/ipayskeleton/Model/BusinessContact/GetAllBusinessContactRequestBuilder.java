package bd.com.ipay.ipayskeleton.Model.BusinessContact;

import android.net.Uri;

import java.net.URLEncoder;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class GetAllBusinessContactRequestBuilder {

    private String generatedUri;

    public GetAllBusinessContactRequestBuilder() {
        generateUri();
    }

    private void generateUri() {
        Uri uri = Uri.parse(Constants.BASE_URL_MM + Constants.URL_GET_BUSINESS_LIST_ALL)
                .buildUpon()
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
