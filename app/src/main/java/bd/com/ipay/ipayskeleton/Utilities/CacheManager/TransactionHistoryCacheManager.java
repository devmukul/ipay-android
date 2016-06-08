package bd.com.ipay.ipayskeleton.Utilities.CacheManager;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory.TransactionHistoryClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory.TransactionHistoryRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory.TransactionHistoryResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class TransactionHistoryCacheManager implements HttpResponseListener {

    private HttpRequestPostAsyncTask mTransactionHistoryTask = null;
    private TransactionHistoryResponse mTransactionHistoryResponse;
    
    private Context mContext;
    private List<TransactionHistoryClass> userTransactionHistoryClasses;
    
    public TransactionHistoryCacheManager(Context context) {
        mContext = context;
    }
    
    public void updateCache() {
        
    }
    
    public interface OnUpdateCacheListener {
        public void onUpdateCache();
    }

//    private void getTransactionHistory() {
//        if (mTransactionHistoryTask != null) {
//            return;
//        }
//
//        TransactionHistoryRequest mTransactionHistoryRequest;
//        if (fromDate != null && toDate != null) {
//            mTransactionHistoryRequest = new TransactionHistoryRequest(
//                    type, historyPageCount, fromDate.getTimeInMillis(), toDate.getTimeInMillis());
//        } else {
//            mTransactionHistoryRequest = new TransactionHistoryRequest(type, historyPageCount);
//        }
//
//        Gson gson = new Gson();
//        String json = gson.toJson(mTransactionHistoryRequest);
//        mTransactionHistoryTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_TRANSACTION_HISTORY,
//                Constants.BASE_URL_SM + Constants.URL_TRANSACTION_HISTORY, json, mContext);
//        mTransactionHistoryTask.mHttpResponseListener = this;
//        mTransactionHistoryTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//    }


    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
//
//        if (result == null) {
//            mTransactionHistoryTask = null;
//            if (mContext != null)
//                Toast.makeText(mContext, R.string.fetch_info_failed, Toast.LENGTH_LONG).show();
//            return;
//        }
//
//
//        Gson gson = new Gson();
//
//        if (result.getApiCommand().equals(Constants.COMMAND_GET_TRANSACTION_HISTORY)) {
//
//            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
//
//                try {
//                    mTransactionHistoryResponse = gson.fromJson(result.getJsonString(), TransactionHistoryResponse.class);
//
//                    if (userT123456ransactionHistoryClasses == null || userTransactionHistoryClasses.size() == 0) {
//                        userTransactionHistoryClasses = mTransactionHistoryResponse.getTransactions();
//                    } else {
//                        List<TransactionHistoryClass> tempTransactionHistoryClasses;
//                        tempTransactionHistoryClasses = mTransactionHistoryResponse.getTransactions();
//                        userTransactionHistoryClasses.addAll(tempTransactionHistoryClasses);
//                    }
//
//                    hasNext = mTransactionHistoryResponse.isHasNext();
//                    if (userTransactionHistoryClasses != null && userTransactionHistoryClasses.size() > 0)
//                        mEmptyListTextView.setVisibility(View.GONE);
//                    else mEmptyListTextView.setVisibility(View.VISIBLE);
//
//                    mTransactionHistoryAdapter.notifyDataSetChanged();
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    if (mContext != null)
//                        Toast.makeText(mContext, R.string.transaction_history_get_failed, Toast.LENGTH_LONG).show();
//                }
//
//            } else {
//                if (mContext != null)
//                    Toast.makeText(mContext, R.string.transaction_history_get_failed, Toast.LENGTH_LONG).show();
//            }
//
//            mSwipeRefreshLayout.setRefreshing(false);
//            mTransactionHistoryTask = null;
//        }
    }
}
