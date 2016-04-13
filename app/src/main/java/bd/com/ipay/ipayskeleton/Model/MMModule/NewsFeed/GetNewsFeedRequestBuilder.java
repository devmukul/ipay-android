package bd.com.ipay.ipayskeleton.Model.MMModule.NewsFeed;

import android.net.Uri;

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
        Uri uri = Uri.parse(Constants.BASE_URL + "/" + Constants.URL_GET_NEWS_FEED)
                    .buildUpon()
                    .appendQueryParameter(PARAM_PAGE, page + "")
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
