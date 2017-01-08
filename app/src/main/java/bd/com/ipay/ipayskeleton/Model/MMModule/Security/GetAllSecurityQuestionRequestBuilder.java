package bd.com.ipay.ipayskeleton.Model.MMModule.Security;

import android.net.Uri;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class GetAllSecurityQuestionRequestBuilder {
    private String generatedUri;

    public GetAllSecurityQuestionRequestBuilder() {
        generateUri();
    }

    private void generateUri() {
        Uri uri = Uri.parse(Constants.BASE_URL_MM + Constants.URL_GET_SECURITY_ALL_QUESTIONS)
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
