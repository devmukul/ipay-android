package bd.com.ipay.ipayskeleton.PaymentFragments.CommonFragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.google.gson.Gson;

import java.math.BigDecimal;

import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.Model.MMModule.ServiceCharge.GetServiceChargeRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.ServiceCharge.GetServiceChargeResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

/**
 * Be sure to call the attemptGetServiceCharge method at the end of your onCreateView method
 * of the fragment. If you override httpResponseReceiver, make sure to call
 * super.httpResponseReceiver() first.
 */
public abstract class ReviewFragment extends Fragment implements HttpResponseListener {

    private ProgressDialog mProgressDialog;

    private HttpRequestPostAsyncTask mServiceChargeTask = null;
    private GetServiceChargeResponse mGetServiceChargeResponse;

    /**
     * Service ID used to query the service charge
     */
    public abstract int getServiceID();

    /**
     * The original amount you have entered in the previous page
     */
    public abstract BigDecimal getAmount();

    /**
     * This method will be called once the service charge loading is finished. You should populate
     * the service charge and net amount view withing this method.
     */
    public abstract void onServiceChargeLoadFinished(BigDecimal serviceCharge);

    protected void attemptGetServiceCharge() {

        if (mServiceChargeTask != null) {
            return;
        }

        SharedPreferences pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Context.MODE_PRIVATE);
        int accountType = pref.getInt(Constants.ACCOUNT_TYPE, Constants.PERSONAL_ACCOUNT_TYPE);
        int accountClass = Constants.DEFAULT_USER_CLASS;

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.please_wait));
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                getActivity().finish();
            }
        });
        mProgressDialog.show();

        GetServiceChargeRequest mServiceChargeRequest = new GetServiceChargeRequest(getServiceID(), accountType, accountClass);
        Gson gson = new Gson();
        String json = gson.toJson(mServiceChargeRequest);
        mServiceChargeTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_SERVICE_CHARGE,
                Constants.BASE_URL_SM + Constants.URL_SERVICE_CHARGE, json, getActivity());
        mServiceChargeTask.mHttpResponseListener = this;
        mServiceChargeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        if (result == null) {
            mProgressDialog.dismiss();
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }


        Gson gson = new Gson();

        mProgressDialog.dismiss();

        if (result.getApiCommand().equals(Constants.COMMAND_GET_SERVICE_CHARGE)) {
            try {
                mGetServiceChargeResponse = gson.fromJson(result.getJsonString(), GetServiceChargeResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (mGetServiceChargeResponse != null) {
                        if (mGetServiceChargeResponse.getServiceCharge(getAmount()).compareTo(BigDecimal.ZERO) < 0) {
                            Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        } else {
                            onServiceChargeLoadFinished(mGetServiceChargeResponse.getServiceCharge(getAmount()));
                        }

                    } else {
                        Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                        return;
                    }
                } else {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();

                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }


            mServiceChargeTask = null;
        }
    }
}
