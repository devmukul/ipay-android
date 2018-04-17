package bd.com.ipay.ipayskeleton.PaymentFragments.CommonFragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.google.gson.Gson;

import java.math.BigDecimal;

import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.ServiceCharge.GetServiceChargeRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.ServiceCharge.GetServiceChargeResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;

/**
 * Be sure to call the attemptGetServiceCharge method at the end of your onCreateView method
 * of the fragment. If you override httpResponseReceiver, make sure to call
 * super.httpResponseReceiver() first.
 */
public abstract class ReviewFragment extends Fragment implements HttpResponseListener {

    private ProgressDialog mProgressDialog;

    private HttpRequestPostAsyncTask mServiceChargeTask = null;

    private GetServiceChargeResponse mGetServiceChargeResponse;

    // Service ID used to query the service charge
    protected abstract int getServiceID();

    // The original amount you have entered in the previous page
    protected abstract BigDecimal getAmount();

    /**
     * This method will be called once the service charge loading is finished. You should populate
     * the service charge and net amount view withing this method.
     */
    protected abstract void onServiceChargeLoadFinished(BigDecimal serviceCharge);

    protected void attemptGetServiceCharge() {

        if (mServiceChargeTask != null) {
            return;
        }

        int accountType = ProfileInfoCacheManager.getAccountType();
        int accountClass = Constants.DEFAULT_USER_CLASS;

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.please_wait_loading));
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (getActivity() != null)
                    getActivity().finish();
            }
        });
        mProgressDialog.show();

        GetServiceChargeRequest mServiceChargeRequest = new GetServiceChargeRequest(getServiceID(), accountType, accountClass);
        Gson gson = new Gson();
        String json = gson.toJson(mServiceChargeRequest);
        mServiceChargeTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_SERVICE_CHARGE,
                Constants.BASE_URL_SM + Constants.URL_SERVICE_CHARGE, json, getActivity(),false);
        mServiceChargeTask.mHttpResponseListener = this;
        mServiceChargeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {

        if (mProgressDialog != null && isAdded())
            mProgressDialog.dismiss();

        if (result == null) {
            mServiceChargeTask = null;

            if (getActivity() != null) {
                Toaster.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_LONG);
            }

            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_GET_SERVICE_CHARGE)) {
            try {
                mGetServiceChargeResponse = gson.fromJson(result.getJsonString(), GetServiceChargeResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (mGetServiceChargeResponse != null) {
                        if (mGetServiceChargeResponse.getServiceCharge(getAmount()).compareTo(BigDecimal.ZERO) < 0) {
                            Toaster.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT);
                            getActivity().finish();
                        } else {
                            onServiceChargeLoadFinished(mGetServiceChargeResponse.getServiceCharge(getAmount()));
                        }

                    } else {
                        Toaster.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT);
                        getActivity().finish();
                        return;
                    }
                } else {
                    if (getActivity() != null) {
                        Toaster.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT);
                        getActivity().finish();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();

                Toaster.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT);
                getActivity().finish();
            }

            mServiceChargeTask = null;

        }
    }
}
