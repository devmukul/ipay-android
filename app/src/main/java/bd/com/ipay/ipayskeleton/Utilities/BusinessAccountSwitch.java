package bd.com.ipay.ipayskeleton.Utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.HomeActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRoles.BusinessAccountDetails;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRoles.BusinessService;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ACLManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;

public class BusinessAccountSwitch implements HttpResponseListener {

    public int businessAccountId;
    private ProgressDialog mProgressDialog;
    private HttpRequestGetAsyncTask mSwitchAccountAsyncTask = null;
    private BusinessAccountDetails mBusinessAccoutnDetails;
    private Context context;

    public BusinessAccountSwitch(int businessAccountId, Context context) {
        this.businessAccountId = businessAccountId;
        this.context = context;
        mProgressDialog = new ProgressDialog(context);
    }

    public void requestSwitchAccount() {
        mSwitchAccountAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_SWITCH_ACCOUNT, Constants.BASE_URL_MM +
                Constants.URL_SWITCH_ACCOUNT + Integer.toString(businessAccountId), context, this);

        mSwitchAccountAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        mProgressDialog.setMessage(context.getResources().getString(R.string.switching));
        mProgressDialog.show();
    }

    private int[] getServiceIDsFromServiceList(List<BusinessService> businessServiceList) {
        List<Long> serviceIDs = new ArrayList<>();
        //adding the logout service by default
        serviceIDs.add((long)8026);
        for (BusinessService businessService : businessServiceList) {
            serviceIDs.add(businessService.getServiceCode());
        }
        int[] serviceIdArray = new int[serviceIDs.size()];
        for (int i = 0; i < serviceIDs.size(); i++) {
            serviceIdArray[i] = Integer.parseInt(Long.toString(serviceIDs.get(i)));
        }
        return serviceIdArray;
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mSwitchAccountAsyncTask = null;
            Toast.makeText(context, R.string.service_not_available, Toast.LENGTH_LONG).show();
            return;
        } else {
            Gson gson = new Gson();
            try {
                if (result.getApiCommand().equals(Constants.COMMAND_SWITCH_ACCOUNT)) {
                    switch (result.getStatus()) {
                        case Constants.HTTP_RESPONSE_STATUS_OK:
                            mBusinessAccoutnDetails = gson.fromJson(result.getJsonString(), BusinessAccountDetails.class);
                            TokenManager.setOnAccountId(null);
                            TokenManager.setOnAccountId(Long.toString(mBusinessAccoutnDetails.getBusinessAccountId()));
                            ProfileInfoCacheManager.setOnAccountId(Long.toString(mBusinessAccoutnDetails.getBusinessAccountId()));
                            ACLManager.updateAllowedServiceArray
                                    (getServiceIDsFromServiceList(mBusinessAccoutnDetails.getServiceList()));
                            ProfileInfoCacheManager.setAccountType(Constants.BUSINESS_ACCOUNT_TYPE);
                            ProfileInfoCacheManager.setUserName(mBusinessAccoutnDetails.getBusinessName());
                            ProfileInfoCacheManager.setSwitchAccount(true);
                            ProfileInfoCacheManager.setProfilePictureUrl(mBusinessAccoutnDetails.getProfilePictures().get(0).getUrl());
                            Intent intent = new Intent(context, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent);
                            break;
                        default:
                            Toaster.makeText(context, context.getString(R.string.cant_switch), Toast.LENGTH_LONG);
                    }
                }
            } catch (Exception e) {
                Toaster.makeText(context, context.getString(R.string.cant_switch), Toast.LENGTH_LONG);
            }
        }
    }
}
