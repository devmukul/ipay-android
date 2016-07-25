package bd.com.ipay.ipayskeleton.Model.MMModule.UserActivity;

import android.net.Uri;

import java.security.InvalidParameterException;
import java.util.Calendar;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class GetActivityRequestBuilder {

    private final static String PARAM_TYPE = "type";
    private final static String PARAM_FROM_DATE = "fromDate";
    private final static String PARAM_TO_DATE = "toDate";
    private final static String PARAM_PAGE = "page";
    private final static String PARAM_COUNT = "count";

    public static String generateUri(Integer type, Calendar fromDate, Calendar toDate, Integer page, Integer count) {
        Uri.Builder uri = Uri.parse(Constants.BASE_URL_MM + Constants.URL_USER_ACTIVITY)
                .buildUpon();

        if (type != null)
            uri.appendQueryParameter(PARAM_TYPE, Integer.toString(type));
        if (fromDate != null)
            uri.appendQueryParameter(PARAM_FROM_DATE, Long.toString(fromDate.getTimeInMillis()));
        if (toDate != null)
            uri.appendQueryParameter(PARAM_TO_DATE, Long.toString(toDate.getTimeInMillis()));
        if (page == null)
            throw new InvalidParameterException("page is a required parameter");
        else
            uri.appendQueryParameter(PARAM_PAGE, Integer.toString(page));
        if (count != null)
            uri.appendQueryParameter(PARAM_COUNT, Integer.toString(count));

        return uri.build().toString();
    }

}
