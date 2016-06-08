package bd.com.ipay.ipayskeleton.Utilities.CacheManager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory.TransactionHistoryClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory.TransactionHistoryRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory.TransactionHistoryResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class TransactionHistoryCacheManager implements HttpResponseListener {

    private SharedPreferences pref;

    private HttpRequestPostAsyncTask mTransactionHistoryTask = null;
    private TransactionHistoryResponse mTransactionHistoryResponse;
    
    private Context mContext;
    private List<TransactionHistoryClass> userTransactionHistoryClasses;

    private OnUpdateCacheListener mOnUpdateCacheListener;

    public TransactionHistoryCacheManager(Context context) {
        this(context, null);
    }
    
    public TransactionHistoryCacheManager(Context context, OnUpdateCacheListener onUpdateCacheListener) {
        mContext = context;
        mOnUpdateCacheListener = onUpdateCacheListener;

        pref = context.getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
    }
    
    public void updateCache() {

        DataHelper dataHelper = DataHelper.getInstance(mContext);
        long fromDate = dataHelper.getLastTransactionTime();
        dataHelper.closeDbOpenHelper();

        long toDate = Calendar.getInstance().getTimeInMillis();

        getTransactionHistory(fromDate, toDate);
    }

    public boolean isUpdateNeeded(String tag) {
        return pref.getBoolean(tag, true);
    }

    public void setUpdateNeeded(boolean isUpdateNeeded) {
        pref.edit().putBoolean(Constants.PUSH_NOTIFICATION_TAG_TRANSACTION_HISTORY, isUpdateNeeded).apply();
    }

    public void setHasNext(boolean hasNext) {
        pref.edit().putBoolean(Constants.PUSH_NOTIFICATION_TAG_TRANSACTION_HISTORY, hasNext).apply();
    }

    public boolean hasNext() {
        return pref.getBoolean(Constants.TRANSACTION_HISTORY_HAS_NEXT, false);
    }

    private void getTransactionHistory(long fromDate, long toDate) {
        if (mTransactionHistoryTask != null) {
            return;
        }

        TransactionHistoryRequest mTransactionHistoryRequest = new TransactionHistoryRequest(null, 0, fromDate, toDate);

        Gson gson = new Gson();
        String json = gson.toJson(mTransactionHistoryRequest);
        mTransactionHistoryTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_TRANSACTION_HISTORY,
                Constants.BASE_URL_SM + Constants.URL_TRANSACTION_HISTORY, json, mContext);
        mTransactionHistoryTask.mHttpResponseListener = this;
        mTransactionHistoryTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        if (result == null) {
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
                    dataHelper.closeDbOpenHelper();

                    setHasNext(mTransactionHistoryResponse.isHasNext());

                    if (mOnUpdateCacheListener != null)
                        mOnUpdateCacheListener.onUpdateCache(mTransactionHistoryResponse);

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
        void onUpdateCache(TransactionHistoryResponse transactionHistoryResponse);
    }
}
