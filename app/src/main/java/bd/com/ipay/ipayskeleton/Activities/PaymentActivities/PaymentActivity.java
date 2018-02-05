package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Aspect.ValidateAccess;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.MandatoryBusinessRules;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetUserInfoRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetUserInfoResponse;
import bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments.MakePaymentFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments.PaymentRequestsReceivedFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class PaymentActivity extends BaseActivity implements HttpResponseListener {

    private FloatingActionButton mFabMakingPayment;
    private boolean switchedToPendingList = true;
    public static final MandatoryBusinessRules mMandatoryBusinessRules = new MandatoryBusinessRules();
    private HttpRequestGetAsyncTask mGetProfileInfoTask = null;
    private GetUserInfoResponse mGetUserInfoResponse;

    private ProgressDialog mProgressDialog;

    private String address;
    private String district;
    private String country;

    /**
     * If this value is set in the intent extras,
     * you would be taken directly to the new request page
     */
    public static final String LAUNCH_NEW_REQUEST = "LAUNCH_NEW_REQUEST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        mFabMakingPayment = (FloatingActionButton) findViewById(R.id.fab_payment_making);
        mProgressDialog = new ProgressDialog(this);

        if (getIntent().hasExtra(Constants.MOBILE_NUMBER) || getIntent().getBooleanExtra(LAUNCH_NEW_REQUEST, false)) {
            if (getIntent().getStringExtra(Constants.MOBILE_NUMBER) != null) {
                getProfileInfo(getIntent().getStringExtra(Constants.MOBILE_NUMBER));
            } else {
                switchToMakePaymentFragment(null);
            }
        } else {
            switchToReceivedPaymentRequestsFragment();
        }

        mFabMakingPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            @ValidateAccess(ServiceIdConstants.MAKE_PAYMENT)
            public void onClick(View v) {
                switchToMakePaymentFragment();
            }
        });

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getProfileInfo(String mobileNumber) {
        if (mGetProfileInfoTask != null) {
            return;
        }
        mProgressDialog.setMessage("Loading");
        mProgressDialog.show();

        GetUserInfoRequestBuilder mGetUserInfoRequestBuilder = new GetUserInfoRequestBuilder(mobileNumber);

        String mUri = mGetUserInfoRequestBuilder.getGeneratedUri();
        mGetProfileInfoTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_USER_INFO,
                mUri, this, this);
        mProgressDialog.setMessage(getString(R.string.loading));
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
        mGetProfileInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Utilities.hideKeyboard(this);
        if (item.getItemId() == android.R.id.home) {
            if (switchedToPendingList) {
                super.onBackPressed();
            }
            if (getIntent().hasExtra(Constants.MOBILE_NUMBER) || getIntent().getBooleanExtra(LAUNCH_NEW_REQUEST, false)) {
                super.onBackPressed();
            } else {
                switchToReceivedPaymentRequestsFragment();
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void onBackPressed() {
        if (getIntent().hasExtra(Constants.MOBILE_NUMBER) || getIntent().getBooleanExtra(LAUNCH_NEW_REQUEST, false)) {
            finish();
        } else if (switchedToPendingList) {
            super.onBackPressed();
        } else {
            switchToReceivedPaymentRequestsFragment();
        }
    }

    public void switchToMakePaymentFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new MakePaymentFragment()).commit();
        mFabMakingPayment.setVisibility(View.GONE);
        switchedToPendingList = false;
    }

    public void switchToMakePaymentFragment(Bundle bundle) {
        if (bundle == null) {
            switchToMakePaymentFragment();
        } else {
            MakePaymentFragment makePaymentFragment = new MakePaymentFragment();
            makePaymentFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, makePaymentFragment).commit();
            mFabMakingPayment.setVisibility(View.GONE);
            switchedToPendingList = false;
        }
    }

    public void switchToReceivedPaymentRequestsFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new PaymentRequestsReceivedFragment()).commit();
        mFabMakingPayment.setVisibility(View.VISIBLE);
        switchedToPendingList = true;
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        mProgressDialog.dismiss();
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            if (this != null)
                Toaster.makeText(this, R.string.service_not_available, Toast.LENGTH_SHORT);
        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_USER_INFO)) {

            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                Bundle bundle = new Bundle();

                try {
                    Gson gson = new Gson();
                    mGetUserInfoResponse = gson.fromJson(result.getJsonString(), GetUserInfoResponse.class);
                    if (mGetUserInfoResponse.getAddressList().getOFFICE() != null) {
                        address = mGetUserInfoResponse.getAddressList().getOFFICE().get(0).getAddressLine1();
                        district = mGetUserInfoResponse.getAddressList().getOFFICE().get(0).getDistrict();
                        country = mGetUserInfoResponse.getAddressList().getOFFICE().get(0).getCountry();
                        bundle.putString(Constants.ADDRESS, address);
                        bundle.putString(Constants.COUNTRY, country);
                        bundle.putString(Constants.DISTRICT, district);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                switchToMakePaymentFragment(bundle);
            }
        }
    }

    @Override
    public Context setContext() {
        return PaymentActivity.this;
    }
}
