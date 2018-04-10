package bd.com.ipay.ipayskeleton;

import android.content.Context;
import android.widget.Toast;

import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class HttpErrorHandler {

    public static boolean isErrorFound(GenericHttpResponse result, Context context) {
        if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR) {
            Toast.makeText(context, "Internal Server Error", Toast.LENGTH_LONG).show();
            return true;
        } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            Toast.makeText(context, "Not found", Toast.LENGTH_LONG).show();
            return true;
        } else if (result.getStatus() == 0) {
            Toast.makeText(context, result.getErrorMessage(), Toast.LENGTH_LONG).show();
            return true;
        } else if (result == null) {
            Toast.makeText(context, context.getString(R.string.service_not_available), Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

}
