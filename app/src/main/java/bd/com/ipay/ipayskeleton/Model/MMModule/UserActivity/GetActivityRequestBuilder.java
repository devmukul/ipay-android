package bd.com.ipay.ipayskeleton.Model.MMModule.UserActivity;

import android.net.Uri;

import java.net.URLEncoder;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class GetActivityRequestBuilder {

    private final static String PARAM_TYPE = "type";
    private final static String PARAM_FROM_DATE = "fromDate";
    private final static String PARAM_TO_DATE = "fromDate";
    private final static String PARAM_PAGE = "page";
    private final static String PARAM_COUNT = "count";

    public static String generateUri(int type, long fromDate, long toDate, int page, int count) {
        Uri uri = Uri.parse(Constants.BASE_URL_MM + "/" + Constants.URL_USER_ACTIVITY)
                .buildUpon()
                .appendQueryParameter(PARAM_TYPE, Integer.toString(type))
                .appendQueryParameter(PARAM_FROM_DATE, Long.toString(fromDate))
                .appendQueryParameter(PARAM_TO_DATE, Long.toString(toDate))
                .appendQueryParameter(PARAM_PAGE, Integer.toString(page))
                .appendQueryParameter(PARAM_COUNT, Integer.toString(count))
                .build();

        return uri.toString();
    }

}
