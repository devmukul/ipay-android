package bd.com.ipay.ipayskeleton.Model.MMModule.Events;

import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class GetEventCategoriesRequestBuilder {

    private String generatedUri;

    public GetEventCategoriesRequestBuilder() {
        generateUri();
    }

    private void generateUri() {

        try {
            URI uri = new URIBuilder()
                    .setScheme(Constants.SCHEME)
                    .setHost(Constants.BASE_URL_GET_EM)
                    .setPort(Constants.BASE_URL_GET_EM_PORT)
                    .setPath(Constants.BASE_URL_GET_EM_PATH + "/" + Constants.URL_EVENT_CATEGORIES)
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
