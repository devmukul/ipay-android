package bd.com.ipay.ipayskeleton.Model.MMModule.Resource;

import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public abstract class ResourceRequestBuilder {
    private static final String PARAM_TYPE = "type";

    private String generatedUri;

    public ResourceRequestBuilder() {
        generateUri();
    }

    private void generateUri() {
        try {
            URI uri = new URIBuilder()
                    .setScheme(Constants.SCHEME)
                    .setHost(Constants.BASE_URL_GET_MM)
                    .setPort(Constants.BASE_URL_GET_MM_PORT)
                    .setPath(Constants.BASE_URL_GET_MM_PATH + "/" + Constants.URL_RESOURCE)
                    .addParameter(PARAM_TYPE, getResourceType())
                    .build();
            setGeneratedUri(uri.toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public abstract String getResourceType();

    public String getGeneratedUri() {
        return generatedUri;
    }

    public void setGeneratedUri(String generatedUri) {
        this.generatedUri = generatedUri;
    }
}
