package bd.com.ipay.ipayskeleton.Model.MMModule.Resource;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public abstract class ResourceRequestBuilder {
    private static final String PARAM_RESOURCE = "resource";

    private String generatedUri;

    public ResourceRequestBuilder() {
        generateUri();
    }

    private void generateUri() {
        try {
            URI uri = new URIBuilder()
                    .setScheme("http")
                    .setHost(Constants.BASE_URL_GET_MM)
                    .setPort(Constants.BASE_URL_GET_MM_PORT)
                    .setPath(Constants.BASE_URL_GET_MM_PATH + "/" + Constants.URL_GET_USER_INFO)
                    .addParameter(PARAM_RESOURCE, getResourceType())
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
