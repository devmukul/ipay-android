package bd.com.ipay.ipayskeleton.PaymentFragments.CommonFragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.google.gson.Gson;

import java.math.BigDecimal;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.AddMoneyActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.PaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.RequestMoneyActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.RequestPaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.SendMoneyActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.TopUpActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.WithdrawMoneyActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.BusinessRule;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.GetBusinessRuleWithServiceChargeRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.GetBusinessRulesWithServiceChargeResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.ServiceCharge.GetServiceChargeRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.ServiceCharge.GetServiceChargeResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.BusinessRuleConstants;
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
    private HttpRequestGetAsyncTask mGetBusinessRuleTask = null;

    private GetServiceChargeResponse mGetServiceChargeResponse;

    private GetBusinessRulesWithServiceChargeResponse mBusinessRulesResponseWithServiceCharge;

    // Service ID used to query the service charge
    protected abstract int getServiceID();

    // The original amount you have entered in the previous page
    protected abstract BigDecimal getAmount();

    /**
     * This method will be called once the service charge loading is finished. You should populate
     * the service charge and net amount view withing this method.
     */
    protected abstract void onServiceChargeLoadFinished(BigDecimal serviceCharge);

    protected abstract void onPinLoadFinished(boolean isPinRequired);

    protected void attemptGetServiceCharge() {

        if (mServiceChargeTask != null) {
            return;
        }

        int accountType = ProfileInfoCacheManager.getAccountType();
        int accountClass = Constants.DEFAULT_USER_CLASS;

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.please_wait));
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
                Constants.BASE_URL_SM + Constants.URL_SERVICE_CHARGE, json, getActivity());
        mServiceChargeTask.mHttpResponseListener = this;
        mServiceChargeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    protected void attemptGetBusinessRuleWithServiceCharge(int serviceID) {

        if (mGetBusinessRuleTask != null) return;

        String mUri = new GetBusinessRuleWithServiceChargeRequestBuilder(serviceID).getGeneratedUri();
        mGetBusinessRuleTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_BUSINESS_RULE_WITH_SERVICE_CHARGE,
                mUri, getActivity(), this);

        mGetBusinessRuleTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {

        if (mProgressDialog != null && isAdded())
            mProgressDialog.dismiss();

        if (result == null) {
            mGetBusinessRuleTask = null;
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

                        onPinLoadFinished(mGetServiceChargeResponse.isPinRequired());

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

        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_BUSINESS_RULE_WITH_SERVICE_CHARGE)) {

            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                try {
                    gson = new Gson();
                    mBusinessRulesResponseWithServiceCharge = gson.fromJson(result.getJsonString(), GetBusinessRulesWithServiceChargeResponse.class);
                    if (mBusinessRulesResponseWithServiceCharge != null) {

                        if (mBusinessRulesResponseWithServiceCharge.getBusinessRules() != null) {
                            for (BusinessRule rule : mBusinessRulesResponseWithServiceCharge.getBusinessRules()) {
                                switch (rule.getRuleID()) {
                                    case BusinessRuleConstants.SERVICE_RULE_SEND_MONEY_MAX_AMOUNT_PER_PAYMENT:
                                        SendMoneyActivity.mMandatoryBusinessRules.setMAX_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                                        break;
                                    case BusinessRuleConstants.SERVICE_RULE_SEND_MONEY_MIN_AMOUNT_PER_PAYMENT:
                                        SendMoneyActivity.mMandatoryBusinessRules.setMIN_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                                        break;
                                    case BusinessRuleConstants.SERVICE_RULE_ADD_MONEY_MAX_AMOUNT_PER_PAYMENT:
                                        AddMoneyActivity.mMandatoryBusinessRules.setMAX_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                                        break;
                                    case BusinessRuleConstants.SERVICE_RULE_ADD_MONEY_MIN_AMOUNT_PER_PAYMENT:
                                        AddMoneyActivity.mMandatoryBusinessRules.setMIN_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                                        break;
                                    case BusinessRuleConstants.SERVICE_RULE_TOP_UP_MAX_AMOUNT_PER_PAYMENT:
                                        TopUpActivity.mMandatoryBusinessRules.setMAX_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                                        break;
                                    case BusinessRuleConstants.SERVICE_RULE_TOP_UP_MIN_AMOUNT_PER_PAYMENT:
                                        TopUpActivity.mMandatoryBusinessRules.setMIN_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                                        break;
                                    case BusinessRuleConstants.SERVICE_RULE_WITHDRAW_MONEY_MAX_AMOUNT_PER_PAYMENT:
                                        WithdrawMoneyActivity.mMandatoryBusinessRules.setMAX_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                                        break;
                                    case BusinessRuleConstants.SERVICE_RULE_WITHDRAW_MONEY_MIN_AMOUNT_PER_PAYMENT:
                                        WithdrawMoneyActivity.mMandatoryBusinessRules.setMIN_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                                        break;
                                    case BusinessRuleConstants.SERVICE_RULE_MAKE_PAYMENT_MAX_AMOUNT_PER_PAYMENT:
                                       PaymentActivity.mMandatoryBusinessRules.setMAX_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                                        break;
                                    case BusinessRuleConstants.SERVICE_RULE_MAKE_PAYMENT_MIN_AMOUNT_PER_PAYMENT:
                                        PaymentActivity.mMandatoryBusinessRules.setMIN_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                                        break;
                                    case BusinessRuleConstants.SERVICE_RULE_REQUEST_MONEY_MAX_AMOUNT_PER_PAYMENT:
                                        RequestMoneyActivity.mMandatoryBusinessRules.setMAX_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                                        break;
                                    case BusinessRuleConstants.SERVICE_RULE_REQUEST_MONEY_MIN_AMOUNT_PER_PAYMENT:
                                        RequestMoneyActivity.mMandatoryBusinessRules.setMIN_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                                        break;
                                    case BusinessRuleConstants.SERVICE_RULE_REQUEST_PAYMENT_MAX_AMOUNT_PER_PAYMENT:
                                        RequestPaymentActivity.mMandatoryBusinessRules.setMAX_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                                        break;
                                    case BusinessRuleConstants.SERVICE_RULE_REQUEST_PAYMENT_MIN_AMOUNT_PER_PAYMENT:
                                        RequestPaymentActivity.mMandatoryBusinessRules.setMIN_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                                        break;
                                }
                            }
                        }

                        if (mBusinessRulesResponseWithServiceCharge.getFeeCharge() != null) {
                            if (mBusinessRulesResponseWithServiceCharge.getFeeCharge().getServiceCharge(getAmount()).compareTo(BigDecimal.ZERO) < 0) {
                                Toaster.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT);
                                getActivity().finish();
                            } else {
                                onServiceChargeLoadFinished(mBusinessRulesResponseWithServiceCharge.getFeeCharge().getServiceCharge(getAmount()));
                            }

                        } else {
                            Toaster.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT);
                            getActivity().finish();
                            return;
                        }
                        if (mBusinessRulesResponseWithServiceCharge.getPinRequired() != null) {
                          //  onPinLoadFinished(mBusinessRulesResponseWithServiceCharge.getPinRequired());
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toaster.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT);
                }

            } else {
                if (getActivity() != null)
                    Toaster.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT);
            }

            mGetBusinessRuleTask = null;

        }

    }
}
