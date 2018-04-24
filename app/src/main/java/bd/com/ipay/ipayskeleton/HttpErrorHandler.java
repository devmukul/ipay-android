package bd.com.ipay.ipayskeleton;

import android.app.Dialog;
import android.content.Context;
import android.widget.Toast;

import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomProgressDialog;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class HttpErrorHandler {

    public static boolean isErrorFound(GenericHttpResponse result, Context context, Dialog alertDialog) {
        if (alertDialog != null && alertDialog instanceof CustomProgressDialog) {
            if (result == null) {
                ((CustomProgressDialog) alertDialog).
                        showFailureAnimationAndMessage(context.getString(R.string.service_not_available));
                return true;
            } else if (result.getErrorMessage() != null) {
                ((CustomProgressDialog) alertDialog).showFailureAnimationAndMessage(result.getErrorMessage());
                return true;
            } else {
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
                    ((CustomProgressDialog) alertDialog).showFailureAnimationAndMessage("Not Found");
                    return true;
                } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR) {
                    ((CustomProgressDialog) alertDialog).showFailureAnimationAndMessage("Internal Server Error");
                    return true;
                }
                return false;
            }
        } else {
            if (alertDialog != null) {
                alertDialog.dismiss();
            }
            if (result == null) {
                return true;
            } else if (result.getErrorMessage() != null) {
                if (!result.isSilent()) {
                    Toast.makeText(context, result.getErrorMessage(), Toast.LENGTH_LONG).show();
                }
                return true;
            } else {
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
                    return true;
                } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR) {
                    Toast.makeText(context, "Internal Server Error", Toast.LENGTH_LONG).show();
                    return true;
                }
                return false;

            }

        }

    }
}
