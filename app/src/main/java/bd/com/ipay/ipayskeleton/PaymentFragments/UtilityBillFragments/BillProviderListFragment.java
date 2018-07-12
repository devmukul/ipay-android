package bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.TransactionDetailsActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.UtilityBillPaymentActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Aspect.ValidateAccess;
import bd.com.ipay.ipayskeleton.CustomView.CustomSwipeRefreshLayout;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.MerchantBranchSelectorDialog;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory.TransactionHistory;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory.TransactionHistoryRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory.TransactionHistoryResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill.Provider;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class BillProviderListFragment extends ProgressFragment implements HttpResponseListener {
    private HttpRequestGetAsyncTask mTransactionHistoryTask = null;

    private RecyclerView mTransactionHistoryRecyclerView;
    private TransactionHistoryAdapter mTransactionHistoryAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<TransactionHistory> userTransactionHistories;
    private CustomSwipeRefreshLayout mSwipeRefreshLayout;

    private TextView mEmptyListTextView;
    private boolean clearListAfterLoading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bill_provider_list, container, false);
        getActivity().setTitle(R.string.transaction_history);

        initializeViews(v);
        setupViewsAndActions();

        mSwipeRefreshLayout.setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isConnectionAvailable(getActivity()) && mTransactionHistoryTask == null) {
                    refreshTransactionHistory();
                } else {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getTransactionHistory();
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
    public void onDestroyView() {
        super.onDestroyView();
    }



    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {

        if (HttpErrorHandler.isErrorFound(result, getContext(), null)) {
            mTransactionHistoryTask = null;
            setContentShown(true);
            return;
        }

        Gson gson = new Gson();
        if (result.getApiCommand().equals(Constants.COMMAND_GET_TRANSACTION_HISTORY)) {
            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                try {
                    System.out.println("Test Response "+ result.getJsonString());
//                    TransactionHistoryResponse mTransactionHistoryResponse = gson.fromJson(result.getJsonString(), TransactionHistoryResponse.class);
//                    loadTransactionHistory(mTransactionHistoryResponse.getTransactions(), mTransactionHistoryResponse.isHasNext());
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
            mTransactionHistoryTask = null;
            if (this.isAdded()) setContentShown(true);
        }
    }

    private void initializeViews(View v) {
        mEmptyListTextView = (TextView) v.findViewById(R.id.empty_list_text);
        mSwipeRefreshLayout = (CustomSwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        mTransactionHistoryRecyclerView = (RecyclerView) v.findViewById(R.id.list_transaction_history);
    }

    private void setupViewsAndActions() {
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        mTransactionHistoryAdapter = new TransactionHistoryAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mTransactionHistoryRecyclerView.setLayoutManager(mLayoutManager);
        mTransactionHistoryRecyclerView.setAdapter(mTransactionHistoryAdapter);
    }

    private void refreshTransactionHistory() {
        clearListAfterLoading = true;
        getTransactionHistory();
    }

    private void getTransactionHistory() {
        if (mTransactionHistoryTask != null) {
            return;
        }

        mTransactionHistoryTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_TRANSACTION_HISTORY,
                Constants.BASE_URL_UTILITY+Constants.URL_GET_PROVIDER, getActivity(), false);
        mTransactionHistoryTask.mHttpResponseListener = this;
        mTransactionHistoryTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class TransactionHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int FOOTER_VIEW = 1;

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView mTransactionDescriptionView;
            private final TextView mTimeView;
            private final TextView mReceiverView;
            private final TextView mBalanceTextView;
            private TextView mNetAmountView;
            private final ImageView mOtherImageView;
            private final ProfileImageView mProfileImageView;
            private ImageView mStatusIconView;

            public ViewHolder(final View itemView) {
                super(itemView);

                mTransactionDescriptionView = (TextView) itemView.findViewById(R.id.activity_description);
                mTimeView = (TextView) itemView.findViewById(R.id.time);
                mReceiverView = (TextView) itemView.findViewById(R.id.receiver);
                mBalanceTextView = (TextView) itemView.findViewById(R.id.amount);
                mNetAmountView = (TextView) itemView.findViewById(R.id.net_amount);
                mStatusIconView = (ImageView) itemView.findViewById(R.id.status_description_icon);
                mProfileImageView = (ProfileImageView) itemView.findViewById(R.id.profile_picture);
                mOtherImageView = (ImageView) itemView.findViewById(R.id.other_image);
            }

            public void bindView(int pos) {
                final TransactionHistory transactionHistory = userTransactionHistories.get(pos);

                final String description = transactionHistory.getShortDescription();
                final String receiver = transactionHistory.getReceiver();
                String responseTime = Utilities.formatDayMonthYear(transactionHistory.getTime());
                final String netAmountWithSign = String.valueOf(Utilities.formatTakaFromString(transactionHistory.getNetAmountFormatted()));
                final Integer statusCode = transactionHistory.getStatusCode();
                final Double balance = transactionHistory.getAccountBalance();

                if (balance != null) {
                    mBalanceTextView.setText(Utilities.formatTakaWithComma(balance));
                }

                mNetAmountView.setText(netAmountWithSign);

                switch (statusCode) {
                    case Constants.TRANSACTION_STATUS_ACCEPTED: {
                        mNetAmountView.setPaintFlags(mNetAmountView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        mStatusIconView.setImageDrawable(getResources().getDrawable(R.drawable.transaction_tick_sign));
                        break;
                    }
                    case Constants.TRANSACTION_STATUS_CANCELLED: {
                        mNetAmountView.setPaintFlags(mNetAmountView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        mStatusIconView.setImageDrawable(getResources().getDrawable(R.drawable.transaction_cross_sign));
                        break;
                    }
                    case Constants.TRANSACTION_STATUS_REJECTED: {
                        mNetAmountView.setPaintFlags(mNetAmountView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        mStatusIconView.setImageDrawable(getResources().getDrawable(R.drawable.transaction_cross_sign));
                        break;
                    }
                    case Constants.TRANSACTION_STATUS_FAILED: {
                        mNetAmountView.setPaintFlags(mNetAmountView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        mStatusIconView.setImageDrawable(getResources().getDrawable(R.drawable.transaction_cross_sign));
                        break;
                    }
                }

                mTransactionDescriptionView.setText(description);

                if (receiver != null && !receiver.equals("")) {
                    mReceiverView.setVisibility(View.VISIBLE);
                    mReceiverView.setText(receiver);
                } else mReceiverView.setVisibility(View.GONE);

                if (DateUtils.isToday(transactionHistory.getTime())) {
                    responseTime = "Today, " + Utilities.formatTimeOnly(transactionHistory.getTime());
                }
                mTimeView.setText(responseTime);

                if (transactionHistory.getAdditionalInfo().getType().equalsIgnoreCase(Constants.TRANSACTION_TYPE_USER)) {
                    String imageUrl = transactionHistory.getAdditionalInfo().getUserProfilePic();
                    mOtherImageView.setVisibility(View.INVISIBLE);
                    mProfileImageView.setVisibility(View.VISIBLE);
                    mProfileImageView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + imageUrl, false);
                } else {
                    int iconId = transactionHistory.getAdditionalInfo().getImageWithType(getContext());
                    mProfileImageView.setVisibility(View.INVISIBLE);
                    mOtherImageView.setVisibility(View.VISIBLE);
                    mOtherImageView.setImageResource(iconId);
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



        // Now define the view holder for Normal list item
        class NormalViewHolder extends ViewHolder {
            NormalViewHolder(View itemView) {
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
            return new NormalViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_transaction_history, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            try {
                NormalViewHolder vh = (NormalViewHolder) holder;
                vh.bindView(position);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            // Return +1 as there's an extra footer (Load more...)
            if (userTransactionHistories != null && !userTransactionHistories.isEmpty())
                return userTransactionHistories.size() + 1;
            else return 0;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == userTransactionHistories.size()) {
                return FOOTER_VIEW;
            }

            return super.getItemViewType(position);
        }

    }
}
