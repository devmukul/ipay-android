package bd.com.ipay.ipayskeleton.Model.Security;

import android.net.Uri;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class GetPreviousSelectedSecurityQuestionRequestBuilder {
    private String generatedUri;

    public GetPreviousSelectedSecurityQuestionRequestBuilder() {
        generateUri();
    }

    private void generateUri() {
        Uri uri = Uri.parse(Constants.BASE_URL_MM + Constants.URL_GET_SECURITY_QUESTIONS)
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
