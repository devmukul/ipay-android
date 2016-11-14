package bd.com.ipay.ipayskeleton.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;

import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.HomeFragments.TransactionHistoryFragments.TransactionDetailsFragment;
import bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory.SingleTransactionHistoryRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory.SingleTransactionHistoryResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory.TransactionHistoryClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory.TransactionHistoryRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory.TransactionHistoryResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class TransactionDetailsActivity extends BaseActivity {

    private HttpRequestPostAsyncTask mTransactionHistoryTask = null;
    private SingleTransactionHistoryResponse mTransactionHistoryResponse;

    private TransactionHistoryClass transactionHistoryClass;
    private int status;
    private String requestID = null;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProgressDialog = new ProgressDialog(this);

        setContentView(R.layout.activity_transaction_details);
        status = getIntent().getIntExtra(Constants.STATUS, -1);

        if (status == Constants.REQUEST_STATUS_ALL) {
            requestID = getIntent().getStringExtra(Constants.MONEY_REQUEST_ID);
            getTransactionHistory();
        } else {
            transactionHistoryClass = getIntent().getParcelableExtra(Constants.TRANSACTION_DETAILS);
            TransactionDetailsFragment transactionDetailsFragment = new TransactionDetailsFragment();

            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.TRANSACTION_DETAILS, transactionHistoryClass);
            transactionDetailsFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, transactionDetailsFragment).commit();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void getTransactionHistory() {
        if (mTransactionHistoryTask != null) {
            return;
        }
        mProgressDialog.setMessage(getString(R.string.loading));
        mProgressDialog.show();

        SingleTransactionHistoryRequest mTransactionHistoryRequest;
        mTransactionHistoryRequest = new SingleTransactionHistoryRequest(requestID);

        Gson gson = new Gson();
        String json = gson.toJson(mTransactionHistoryRequest);
        mTransactionHistoryTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_TRANSACTION_HISTORY,
                Constants.BASE_URL_SM + Constants.URL_TRANSACTION_HISTORY_SINGLE, json, this.getApplicationContext());
        mTransactionHistoryTask.mHttpResponseListener = this;
        mTransactionHistoryTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mTransactionHistoryTask = null;
            if (this != null)
                Toast.makeText(this, R.string.fetch_info_failed, Toast.LENGTH_LONG).show();
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_GET_TRANSACTION_HISTORY)) {

            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                try {
                    mTransactionHistoryResponse = gson.fromJson(result.getJsonString(), SingleTransactionHistoryResponse.class);
                    transactionHistoryClass = mTransactionHistoryResponse.getTransaction();
                    if (transactionHistoryClass != null) {
                        TransactionDetailsFragment transactionDetailsFragment = new TransactionDetailsFragment();

                        Bundle bundle = new Bundle();
                        bundle.putParcelable(Constants.TRANSACTION_DETAILS, transactionHistoryClass);
                        transactionDetailsFragment.setArguments(bundle);

                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, transactionDetailsFragment).commit();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    if (this != null)
                        Toast.makeText(this, R.string.transaction_history_get_failed, Toast.LENGTH_LONG).show();
                }

            } else {
                if (this != null)
                    Toast.makeText(this, R.string.transaction_history_get_failed, Toast.LENGTH_LONG).show();
            }

            mProgressDialog.dismiss();
            mTransactionHistoryTask = null;
        }
    }

    @Override
    protected Context setContext() {
        return TransactionDetailsActivity.this;
    }
}
