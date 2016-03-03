package bd.com.ipay.ipayskeleton.Model.MMModule.Events;

import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class GetEventListPersonalUserRequestBuilder {

    private final String PARAM_CATEGORY_ID = "categoryId";
    private final String PARAM_START_TIME = "startTime";
    private final String PARAM_END_TIME = "endTime";

    private long categoryId;
    private long startTime;
    private long endTime;

    private String generatedUri;

    public GetEventListPersonalUserRequestBuilder(long categoryId, long startTime, long endTime) {
        this.categoryId = categoryId;
        this.startTime = startTime;
        this.endTime = endTime;

        generateUri(categoryId, startTime, endTime);
    }

    private void generateUri(long categoryId, long startTime, long endTime) {

        try {
            URI uri = new URIBuilder()
                    .setScheme("http")
                    .setHost(Constants.BASE_URL_GET_EM)
                    .setPort(Constants.BASE_URL_GET_EM_PORT)
                    .setPath(Constants.BASE_URL_GET_EM_PATH + "/" + Constants.URL_EVENT_LIST)
                    .addParameter(PARAM_CATEGORY_ID, categoryId + "")
                    .addParameter(PARAM_START_TIME, startTime + "")
                    .addParameter(PARAM_END_TIME, endTime + "")
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
