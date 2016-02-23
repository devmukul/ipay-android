package bd.com.ipay.ipayskeleton.Model.MMModule.Profile;

import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
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

        try {
            URI uri = new URIBuilder()
                    .setScheme("http")
                    .setHost(Constants.BASE_URL_GET_MM)
                    .setPort(Constants.BASE_URL_GET_MM_PORT)
                    .setPath(Constants.BASE_URL_GET_MM_PATH + "/" + Constants.URL_GET_USER_INFO)
                    .addParameter(PARAM_LOGIN_ID, loginId)
                    .build();
            setGeneratedUri(uri.toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public String getGeneratedUri() {
        return generatedUri;
    }

    public void setGeneratedUri(String generatedUri) {
        this.generatedUri = generatedUri;
    }
}
