package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
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
import bd.com.ipay.ipayskeleton.Model.Security.GetSecurityQuestionRequestBuilder;
import bd.com.ipay.ipayskeleton.PaymentFragments.InvoiceFragment.CreateInvoiceFragmentStepOne;
import bd.com.ipay.ipayskeleton.PaymentFragments.InvoiceFragment.CreateInvoiceFragmentStepTwo;
import bd.com.ipay.ipayskeleton.PaymentFragments.InvoiceFragment.InvoiceDetailsFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.InvoiceFragment.RequestPaymentFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.InvoiceFragment.SentInvoicesFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments.InvoiceHistoryFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class InvoiceActivity extends BaseActivity implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetSingleInvoiceTask = null;
    private PendingPaymentClass mGetSingleInvoiceResponse;

    private ProgressDialog mProgressDialog;

    public FloatingActionButton mFabCreateInvoice;

    private boolean switchedFromTransactionHistory = false;

    private long INVOICE_TAG = -1;
    private long invoiceID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        mFabCreateInvoice = (FloatingActionButton) findViewById(R.id.fab_create_invoice);

        mProgressDialog = new ProgressDialog(this);

        mFabCreateInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToRequestPaymentFragment();
                //switchToCreateInvoiceFragment();
            }
        });

        invoiceID = getIntent().getLongExtra(Constants.REQUEST_ID, INVOICE_TAG);
        if (invoiceID != INVOICE_TAG) {
            switchedFromTransactionHistory = true;
            getSingleInvoiceDetails();
        } else
            switchToInvoicesSentFragment();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {

        Utilities.hideKeyboard(this);
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else
            super.onBackPressed();

    }

    public void switchToInvoicesSentFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SentInvoicesFragment())
                .commit();
        mFabCreateInvoice.setVisibility(View.VISIBLE);

    }

    private void switchToCreateInvoiceStepOneFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStackImmediate();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new CreateInvoiceFragmentStepOne())
                .addToBackStack(null)
                .commit();

        mFabCreateInvoice.setVisibility(View.GONE);
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

        mFabCreateInvoice.setVisibility(View.GONE);
    }

    private void switchToRequestPaymentFragment() {

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new RequestPaymentFragment())
                .addToBackStack(null)
                .commit();
        mFabCreateInvoice.setVisibility(View.GONE);
    }

    public void switchToInvoiceDetailsFragment(Bundle bundle) {
        InvoiceDetailsFragment invoiceDetailsFragment = new InvoiceDetailsFragment();
        if (bundle != null) {
            invoiceDetailsFragment.setArguments(bundle);
        }

        if (switchedFromTransactionHistory)
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, invoiceDetailsFragment)
                    .commit();
        else
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, invoiceDetailsFragment)
                    .addToBackStack(null)
                    .commit();

        mFabCreateInvoice.setVisibility(View.GONE);
    }

    public void switchToInvoiceHistoryFragment(Bundle bundle) {
        InvoiceHistoryFragment invoiceHistoryFragment = new InvoiceHistoryFragment();
        invoiceHistoryFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, invoiceHistoryFragment).commit();

        mFabCreateInvoice.setVisibility(View.GONE);
    }

    private void getSingleInvoiceDetails() {
        if (mGetSingleInvoiceTask != null) {
            return;
        }
        mProgressDialog.setMessage(getString(R.string.loading));
        mProgressDialog.show();

        String mUri = new GetSingleInvoiceRequestBuilder(invoiceID).getGeneratedUri();
        mGetSingleInvoiceTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_SINGLE_INVOICE,
                mUri, this, this);

        mGetSingleInvoiceTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    @Override
    public Context setContext() {
        return InvoiceActivity.this;
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR) {
            mGetSingleInvoiceTask = null;
            Toast.makeText(this, R.string.service_not_available, Toast.LENGTH_LONG).show();
            return;
        }

        Gson gson = new Gson();
        if (result.getApiCommand().equals(Constants.COMMAND_GET_SINGLE_INVOICE)) {
            try {
                mGetSingleInvoiceResponse = gson.fromJson(result.getJsonString(), PendingPaymentClass.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                    List<ItemList> mItemList = Arrays.asList(mGetSingleInvoiceResponse.getItemList());

                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.DESCRIPTION, mGetSingleInvoiceResponse.description);
                    bundle.putString(Constants.TIME, Utilities.getDateFormat(mGetSingleInvoiceResponse.getRequestTime()));
                    bundle.putLong(Constants.MONEY_REQUEST_ID, mGetSingleInvoiceResponse.getId());
                    bundle.putString(Constants.AMOUNT, mGetSingleInvoiceResponse.getAmount().toString());
                    bundle.putString(Constants.VAT, mGetSingleInvoiceResponse.getVat().toString());
                    bundle.putParcelableArrayList(Constants.INVOICE_ITEM_NAME_TAG, new ArrayList<>(mItemList));
                    bundle.putString(Constants.TITLE, mGetSingleInvoiceResponse.getTitle());
                    bundle.putInt(Constants.STATUS, Constants.INVOICE_STATUS_PROCESSING);

                    if (ProfileInfoCacheManager.getMobileNumber().equals(mGetSingleInvoiceResponse.getReceiverProfile().getUserMobileNumber())) {
                        bundle.putString(Constants.MOBILE_NUMBER, mGetSingleInvoiceResponse.getOriginatorProfile().getUserMobileNumber());
                        bundle.putString(Constants.NAME, mGetSingleInvoiceResponse.getOriginatorProfile().getUserName());
                        bundle.putString(Constants.PHOTO_URI, Constants.BASE_URL_FTP_SERVER + mGetSingleInvoiceResponse.getOriginatorProfile().getUserProfilePicture());
                        switchToInvoiceHistoryFragment(bundle);
                    } else {
                        bundle.putString(Constants.MOBILE_NUMBER, mGetSingleInvoiceResponse.getReceiverProfile().getUserMobileNumber());
                        bundle.putString(Constants.NAME, mGetSingleInvoiceResponse.getReceiverProfile().getUserName());
                        bundle.putString(Constants.PHOTO_URI, Constants.BASE_URL_FTP_SERVER + mGetSingleInvoiceResponse.getReceiverProfile().getUserProfilePicture());
                        switchToInvoiceDetailsFragment(bundle);
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

            mGetSingleInvoiceTask = null;
            mProgressDialog.dismiss();
        }
    }
}





