package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Ticket;

import android.net.Uri;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class UploadTicketAttachmentRequestBuilder {
    public Uri generateUri() {
        Uri.Builder uri = Uri.parse(Constants.BASE_URL_ADMIN + Constants.URL_UPLOAD_TICKET_ATTACHMENT)
                .buildUpon();

        return uri.build();
    }
}
