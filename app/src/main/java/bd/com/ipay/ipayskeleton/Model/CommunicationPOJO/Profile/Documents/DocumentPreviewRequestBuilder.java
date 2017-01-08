package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.Documents;

import android.net.Uri;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class DocumentPreviewRequestBuilder {

    private final static String PARAM_DOCUMENT_TYPE = "documentType";
    private final static String PARAM_DOCUMENT_ID = "documentId";
    private final static String PARAM_RESOURCE_TOKEN = "resourceToken";

    public static String generateUri(String resourceToken, String fileUrl, String documentId, String documentType) {
        Uri.Builder uri = Uri.parse(Constants.BASE_URL_FTP_SERVER + fileUrl)
                .buildUpon();

        uri.appendQueryParameter(PARAM_RESOURCE_TOKEN, resourceToken);
        uri.appendQueryParameter(PARAM_DOCUMENT_ID, documentId);
        uri.appendQueryParameter(PARAM_DOCUMENT_TYPE, documentType);

        return uri.build().toString();
    }

}
