
package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.IPayHere;

import android.net.Uri;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class IPayHereRequestUrlBuilder {

    private final static String PARAM_RADIUS = "radiusInMeter";
    private final static String PARAM_LAT = "latitude";
    private final static String PARAM_LON = "longitude";

    public static String generateUri(Long radius, String lat, String lon) {
        Uri.Builder uri = Uri.parse(Constants.BASE_URL_MM + Constants.URL_BUSINESS_NEARBY)
                .buildUpon();

        if (radius != null)
            uri.appendQueryParameter(PARAM_RADIUS, Long.toString(radius));
        if (lat != null)
            uri.appendQueryParameter(PARAM_LAT, lat);
        if (lon != null)
            uri.appendQueryParameter(PARAM_LON, lon);

        return uri.build().toString();
    }


    public static String generateUri( String lat, String lon) {
        Uri.Builder uri = Uri.parse(Constants.BASE_URL_MM + Constants.URL_BUSINESS_NEARBY)
                .buildUpon();

        if (lat != null)
            uri.appendQueryParameter(PARAM_LAT, lat);
        if (lon != null)
            uri.appendQueryParameter(PARAM_LON, lon);

        return uri.build().toString();
    }

}
