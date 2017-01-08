package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.NewsFeed;

import android.net.Uri;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

class GetNewsFeedRequestBuilder {

    private final String PARAM_PAGE = "page";

    private final int page;
    private String generatedUri;

    public GetNewsFeedRequestBuilder(int page) {
        this.page = page;
        generateUri(page);
    }

    private void generateUri(int page) {
        Uri uri = Uri.parse(Constants.BASE_URL_MM + Constants.URL_GET_NEWS_FEED)
                    .buildUpon()
                    .appendQueryParameter(PARAM_PAGE, page + "")
                    .build();
        setGeneratedUri(uri.toString());
    }

    public String getGeneratedUri() {
        return generatedUri;
    }

    private void setGeneratedUri(String generatedUri) {
        this.generatedUri = generatedUri;
    }
}
