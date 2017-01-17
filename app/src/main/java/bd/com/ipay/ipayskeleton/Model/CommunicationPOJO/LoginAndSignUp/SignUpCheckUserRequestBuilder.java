package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.LoginAndSignUp;

import android.net.Uri;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class SignUpCheckUserRequestBuilder {

    private final String mobileNumber;
    private String generatedUri;

    public SignUpCheckUserRequestBuilder(String mobileNumber) {
        this.mobileNumber = mobileNumber;
        generateUri();
    }

    private void generateUri() {
        Uri uri = Uri.parse(Constants.BASE_URL_MM + Constants.URL_SIGN_UP_CHECK_USER + mobileNumber);
        setGeneratedUri(uri.toString());
    }

    public String getGeneratedUri() {
        return generatedUri;
    }

    private void setGeneratedUri(String generatedUri) {
        this.generatedUri = generatedUri;
    }
}

