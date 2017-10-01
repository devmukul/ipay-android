package bd.com.ipay.ipayskeleton.PaymentFragments.ServicesFragments;

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
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class MobileTopupHistoryFragment extends ProgressFragment implements HttpResponseListener {
    private HttpRequestPostAsyncTask mTopupHistoryTask = null;
    private TransactionHistoryResponse mTopupHistoryResponse;
    private TopupHistoryAdapter mTopupHistoryAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<TransactionHistory> mTopupHistories;
    private TopupHistoryBroadcastReceiver topupHistoryBroadcastReceiver;

    private TextView mEmptyListTextView;
    private RecyclerView mTopupHistoryRecyclerView;
    private CustomSwipeRefreshLayout mSwipeRefreshLayout;

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
        View v = inflater.inflate(R.layout.fragment_topup_history, container, false);
        mMobileNumber = ProfileInfoCacheManager.getMobileNumber();
        initializeViews(v);
        setupViewsAndActions();

        mSwipeRefreshLayout.setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    refreshTopupHistory();
                }
            }
        });


        return v;
    }

    private void initializeViews(View v) {
        mEmptyListTextView = (TextView) v.findViewById(R.id.empty_list_text);
        mSwipeRefreshLayout = (CustomSwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        mTopupHistoryRecyclerView = (RecyclerView) v.findViewById(R.id.list_topup_history);
    }

    private void setupViewsAndActions() {
        setupRecyclerView();
        implementScrollListener();
    }

    private void setupRecyclerView() {
        mTopupHistoryAdapter = new TopupHistoryAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mTopupHistoryRecyclerView.setLayoutManager(mLayoutManager);
        mTopupHistoryRecyclerView.setAdapter(mTopupHistoryAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getTopUpHistory();
    }

    @Override
    public void onResume() {
        super.onResume();
        topupHistoryBroadcastReceiver = new TopupHistoryBroadcastReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(topupHistoryBroadcastReceiver,
                new IntentFilter(Constants.TOPUP_HISTORY_UPDATE_BROADCAST));
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
                refreshTopupHistory();
            }
        }
    }

    @Override
    public void onDestroyView() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(topupHistoryBroadcastReceiver);
        super.onDestroyView();
    }

    private void refreshTopupHistory() {
        historyPageCount = 0;
        clearListAfterLoading = true;
        getTopUpHistory();
    }

    private void getTopUpHistory() {
        if (mTopupHistoryTask != null) {
            return;
        }
        TransactionHistoryRequest mTransactionHistoryRequest;
        mTransactionHistoryRequest = new TransactionHistoryRequest(Constants.TRANSACTION_HISTORY_TOP_UP, historyPageCount);

        Gson gson = new Gson();
        String json = gson.toJson(mTransactionHistoryRequest);
        mTopupHistoryTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_TRANSACTION_HISTORY,
                Constants.BASE_URL_SM + Constants.URL_TRANSACTION_HISTORY_COMPLETED, json, getActivity());
        mTopupHistoryTask.mHttpResponseListener = this;
        mTopupHistoryTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void loadTransactionHistory(List<TransactionHistory> transactionHistories, boolean hasNext) {
        if (clearListAfterLoading || mTopupHistories == null || mTopupHistories.size() == 0) {
            mTopupHistories = transactionHistories;
            clearListAfterLoading = false;
        } else {
            List<TransactionHistory> tempTransactionHistories;
            tempTransactionHistories = transactionHistories;
            mTopupHistories.addAll(tempTransactionHistories);
        }

        this.hasNext = hasNext;
        if (mTopupHistories != null && mTopupHistories.size() > 0)
            mEmptyListTextView.setVisibility(View.GONE);
        else
            mEmptyListTextView.setVisibility(View.VISIBLE);

        if (isLoading)
            isLoading = false;
        mTopupHistoryAdapter.notifyDataSetChanged();
        setContentShown(true);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mTopupHistoryTask = null;
            if (getActivity() != null) {
                Toaster.makeText(getActivity(), R.string.fetch_info_failed, Toast.LENGTH_LONG);
            }
            return;
        }

        Gson gson = new Gson();
        if (result.getApiCommand().equals(Constants.COMMAND_GET_TRANSACTION_HISTORY)) {

            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                try {
                    mTopupHistoryResponse = gson.fromJson(result.getJsonString(), TransactionHistoryResponse.class);
                    loadTransactionHistory(mTopupHistoryResponse.getTransactions(), mTopupHistoryResponse.isHasNext());
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), R.string.transaction_history_get_failed, Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), R.string.transaction_history_get_failed, Toast.LENGTH_LONG).show();
                }
            }

            mSwipeRefreshLayout.setRefreshing(false);
            mTopupHistoryTask = null;
            if (this.isAdded()){
                setContentShown(true);
            }
        }
    }

    private class TopupHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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
                final TransactionHistory transactionHistory = mTopupHistories.get(pos);
                final String description = transactionHistory.getShortDescription(mMobileNumber);
                final String receiver = transactionHistory.getReceiver();
                final String responseTime = Utilities.formatDateWithTime(transactionHistory.getResponseTime());
                final String netAmountWithSign = transactionHistory.getNetAmountFormatted(transactionHistory.getAdditionalInfo().getUserMobileNumber());
                final Integer statusCode = transactionHistory.getStatusCode();
                final Double balance = transactionHistory.getBalance();
                final String imageUrl = transactionHistory.getAdditionalInfo().getUserProfilePic();
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

                if (serviceId == Constants.TRANSACTION_HISTORY_TOP_UP
                        || serviceId == Constants.TRANSACTION_HISTORY_TOP_UP_ROLLBACK) {
                    mProfileImageView.setVisibility(View.INVISIBLE);
                    mOtherImageView.setVisibility(View.VISIBLE);
                    if (ContactEngine.isValidNumber(receiver)) {
                        int mIcon = getOperatorIcon(receiver);
                        mOtherImageView.setImageResource(mIcon);
                    } else mOtherImageView.setImageResource(R.drawable.ic_top_up);

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
                            getTopUpHistory();
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
            if (mTopupHistories != null && !mTopupHistories.isEmpty())
                return mTopupHistories.size() + 1;
            else return 0;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == mTopupHistories.size()) {
                return FOOTER_VIEW;
            }

            return super.getItemViewType(position);
        }

        private int getOperatorIcon(String phoneNumber) {
            phoneNumber = ContactEngine.trimPrefix(phoneNumber);

            final String[] OPERATOR_PREFIXES = getResources().getStringArray(R.array.operator_prefix);
            int[] operator_array = new int[]{
                    R.drawable.ic_gp2,
                    R.drawable.ic_gp2,
                    R.drawable.ic_robi2,
                    R.drawable.ic_airtel2,
                    R.drawable.ic_banglalink2,
                    R.drawable.ic_teletalk2,
            };

            for (int i = 0; i < OPERATOR_PREFIXES.length; i++) {
                if (phoneNumber.startsWith(OPERATOR_PREFIXES[i])) {
                    return operator_array[i];
                }
            }
            return 0;
        }

    }

    private void implementScrollListener() {
        mTopupHistoryRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

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
                    mTopupHistoryAdapter.notifyDataSetChanged();
                    getTopUpHistory();
                }

            }

        });

    }

    private class TopupHistoryBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshTopupHistory();
        }
    }


}
