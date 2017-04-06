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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.Api.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.MandatoryBusinessRules;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment.GetSinglePaymentRequestDetailRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment.InvoiceItem;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment.PendingPaymentClass;
import bd.com.ipay.ipayskeleton.PaymentFragments.RequestPaymentFragments.RequestPaymentFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.RequestPaymentFragments.PaymentRequestSentDetailsFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.RequestPaymentFragments.PaymentRequestsSentFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments.PaymentRequestReceivedDetailsFragment;
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

    private String mTime;
    private String mDescription;
    private BigDecimal mAmount;
    private BigDecimal mVat;
    private long mRequestID;
    private String mTransactionID;
    private List<InvoiceItem> mInvoiceItemList;

    private String mName;
    private String mMobileNumber;
    private String mPhotoUri;
    private String mTitle;

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
            getSinglePaymentRequestDetails();
        } else
            switchToSentPaymentRequestsFragment();

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

    public void switchToSentPaymentRequestsFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new PaymentRequestsSentFragment())
                .commit();
        mFabNewRequestPayment.setVisibility(View.VISIBLE);

    }

    private void switchToRequestPaymentFragment() {

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new RequestPaymentFragment())
                .addToBackStack(null)
                .commit();
        mFabNewRequestPayment.setVisibility(View.GONE);
    }

    public void switchToSentPaymentRequestDetailsFragment(Bundle bundle) {
        PaymentRequestSentDetailsFragment paymentRequestSentDetailsFragment = new PaymentRequestSentDetailsFragment();
        if (bundle != null) {
            paymentRequestSentDetailsFragment.setArguments(bundle);
        }

        if (switchedFromTransactionHistory)
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, paymentRequestSentDetailsFragment)
                    .commit();
        else
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, paymentRequestSentDetailsFragment)
                    .addToBackStack(null)
                    .commit();

        mFabNewRequestPayment.setVisibility(View.GONE);
    }

    public void switchToReceivedPaymentRequestDetailsFragment(Bundle bundle) {
        PaymentRequestReceivedDetailsFragment paymentRequestReceivedDetailsFragment = new PaymentRequestReceivedDetailsFragment();
        paymentRequestReceivedDetailsFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, paymentRequestReceivedDetailsFragment).commit();

        mFabNewRequestPayment.setVisibility(View.GONE);
    }

    private void getSinglePaymentRequestDetails() {
        if (mGetSingleRequestPaymentDetailsTask != null) {
            return;
        }
        mProgressDialog.setMessage(getString(R.string.loading));
        mProgressDialog.show();

        String mUri = new GetSinglePaymentRequestDetailRequestBuilder(requestPaymentID).getGeneratedUri();
        mGetSingleRequestPaymentDetailsTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_SINGLE_INVOICE,
                mUri, this, this);

        mGetSingleRequestPaymentDetailsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
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

                    mInvoiceItemList = Arrays.asList(mGetSingleRequestPaymentDetailsResponse.getItemList());
                    mDescription = mGetSingleRequestPaymentDetailsResponse.getDescriptionOfRequest();
                    mTime = Utilities.formatDateWithTime(mGetSingleRequestPaymentDetailsResponse.getRequestTime());
                    mRequestID = mGetSingleRequestPaymentDetailsResponse.getId();
                    mTransactionID = mGetSingleRequestPaymentDetailsResponse.getTransactionID();
                    mAmount = mGetSingleRequestPaymentDetailsResponse.getAmount();
                    mVat = mGetSingleRequestPaymentDetailsResponse.getVat();
                    mTitle = mGetSingleRequestPaymentDetailsResponse.getTitle();

                    if (ProfileInfoCacheManager.getMobileNumber().equals(mGetSingleRequestPaymentDetailsResponse.getReceiverProfile().getUserMobileNumber())) {
                        mMobileNumber = mGetSingleRequestPaymentDetailsResponse.getOriginatorProfile().getUserMobileNumber();
                        mName = mGetSingleRequestPaymentDetailsResponse.getOriginatorProfile().getUserName();
                        mPhotoUri = mGetSingleRequestPaymentDetailsResponse.getOriginatorProfile().getUserProfilePicture();

                        launchReceivedPaymentRequestDetailsReviewFragment();
                    } else {
                        mMobileNumber = mGetSingleRequestPaymentDetailsResponse.getReceiverProfile().getUserMobileNumber();
                        mName = mGetSingleRequestPaymentDetailsResponse.getReceiverProfile().getUserName();
                        mPhotoUri = mGetSingleRequestPaymentDetailsResponse.getReceiverProfile().getUserProfilePicture();

                        launchSentPaymentRequestDetailsReviewFragment();
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

    private void launchReceivedPaymentRequestDetailsReviewFragment() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DESCRIPTION, mDescription);
        bundle.putString(Constants.TIME, mTime);
        bundle.putLong(Constants.MONEY_REQUEST_ID, mRequestID);
        bundle.putString(Constants.AMOUNT, mAmount.toString());
        bundle.putString(Constants.VAT, mVat.toString());
        bundle.putString(Constants.TITLE, mTitle);
        bundle.putInt(Constants.STATUS, Constants.INVOICE_STATUS_PROCESSING);

        if (mInvoiceItemList != null)
            bundle.putParcelableArrayList(Constants.INVOICE_ITEM_NAME_TAG, new ArrayList<>(mInvoiceItemList));
        else
            bundle.putParcelableArrayList(Constants.INVOICE_ITEM_NAME_TAG, null);
        bundle.putString(Constants.MOBILE_NUMBER, mMobileNumber);
        bundle.putString(Constants.NAME, mName);
        bundle.putString(Constants.PHOTO_URI, Constants.BASE_URL_FTP_SERVER + mPhotoUri);

        switchToReceivedPaymentRequestDetailsFragment(bundle);
    }

    private void launchSentPaymentRequestDetailsReviewFragment() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DESCRIPTION, mDescription);
        bundle.putString(Constants.TIME, mTime);
        bundle.putLong(Constants.MONEY_REQUEST_ID, mRequestID);
        bundle.putString(Constants.AMOUNT, mAmount.toString());
        bundle.putString(Constants.VAT, mVat.toString());
        bundle.putString(Constants.TITLE, mTitle);
        bundle.putInt(Constants.STATUS, Constants.INVOICE_STATUS_PROCESSING);
        bundle.putString(Constants.TRANSACTION_ID, mTransactionID);

        if (mInvoiceItemList != null)
            bundle.putParcelableArrayList(Constants.INVOICE_ITEM_NAME_TAG, new ArrayList<>(mInvoiceItemList));
        else
            bundle.putParcelableArrayList(Constants.INVOICE_ITEM_NAME_TAG, null);
        bundle.putString(Constants.MOBILE_NUMBER, mMobileNumber);
        bundle.putString(Constants.NAME, mName);
        bundle.putString(Constants.PHOTO_URI, Constants.BASE_URL_FTP_SERVER + mPhotoUri);

        switchToSentPaymentRequestDetailsFragment(bundle);
    }

    @Override
    public Context setContext() {
        return RequestPaymentActivity.this;
    }
}





