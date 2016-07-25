package bd.com.ipay.ipayskeleton.Model.MMModule.Events;

import android.net.Uri;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

class GetEventListPersonalUserRequestBuilder {

    private final String PARAM_CATEGORY_ID = "categoryId";
    private final String PARAM_START_TIME = "startTime";
    private final String PARAM_END_TIME = "endTime";

    private final long categoryId;
    private final long startTime;
    private final long endTime;

    private String generatedUri;

    public GetEventListPersonalUserRequestBuilder(long categoryId, long startTime, long endTime) {
        this.categoryId = categoryId;
        this.startTime = startTime;
        this.endTime = endTime;

        generateUri(categoryId, startTime, endTime);
    }

    private void generateUri(long categoryId, long startTime, long endTime) {
        Uri uri = Uri.parse(Constants.BASE_URL_SM + Constants.URL_EVENT_LIST)
                .buildUpon()
                .appendQueryParameter(PARAM_CATEGORY_ID, categoryId + "")
                .appendQueryParameter(PARAM_START_TIME, startTime + "")
                .appendQueryParameter(PARAM_END_TIME, endTime + "")
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
