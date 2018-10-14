package bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.LankaBangla.Dps;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import bd.com.ipay.ipayskeleton.Activities.UtilityBillPayActivities.IPayUtilityBillPayActionActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomProgressDialog;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill.LankaBanglaDpsUserInfoResponse;
import bd.com.ipay.ipayskeleton.PaymentFragments.IpayAbstractDpsInputFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;
import bd.com.ipay.ipayskeleton.Widget.View.DpsBillDetailsDialog;

public class LankaBanglaDpsNumberInputFragment extends IpayAbstractDpsInputFragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetLankaBanglaDpsUserInfoAsyncTask = null;
    private final Gson gson = new GsonBuilder().create();
    private CustomProgressDialog mProgressDialog;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() != null)
            getActivity().setTitle(R.string.lanka_bangla_dps);

        mProgressDialog = new CustomProgressDialog(getActivity());
    }

    @Override
    public boolean verifyInput() {
        if (TextUtils.isEmpty(getDpsNumber())) {
            showErrorMessage(getString(R.string.empty_dps_number_message));
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void performButtonAction() {
        if (verifyInput()) {
            attemptGetLankaBanglaDpsUser();
        }
    }

    private void attemptGetLankaBanglaDpsUser() {
        String url = Constants.BASE_URL_UTILITY + Constants.LANKABANGLA_DPS_USER + getDpsNumber();
        mGetLankaBanglaDpsUserInfoAsyncTask = new HttpRequestGetAsyncTask(
                Constants.COMMAND_GET_LANKABANGLA_DPS_CUSTOMER_INFO, url, getContext(), false);
        mGetLankaBanglaDpsUserInfoAsyncTask.mHttpResponseListener = this;
        mProgressDialog.setLoadingMessage("Please wait");
        mProgressDialog.showDialog();
        mGetLankaBanglaDpsUserInfoAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (getActivity() == null)
            return;

        if (HttpErrorHandler.isErrorFound(result, getContext(), mProgressDialog)) {
            mGetLankaBanglaDpsUserInfoAsyncTask = null;
            if (result != null && result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
                LankaBanglaDpsUserInfoResponse lankaBanglaDpsUserInfoResponse = gson.fromJson(result.getJsonString(), LankaBanglaDpsUserInfoResponse.class);
                if (!TextUtils.isEmpty(lankaBanglaDpsUserInfoResponse.getMessage())) {
                    Toaster.makeText(getActivity(), lankaBanglaDpsUserInfoResponse.getMessage(), Toast.LENGTH_SHORT);
                } else {
                    Toaster.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT);
                }
            }
        } else {
            try {
                switch (result.getApiCommand()) {
                    case Constants.COMMAND_GET_LANKABANGLA_DPS_CUSTOMER_INFO:
                        mGetLankaBanglaDpsUserInfoAsyncTask = null;
                        LankaBanglaDpsUserInfoResponse lankaBanglaDpsUserInfoResponse = gson.fromJson(result.getJsonString(), LankaBanglaDpsUserInfoResponse.class);
                        switch (result.getStatus()) {
                            case Constants.HTTP_RESPONSE_STATUS_OK:
                                showLankaBanglaUserInfo(lankaBanglaDpsUserInfoResponse);
                                break;
                            default:
                                if (!TextUtils.isEmpty(lankaBanglaDpsUserInfoResponse.getMessage())) {
                                    Toaster.makeText(getActivity(), lankaBanglaDpsUserInfoResponse.getMessage(), Toast.LENGTH_SHORT);
                                } else {
                                    Toaster.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT);
                                }
                                break;
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toaster.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT);
            }
        }
        mProgressDialog.dismissDialog();
    }

    private void showLankaBanglaUserInfo(final LankaBanglaDpsUserInfoResponse lankaBanglaCustomerInfoResponse) {
        if (getActivity() == null)
            return;

        final DpsBillDetailsDialog billDetailsDialog = new DpsBillDetailsDialog(getContext());
        billDetailsDialog.setTitle(getString(R.string.bill_details));
        billDetailsDialog.setClientLogoImageResource(R.drawable.ic_lankabd2);

        billDetailsDialog.setBillTitleInfo(lankaBanglaCustomerInfoResponse.getAccountNumber());
        billDetailsDialog.setBillSubTitleInfo(lankaBanglaCustomerInfoResponse.getAccountTitle());
        billDetailsDialog.setAccountName(lankaBanglaCustomerInfoResponse.getAccountTitle());
        billDetailsDialog.setAccountNumber(lankaBanglaCustomerInfoResponse.getAccountNumber());
        billDetailsDialog.setBranchID(lankaBanglaCustomerInfoResponse.getBranchId());
        billDetailsDialog.setMaturityDate(lankaBanglaCustomerInfoResponse.getAccountMaturityDate());
        billDetailsDialog.setInstallmentAmount(Long.toString(lankaBanglaCustomerInfoResponse.getInstallmentAmount()));

        billDetailsDialog.setCloseButtonAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                billDetailsDialog.cancel();
            }
        });
        billDetailsDialog.setPayBillButtonAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                billDetailsDialog.cancel();
                Bundle bundle = new Bundle();
                bundle.putString(LankaBanglaDpsAmountInputFragment.ACCOUNT_NUMBER, getDpsNumber());
                bundle.putString(LankaBanglaDpsAmountInputFragment.INSTALLMENT_AMOUNT, Long.toString(lankaBanglaCustomerInfoResponse.getInstallmentAmount()));
                Utilities.hideKeyboard(getActivity());
                final LankaBanglaDpsAmountInputFragment lankaBanglaDpsAmountInputFragment = new LankaBanglaDpsAmountInputFragment();

                if (getActivity() instanceof IPayUtilityBillPayActionActivity) {
                    ((IPayUtilityBillPayActionActivity) getActivity()).switchFragment(lankaBanglaDpsAmountInputFragment, bundle, 2, true);
                }
            }
        });
        billDetailsDialog.show();
    }
}
