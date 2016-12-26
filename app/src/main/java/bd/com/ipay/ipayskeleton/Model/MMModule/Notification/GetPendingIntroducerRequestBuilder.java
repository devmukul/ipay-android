package bd.com.ipay.ipayskeleton.Model.MMModule.Notification;

import android.net.Uri;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class GetPendingIntroducerRequestBuilder {
    private String generatedUri;
    private long requestID;
    private String introducerAcceptRejectStatus;

    public GetPendingIntroducerRequestBuilder(long requestID, String introducerAcceptRejectStatus)
    {
        this.requestID = requestID;
        this.introducerAcceptRejectStatus =introducerAcceptRejectStatus;

        generateUri();
    }

    private void generateUri() {
        Uri uri = Uri.parse(Constants.BASE_URL_MM + Constants.URL_PENDING_INTRODUCER_ACTION + "/" + requestID + "/" + introducerAcceptRejectStatus)
                .buildUpon()
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