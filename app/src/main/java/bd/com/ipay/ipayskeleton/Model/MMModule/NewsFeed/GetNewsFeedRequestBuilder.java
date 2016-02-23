package bd.com.ipay.ipayskeleton.Model.MMModule.NewsFeed;

import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class GetNewsFeedRequestBuilder {

    private final String PARAM_PAGE = "page";

    private int page;
    private String generatedUri;

    public GetNewsFeedRequestBuilder(int page) {
        this.page = page;
        generateUri(page);
    }

    private void generateUri(int page) {

        try {
            URI uri = new URIBuilder()
                    .setScheme("http")
                    .setHost(Constants.BASE_URL_GET_MM)
                    .setPort(Constants.BASE_URL_GET_MM_PORT)
                    .setPath(Constants.BASE_URL_GET_MM_PATH + "/" + Constants.URL_GET_NEWS_FEED)
                    .addParameter(PARAM_PAGE, page + "")
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
