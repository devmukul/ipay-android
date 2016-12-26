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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.GetSingleInvoiceRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.ItemList;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.PendingPaymentClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.BusinessRuleAndServiceCharge.BusinessRule.MandatoryBusinessRules;
import bd.com.ipay.ipayskeleton.PaymentFragments.InvoiceFragment.CreateInvoiceFragmentStepOne;
import bd.com.ipay.ipayskeleton.PaymentFragments.InvoiceFragment.CreateInvoiceFragmentStepTwo;
import bd.com.ipay.ipayskeleton.PaymentFragments.InvoiceFragment.SentPaymentRequestDetailsFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.InvoiceFragment.RequestPaymentFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.InvoiceFragment.SentPaymentRequestsFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments.ReceivedPaymentRequestDetailsFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class RequestPaymentActivity extends BaseActivity implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetSingleRequestPaymentDetailsTask = null;
    private PendingPaymentClass mGetSingleRequestPaymentDetailsResponse;

    private ProgressDialog mProgressDialog;

    public FloatingActionButton mFabNewRequestPayment;

    private boolean switchedFromTransactionHistory = false;

    private long REQUEST_PAYMENT_TAG = -1;
    private long requestPaymentID;

    public static final MandatoryBusinessRules mMandatoryBusinessRules = new MandatoryBusinessRules();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        mFabNewRequestPayment = (FloatingActionButton) findViewById(R.id.fab_new_request_payment);

        mProgressDialog = new ProgressDialog(this);

        mFabNewRequestPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToRequestPaymentFragment();
            }
        });

        requestPaymentID = getIntent().getLongExtra(Constants.REQUEST_ID, REQUEST_PAYMENT_TAG);
        if (requestPaymentID != REQUEST_PAYMENT_TAG) {
            switchedFromTransactionHistory = true;
            getSingleInvoiceDetails();
        } else
            switchToSentRequestPaymentsFragment();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        Utilities.hideKeyboard(this);
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else
            super.onBackPressed();

    }

    public void switchToSentRequestPaymentsFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SentPaymentRequestsFragment())
                .commit();
        mFabNewRequestPayment.setVisibility(View.VISIBLE);

    }

    private void switchToCreateInvoiceStepOneFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStackImmediate();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new CreateInvoiceFragmentStepOne())
                .addToBackStack(null)
                .commit();

        mFabNewRequestPayment.setVisibility(View.GONE);
    }

    public void switchToCreateInvoiceStepTwoFragment(Bundle bundle) {
        while (getSupportFragmentManager().getBackStackEntryCount() > 2) {
            getSupportFragmentManager().popBackStackImmediate();
        }

        CreateInvoiceFragmentStepTwo frag = new CreateInvoiceFragmentStepTwo();
        frag.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, frag)
                .addToBackStack(null)
                .commit();

        mFabNewRequestPayment.setVisibility(View.GONE);
    }

    private void switchToRequestPaymentFragment() {

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new RequestPaymentFragment())
                .addToBackStack(null)
                .commit();
        mFabNewRequestPayment.setVisibility(View.GONE);
    }

    public void switchToSentPaymentRequestDetailsFragment(Bundle bundle) {
        SentPaymentRequestDetailsFragment sentPaymentRequestDetailsFragment = new SentPaymentRequestDetailsFragment();
        if (bundle != null) {
            sentPaymentRequestDetailsFragment.setArguments(bundle);
        }

        if (switchedFromTransactionHistory)
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, sentPaymentRequestDetailsFragment)
                    .commit();
        else
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, sentPaymentRequestDetailsFragment)
                    .addToBackStack(null)
                    .commit();

        mFabNewRequestPayment.setVisibility(View.GONE);
    }

    public void switchToReceivedPaymentRequestDetailsFragment(Bundle bundle) {
        ReceivedPaymentRequestDetailsFragment receivedPaymentRequestDetailsFragment = new ReceivedPaymentRequestDetailsFragment();
        receivedPaymentRequestDetailsFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, receivedPaymentRequestDetailsFragment).commit();

        mFabNewRequestPayment.setVisibility(View.GONE);
    }

    private void getSingleInvoiceDetails() {
        if (mGetSingleRequestPaymentDetailsTask != null) {
            return;
        }
        mProgressDialog.setMessage(getString(R.string.loading));
        mProgressDialog.show();

        String mUri = new GetSingleInvoiceRequestBuilder(requestPaymentID).getGeneratedUri();
        mGetSingleRequestPaymentDetailsTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_SINGLE_INVOICE,
                mUri, this, this);

        mGetSingleRequestPaymentDetailsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    @Override
    public Context setContext() {
        return RequestPaymentActivity.this;
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR) {
            mGetSingleRequestPaymentDetailsTask = null;
            Toast.makeText(this, R.string.service_not_available, Toast.LENGTH_LONG).show();
            return;
        }

        Gson gson = new Gson();
        if (result.getApiCommand().equals(Constants.COMMAND_GET_SINGLE_INVOICE)) {
            try {
                mGetSingleRequestPaymentDetailsResponse = gson.fromJson(result.getJsonString(), PendingPaymentClass.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                    List<ItemList> mItemList = Arrays.asList(mGetSingleRequestPaymentDetailsResponse.getItemList());

                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.DESCRIPTION, mGetSingleRequestPaymentDetailsResponse.description);
                    bundle.putString(Constants.TIME, Utilities.formatDateWithTime(mGetSingleRequestPaymentDetailsResponse.getRequestTime()));
                    bundle.putLong(Constants.MONEY_REQUEST_ID, mGetSingleRequestPaymentDetailsResponse.getId());
                    bundle.putString(Constants.AMOUNT, mGetSingleRequestPaymentDetailsResponse.getAmount().toString());
                    bundle.putString(Constants.VAT, mGetSingleRequestPaymentDetailsResponse.getVat().toString());
                    bundle.putString(Constants.TITLE, mGetSingleRequestPaymentDetailsResponse.getTitle());
                    bundle.putInt(Constants.STATUS, Constants.INVOICE_STATUS_PROCESSING);

                    if (mItemList != null)
                        bundle.putParcelableArrayList(Constants.INVOICE_ITEM_NAME_TAG, new ArrayList<>(mItemList));
                    else
                        bundle.putParcelableArrayList(Constants.INVOICE_ITEM_NAME_TAG, null);

                    if (ProfileInfoCacheManager.getMobileNumber().equals(mGetSingleRequestPaymentDetailsResponse.getReceiverProfile().getUserMobileNumber())) {
                        bundle.putString(Constants.MOBILE_NUMBER, mGetSingleRequestPaymentDetailsResponse.getOriginatorProfile().getUserMobileNumber());
                        bundle.putString(Constants.NAME, mGetSingleRequestPaymentDetailsResponse.getOriginatorProfile().getUserName());
                        bundle.putString(Constants.PHOTO_URI, Constants.BASE_URL_FTP_SERVER + mGetSingleRequestPaymentDetailsResponse.getOriginatorProfile().getUserProfilePicture());
                        switchToReceivedPaymentRequestDetailsFragment(bundle);
                    } else {
                        bundle.putString(Constants.MOBILE_NUMBER, mGetSingleRequestPaymentDetailsResponse.getReceiverProfile().getUserMobileNumber());
                        bundle.putString(Constants.NAME, mGetSingleRequestPaymentDetailsResponse.getReceiverProfile().getUserName());
                        bundle.putString(Constants.PHOTO_URI, Constants.BASE_URL_FTP_SERVER + mGetSingleRequestPaymentDetailsResponse.getReceiverProfile().getUserProfilePicture());
                        switchToSentPaymentRequestDetailsFragment(bundle);
                    }

                } else {
                    Toast.makeText(this, R.string.profile_info_get_failed, Toast.LENGTH_SHORT).show();
                    finish();
                }

            } catch (Exception e) {
                e.printStackTrace();

                Toast.makeText(this, R.string.profile_info_get_failed, Toast.LENGTH_SHORT).show();
                finish();
            }

            mGetSingleRequestPaymentDetailsTask = null;
            mProgressDialog.dismiss();
        }
    }
}





