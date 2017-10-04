package bd.com.ipay.ipayskeleton.PaymentFragments.AddMoneyFragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;

import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.TransactionDetailsActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Aspect.ValidateAccess;
import bd.com.ipay.ipayskeleton.CustomView.CustomSwipeRefreshLayout;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory.TransactionHistory;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory.TransactionHistoryRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory.TransactionHistoryResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class AddMoneyHistoryFragment extends ProgressFragment implements HttpResponseListener {
    private HttpRequestPostAsyncTask mAddMoneyHistoryTask = null;
    private TransactionHistoryResponse mAddMoneyHistoryResponse;
    private AddMoneyHistoryAdapter mAddMoneyHistoryAdapter;
    private List<TransactionHistory> mAddMoneyHistories;
    private AddMoneyHistoryBroadcastReceiver addMoneyHistoryBroadcastReceiver;

    private RecyclerView mAddMoneyHistoryRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private CustomSwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mEmptyListTextView;

    private int historyPageCount = 0;
    private int mTotalItemCount =0;
    private int mPastVisiblesItems;
    private int mVisibleItem;
    private boolean hasNext = false;
    private boolean isLoading = false;
    private boolean clearListAfterLoading;
    private boolean mIsScrolled = false;
    private String mMobileNumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_money_history, container, false);
        mMobileNumber = ProfileInfoCacheManager.getMobileNumber();
        initializeViews(v);
        setupViewsAndActions();

        mSwipeRefreshLayout.setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    refreshAddMoneyHistory();
                }
            }
        });
        return v;
    }

    private void initializeViews(View v) {
        mEmptyListTextView = (TextView) v.findViewById(R.id.empty_list_text);
        mSwipeRefreshLayout = (CustomSwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        mAddMoneyHistoryRecyclerView = (RecyclerView) v.findViewById(R.id.list_transaction_history);
    }

    private void setupViewsAndActions() {
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        mAddMoneyHistoryAdapter = new AddMoneyHistoryAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mAddMoneyHistoryRecyclerView.setLayoutManager(mLayoutManager);
        mAddMoneyHistoryRecyclerView.setAdapter(mAddMoneyHistoryAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getAddMoneyHistory();
        implementScrollListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        addMoneyHistoryBroadcastReceiver = new AddMoneyHistoryBroadcastReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(addMoneyHistoryBroadcastReceiver,
                new IntentFilter(Constants.COMPLETED_TRANSACTION_HISTORY_UPDATE_BROADCAST));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.destroyDrawingCache();
            mSwipeRefreshLayout.clearAnimation();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getView() != null) {
            if (isVisibleToUser) {
                refreshAddMoneyHistory();
            }
        }
    }

    @Override
    public void onDestroyView() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(addMoneyHistoryBroadcastReceiver);
        super.onDestroyView();
    }

    private void refreshAddMoneyHistory() {
        historyPageCount = 0;
        clearListAfterLoading = true;
        getAddMoneyHistory();
    }

    private void getAddMoneyHistory() {
        if (mAddMoneyHistoryTask != null) {
            return;
        }
        TransactionHistoryRequest mAddMOneyHistoryRequest;
        mAddMOneyHistoryRequest = new TransactionHistoryRequest(Constants.TRANSACTION_HISTORY_ADD_MONEY, historyPageCount);

        Gson gson = new Gson();
        String json = gson.toJson(mAddMOneyHistoryRequest);
        mAddMoneyHistoryTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_TRANSACTION_HISTORY,
                Constants.BASE_URL_SM + Constants.URL_TRANSACTION_HISTORY_COMPLETED, json, getActivity());
        mAddMoneyHistoryTask.mHttpResponseListener = this;
        mAddMoneyHistoryTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void loadAddMoneyHistory(List<TransactionHistory> addMoneyHistories, boolean hasNext) {
        if (clearListAfterLoading || mAddMoneyHistories == null || mAddMoneyHistories.size() == 0) {
            mAddMoneyHistories = addMoneyHistories;
            clearListAfterLoading = false;
        } else {
            List<TransactionHistory> tempAddMoneyHistories;
            tempAddMoneyHistories = addMoneyHistories;
            mAddMoneyHistories.addAll(tempAddMoneyHistories);
        }

        this.hasNext = hasNext;
        if (mAddMoneyHistories != null && mAddMoneyHistories.size() > 0)
            mEmptyListTextView.setVisibility(View.GONE);
        else
            mEmptyListTextView.setVisibility(View.VISIBLE);

        if (isLoading)
            isLoading = false;
        mAddMoneyHistoryAdapter.notifyDataSetChanged();
        setContentShown(true);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mAddMoneyHistoryTask = null;
            if (getActivity() != null)
                Toaster.makeText(getActivity(), R.string.fetch_info_failed, Toast.LENGTH_LONG);
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_GET_TRANSACTION_HISTORY)) {

            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                try {
                    mAddMoneyHistoryResponse = gson.fromJson(result.getJsonString(), TransactionHistoryResponse.class);
                    loadAddMoneyHistory(mAddMoneyHistoryResponse.getTransactions(), mAddMoneyHistoryResponse.isHasNext());
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.transaction_history_get_failed, Toast.LENGTH_LONG).show();
                }

            } else {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.transaction_history_get_failed, Toast.LENGTH_LONG).show();
            }

            mSwipeRefreshLayout.setRefreshing(false);
            mAddMoneyHistoryTask = null;
            if (this.isAdded()) setContentShown(true);
        }
    }

    private class AddMoneyHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int FOOTER_VIEW = 1;

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView mTransactionDescriptionView;
            private final TextView mTimeView;
            private final TextView mReceiverView;
            private final TextView mAmountTextView;
            private final TextView mStatusDescriptionView;
            private final TextView mNetAmountView;
            private final ImageView mOtherImageView;
            private final ProfileImageView mProfileImageView;
            private final View mBalanceView;

            public ViewHolder(final View itemView) {
                super(itemView);

                mTransactionDescriptionView = (TextView) itemView.findViewById(R.id.activity_description);
                mTimeView = (TextView) itemView.findViewById(R.id.time);
                mReceiverView = (TextView) itemView.findViewById(R.id.receiver);
                mAmountTextView = (TextView) itemView.findViewById(R.id.amount);
                mNetAmountView = (TextView) itemView.findViewById(R.id.net_amount);
                mStatusDescriptionView = (TextView) itemView.findViewById(R.id.status_description);
                mProfileImageView = (ProfileImageView) itemView.findViewById(R.id.profile_picture);
                mOtherImageView = (ImageView) itemView.findViewById(R.id.other_image);
                mBalanceView = itemView.findViewById(R.id.balance_holder);
            }

            public void bindView(int pos) {
                final TransactionHistory transactionHistory = mAddMoneyHistories.get(pos);

                final String description = transactionHistory.getShortDescription(mMobileNumber);
                final String receiver = transactionHistory.getReceiver();
                final String responseTime = Utilities.formatDateWithTime(transactionHistory.getResponseTime());
                final String netAmountWithSign = transactionHistory.getNetAmountFormatted(transactionHistory.getAdditionalInfo().getUserMobileNumber());
                final Integer statusCode = transactionHistory.getStatusCode();
                final Double balance = transactionHistory.getBalance();
                final String imageUrl = transactionHistory.getAdditionalInfo().getUserProfilePic();
                final int bankIcon = transactionHistory.getAdditionalInfo().getBankIcon(getContext());
                final String bankCode = transactionHistory.getAdditionalInfo().getBankCode();
                final int serviceId = transactionHistory.getServiceID();
                final String status = transactionHistory.getStatus();

                mStatusDescriptionView.setText(status);

                if (balance != null) {
                    mAmountTextView.setText(Utilities.formatTakaWithComma(balance));
                    mBalanceView.setVisibility(View.VISIBLE);
                } else mBalanceView.setVisibility(View.GONE);

                if (statusCode == Constants.HTTP_RESPONSE_STATUS_OK) {
                    mStatusDescriptionView.setTextColor(getResources().getColor(R.color.bottle_green));
                } else {
                    mStatusDescriptionView.setTextColor(getResources().getColor(R.color.background_red));
                }

                mTransactionDescriptionView.setText(description);

                if (receiver != null && !receiver.equals("")) {
                    mReceiverView.setVisibility(View.VISIBLE);
                    mReceiverView.setText(receiver);
                } else mReceiverView.setVisibility(View.GONE);

                mNetAmountView.setText(netAmountWithSign);
                mTimeView.setText(responseTime);

                if (serviceId == Constants.TRANSACTION_HISTORY_ADD_MONEY
                        || serviceId == Constants.TRANSACTION_HISTORY_ADD_MONEY_REVERT) {
                    mProfileImageView.setVisibility(View.INVISIBLE);
                    mOtherImageView.setVisibility(View.VISIBLE);
                    if (bankCode != null) mOtherImageView.setImageResource(bankIcon);
                    else mOtherImageView.setImageResource(R.drawable.ic_tran_add);

                } else {
                    mOtherImageView.setVisibility(View.INVISIBLE);
                    mProfileImageView.setVisibility(View.VISIBLE);
                    mProfileImageView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + imageUrl, false);
                }

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    @ValidateAccess(ServiceIdConstants.TRANSACTION_DETAILS)
                    public void onClick(View v) {
                        if (!mSwipeRefreshLayout.isRefreshing()) {
                            Intent intent = new Intent(getActivity(), TransactionDetailsActivity.class);
                            intent.putExtra(Constants.TRANSACTION_DETAILS, transactionHistory);
                            startActivity(intent);
                        }
                    }
                });
            }
        }

        public class FooterViewHolder extends ViewHolder {
            private TextView mLoadMoreTextView;
            private ProgressBar mLoadMoreProgressBar;

            public FooterViewHolder(View itemView) {
                super(itemView);

                mLoadMoreTextView = (TextView) itemView.findViewById(R.id.load_more);
                mLoadMoreProgressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
            }

            public void bindViewFooter() {
                setItemVisibilityOfFooterView();

                mLoadMoreTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (hasNext) {
                            historyPageCount = historyPageCount + 1;
                            showLoadingInFooter();
                            notifyDataSetChanged();
                            getAddMoneyHistory();
                        }
                    }
                });
            }

            private void setItemVisibilityOfFooterView() {
                if (isLoading) {
                    mLoadMoreProgressBar.setVisibility(View.VISIBLE);
                    mLoadMoreTextView.setVisibility(View.GONE);
                } else {
                    mLoadMoreProgressBar.setVisibility(View.GONE);
                    mLoadMoreTextView.setVisibility(View.VISIBLE);

                    if (hasNext)
                        mLoadMoreTextView.setText(R.string.load_more);
                    else
                        mLoadMoreTextView.setText(R.string.no_more_results);
                }
            }

            private void showLoadingInFooter() {
                isLoading = true;
                notifyDataSetChanged();
            }
        }

        // Now define the view holder for Normal list item
        public class NormalViewHolder extends ViewHolder {
            public NormalViewHolder(View itemView) {
                super(itemView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Do whatever you want on clicking the normal items
                    }
                });
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case FOOTER_VIEW:
                    return new FooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_load_more_footer, parent, false));
                default:
                    return new NormalViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_transaction_history, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            try {
                if (holder instanceof NormalViewHolder) {
                    NormalViewHolder vh = (NormalViewHolder) holder;
                    vh.bindView(position);
                } else if (holder instanceof FooterViewHolder) {
                    FooterViewHolder vh = (FooterViewHolder) holder;
                    vh.bindViewFooter();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            // Return +1 as there's an extra footer (Load more...)
            if (mAddMoneyHistories != null && !mAddMoneyHistories.isEmpty())
                return mAddMoneyHistories.size() + 1;
            else return 0;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == mAddMoneyHistories.size()) {
                return FOOTER_VIEW;
            }

            return super.getItemViewType(position);
        }

    }

    private void implementScrollListener() {
        mAddMoneyHistoryRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    mIsScrolled = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                mVisibleItem = recyclerView.getChildCount();
                mTotalItemCount =mLayoutManager.getItemCount();
                mPastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
                if (mIsScrolled
                        && (mVisibleItem + mPastVisiblesItems) == mTotalItemCount && hasNext) {
                    isLoading = true;
                    mIsScrolled = false;
                    historyPageCount = historyPageCount + 1;
                    mAddMoneyHistoryAdapter.notifyDataSetChanged();
                    getAddMoneyHistory();
                }

            }

        });

    }

    private class AddMoneyHistoryBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshAddMoneyHistory();
        }
    }

}
