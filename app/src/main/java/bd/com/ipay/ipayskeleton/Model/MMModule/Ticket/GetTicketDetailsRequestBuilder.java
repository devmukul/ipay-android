package bd.com.ipay.ipayskeleton.Model.MMModule.Ticket;

import android.net.Uri;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class GetTicketDetailsRequestBuilder {

    private final String PARAM_ID = "id";

    public Uri generateUri(long id) {
        Uri.Builder uri = Uri.parse(Constants.BASE_URL_ADMIN + Constants.URL_GET_TICKET_DETAILS)
                .buildUpon();

        uri.appendQueryParameter(PARAM_ID, Long.toString(id));

        return uri.build();
    }

}
