package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Ticket;

import android.net.Uri;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class GetTicketCategoriesRequestBuilder {

    public Uri generateUri() {
        Uri.Builder uri = Uri.parse(Constants.BASE_URL_ADMIN + Constants.URL_GET_TICKET_CATEGORIES)
                .buildUpon();

        return uri.build();
    }

}
