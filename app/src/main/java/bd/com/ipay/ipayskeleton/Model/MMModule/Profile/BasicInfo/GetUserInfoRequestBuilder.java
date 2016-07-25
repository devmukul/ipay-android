package bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo;

import android.net.Uri;

import java.net.URLEncoder;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class GetUserInfoRequestBuilder {

    private final String PARAM_LOGIN_ID = "loginId";

    private String loginId;
    private String generatedUri;

    public GetUserInfoRequestBuilder(String loginId) {
        this.loginId = URLEncoder.encode(loginId);
        generateUri(loginId);
    }

    private void generateUri(String loginId) {
        Uri uri = Uri.parse(Constants.BASE_URL_MM + Constants.URL_GET_USER_INFO)
                .buildUpon()
                .appendQueryParameter(PARAM_LOGIN_ID, loginId)
                .build();

         setGeneratedUri(uri.toString());
    }

    public String getGeneratedUri() {
        return generatedUri;
    }

    public void setGeneratedUri(String generatedUri) {
        this.generatedUri = generatedUri;
    }
}
