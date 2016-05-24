package bd.com.ipay.ipayskeleton.Model.MMModule.Resource;

import android.net.Uri;

import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public abstract class ResourceRequestBuilder {
    private static final String PARAM_TYPE = "type";
    private static final String PARAM_FILTER = "filter";

    private String generatedUri;
    protected long filter;

    public ResourceRequestBuilder() {
        filter = -1;
        generateUri();
    }

    public ResourceRequestBuilder(long filter) {
        this.filter = filter;
        generateUri();
    }

    private void generateUri() {
        Uri uri;
        if (filter == -1) uri = Uri.parse(Constants.BASE_URL_MM + Constants.URL_RESOURCE)
                .buildUpon()
                .appendQueryParameter(PARAM_TYPE, getResourceType())
                .build();
        else uri = Uri.parse(Constants.BASE_URL_MM + Constants.URL_RESOURCE)
                .buildUpon()
                .appendQueryParameter(PARAM_TYPE, getResourceType())
                .appendQueryParameter(PARAM_FILTER, filter + "")
                .build();

        setGeneratedUri(uri.toString());
    }

    public abstract String getResourceType();

    public String getGeneratedUri() {
        return generatedUri;
    }

    public void setGeneratedUri(String generatedUri) {
        this.generatedUri = generatedUri;
    }
}
