package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory;

import android.net.Uri;

import java.security.InvalidParameterException;
import java.util.Calendar;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class TransactionHistoryRequest {

    private final static String PARAM_SERVICE_ID = "serviceId";
    private final static String PARAM_PAGE = "page";
    private final static String PARAM_START_MS = "startMs";
    private final static String PARAM_END_MS = "endMs";
    private final static String PARAM_LIMIT = "limit";
    private final static String PARAM_SEARCH_TEXT = "searchText";

    public static String generateUri(Integer serviceId, Calendar fromDate, Calendar toDate, Integer page, Integer limit, String searchText) {
        Uri.Builder uri = Uri.parse(Constants.BASE_URL_SM + Constants.URL_TRANSACTION_HISTORY_COMPLETED)
                .buildUpon();

        if (serviceId != null)
            uri.appendQueryParameter(PARAM_SERVICE_ID, Integer.toString(serviceId));
        if (fromDate != null)
            uri.appendQueryParameter(PARAM_START_MS, Long.toString(fromDate.getTimeInMillis()));
        if (toDate != null)
            uri.appendQueryParameter(PARAM_END_MS, Long.toString(toDate.getTimeInMillis()));
        if (searchText != null)
            uri.appendQueryParameter(PARAM_SEARCH_TEXT, searchText);
        if (page == null)
            throw new InvalidParameterException("page is a required parameter");
        else
            uri.appendQueryParameter(PARAM_PAGE, Integer.toString(page));
        if (limit != null)
            uri.appendQueryParameter(PARAM_LIMIT, Integer.toString(limit));

        return uri.build().toString();
    }

    public static String generateUri(Integer serviceId, Calendar fromDate, Calendar toDate, Integer page, Integer limit) {
        Uri.Builder uri = Uri.parse(Constants.BASE_URL_SM + Constants.URL_TRANSACTION_HISTORY_COMPLETED)
                .buildUpon();

        if (serviceId != null)
            uri.appendQueryParameter(PARAM_SERVICE_ID, Integer.toString(serviceId));
        if (fromDate != null)
            uri.appendQueryParameter(PARAM_START_MS, Long.toString(fromDate.getTimeInMillis()));
        if (toDate != null)
            uri.appendQueryParameter(PARAM_END_MS, Long.toString(toDate.getTimeInMillis()));
        if (page == null)
            throw new InvalidParameterException("page is a required parameter");
        else
            uri.appendQueryParameter(PARAM_PAGE, Integer.toString(page));
        if (limit != null)
            uri.appendQueryParameter(PARAM_LIMIT, Integer.toString(limit));

        return uri.build().toString();
    }
}
