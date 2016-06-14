package bd.com.ipay.ipayskeleton.Utilities.CacheManager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DBConstants;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory.TransactionHistoryClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory.TransactionHistoryRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory.TransactionHistoryResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class TransactionHistoryCacheManager implements HttpResponseListener {

    public static final int TRANSACTION_HISTORY_PER_PAGE_ENTRY = 10;

    private SharedPreferences pref;

    private HttpRequestPostAsyncTask mTransactionHistoryTask = null;
    private TransactionHistoryResponse mTransactionHistoryResponse;
    
    private Context mContext;
    private List<TransactionHistoryClass> userTransactionHistoryClasses;

    private OnUpdateCacheListener mOnUpdateCacheListener;

    public TransactionHistoryCacheManager(Context context) {
        mContext = context;

        pref = context.getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
    }
    
    public void updateCache(OnUpdateCacheListener onUpdateCacheListener) {

        mOnUpdateCacheListener = onUpdateCacheListener;

        DataHelper dataHelper = DataHelper.getInstance(mContext);
        long fromDate = dataHelper.getLastTransactionTime() + 1;

        long toDate = Calendar.getInstance().getTimeInMillis();

        getTransactionHistory(fromDate, toDate);
    }

    public boolean isUpdateNeeded() {
        return pref.getBoolean(Constants.PUSH_NOTIFICATION_TAG_TRANSACTION_HISTORY, true);
    }

    public void setUpdateNeeded(boolean isUpdateNeeded) {
        pref.edit().putBoolean(Constants.PUSH_NOTIFICATION_TAG_TRANSACTION_HISTORY, isUpdateNeeded).apply();
    }

    public void loadTransactions() {
        DataHelper dataHelper = DataHelper.getInstance(mContext);
        userTransactionHistoryClasses = dataHelper.getAllTransactionHistory();
        Log.d("Number of transactions", userTransactionHistoryClasses.size() + "");
    }

    public List<TransactionHistoryClass> getTransactions() {
        if (userTransactionHistoryClasses == null)
            throw new RuntimeException("Call loadTransactions() first");

        if (userTransactionHistoryClasses.size() >= DBConstants.MAXIMUM_NUMBER_OF_ENTRIES_IN_TRANSACTION_HISTORY)
            return userTransactionHistoryClasses.subList(0, userTransactionHistoryClasses.size() - 1);
        else
            return userTransactionHistoryClasses;
    }

    public boolean hasNext() {
        if (userTransactionHistoryClasses == null)
            throw new RuntimeException("Call loadTransactions() first");

        if (userTransactionHistoryClasses.size() >= DBConstants.MAXIMUM_NUMBER_OF_ENTRIES_IN_TRANSACTION_HISTORY)
            return true;
        else
            return false;
    }

    public int getPageCount() {
        if (userTransactionHistoryClasses == null)
            throw new RuntimeException("Call loadTransactions() first");

        return (userTransactionHistoryClasses.size() / TRANSACTION_HISTORY_PER_PAGE_ENTRY) - 1;
    }

    private void getTransactionHistory(long fromDate, long toDate) {
        if (mTransactionHistoryTask != null) {
            return;
        }

        TransactionHistoryRequest mTransactionHistoryRequest = new TransactionHistoryRequest(null, 0,
                fromDate, toDate, DBConstants.MAXIMUM_NUMBER_OF_ENTRIES_IN_TRANSACTION_HISTORY);

        Gson gson = new Gson();
        String json = gson.toJson(mTransactionHistoryRequest);
        mTransactionHistoryTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_TRANSACTION_HISTORY,
                Constants.BASE_URL_SM + Constants.URL_TRANSACTION_HISTORY, json, mContext);
        mTransactionHistoryTask.mHttpResponseListener = this;
        mTransactionHistoryTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
					|| result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mTransactionHistoryTask = null;
            if (mContext != null)
                Toast.makeText(mContext, R.string.fetch_info_failed, Toast.LENGTH_LONG).show();
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_GET_TRANSACTION_HISTORY)) {

            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                try {
                    mTransactionHistoryResponse = gson.fromJson(result.getJsonString(), TransactionHistoryResponse.class);

                    DataHelper dataHelper = DataHelper.getInstance(mContext);
                    dataHelper.createTransactionHistories(mTransactionHistoryResponse.getTransactions());

                    if (mOnUpdateCacheListener != null)
                        mOnUpdateCacheListener.onUpdateCache();

                    setUpdateNeeded(false);

                } catch (Exception e) {
                    e.printStackTrace();
                    if (mContext != null)
                        Toast.makeText(mContext, R.string.transaction_history_get_failed, Toast.LENGTH_LONG).show();
                }

            } else {
                if (mContext != null)
                    Toast.makeText(mContext, R.string.transaction_history_get_failed, Toast.LENGTH_LONG).show();
            }

            mTransactionHistoryTask = null;
        }
    }

    public interface OnUpdateCacheListener {
        void onUpdateCache();
    }
}
