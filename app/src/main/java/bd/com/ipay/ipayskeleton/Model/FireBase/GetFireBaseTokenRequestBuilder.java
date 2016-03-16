package bd.com.ipay.ipayskeleton.Model.FireBase;

import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class GetFireBaseTokenRequestBuilder {

    private String generatedUri;

    public GetFireBaseTokenRequestBuilder() {
        generateUri();
    }

    private void generateUri() {

        try {
            URI uri = new URIBuilder()
                    .setScheme(Constants.SCHEME)
                    .setHost(Constants.BASE_URL_GET_MM)
                    .setPort(Constants.BASE_URL_GET_MM_PORT)
                    .setPath(Constants.BASE_URL_GET_MM_PATH + "/" + Constants.URL_GET_FIREBASE_TOKEN)
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
