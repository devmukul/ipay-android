package bd.com.ipay.ipayskeleton.Widget.View;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.Locale;

import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.BulkSignUp.GetUserDetailsResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TrustedDevice.AddToTrustedDeviceRequest;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.BulkSignupUserDetailsCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.CircleTransform;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class BulkSignUpHelperDialog implements HttpResponseListener {
	private final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
	private AlertDialog alertDialog;

    private HttpRequestPostAsyncTask mAddTrustedDeviceTask = null;

	private final RequestManager requestManager;
	private final CircleTransform circleTransform;
	Context context;

	public BulkSignUpHelperDialog(Context context, String text) {

        this.context = context;
		alertDialog = new AlertDialog.Builder(context)
				.setTitle("Info")
				.setMessage(text)
				.setCancelable(false)
				.setNegativeButton("CANCEL", null)
				.create();

		requestManager = Glide.with(context);
		circleTransform = new CircleTransform(context);
	}

	public void setPositiveButton(CharSequence text, final DialogInterface.OnClickListener onClickListener) {
	    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, text ,onClickListener);
	}

	public void setNegativeButton(final DialogInterface.OnClickListener onClickListener) {
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL" ,onClickListener);
	}

    public void setCheckedResponse(String checkedInfo) {
	    sendCheckedResponse(checkedInfo);
    }

	public void show() {
		if (!alertDialog.isShowing())
			alertDialog.show();
	}

	public void cancel() {
		if (alertDialog.isShowing())
			alertDialog.cancel();
	}

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {

        if (HttpErrorHandler.isErrorFound(result, context , mProgressDialog)) {
            hideProgressDialog();
            mLoginTask = null;
            mGetAllAddedCards = null;
            mGetProfileCompletionStatusTask = null;
            mGetProfileInfoTask = null;
            return;
        }
        Gson gson = new Gson();

        switch (result.getApiCommand()) {
            case Constants.COMMAND_GET_BULK_SIGN_UP_USER_DETAILS:
                try {
                    mGetUserDetailsResponse = gson.fromJson(result.getJsonString(), GetUserDetailsResponse.class);
                    BulkSignupUserDetailsCacheManager.updateBulkSignupUserInfoCache(mGetUserDetailsResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getProfileCompletionStatus();
                mGetBulkSignupUserDetailsTask = null;
                break;
        }

    }

    private void sendCheckedResponse(String checkedInfo) {
        if (mAddTrustedDeviceTask != null)
            return;
        mAddTrustedDeviceTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ADD_TRUSTED_DEVICE,
                Constants.BASE_URL_MM + Constants.URL_ADD_TRUSTED_DEVICE+"?checkedInfoName="+checkedInfo, null , context , true);
        mAddTrustedDeviceTask.mHttpResponseListener = this;
        mAddTrustedDeviceTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
