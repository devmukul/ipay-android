package bd.com.ipay.ipayskeleton.HomeFragments.TransactionHistoryFragments;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.RequestPaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.SentReceivedRequestReviewActivity;
import bd.com.ipay.ipayskeleton.Activities.TransactionDetailsActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.CustomView.CustomSwipeRefreshLayout;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Utilities.ContactSearchHelper;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory.TransactionHistory;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory.TransactionHistoryRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory.TransactionHistoryResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class TransactionHistoryPendingFragment extends ProgressFragment implements HttpResponseListener {
    private HttpRequestPostAsyncTask mTransactionHistoryTask = null;
    private TransactionHistoryResponse mTransactionHistoryResponse;

    private RecyclerView mTransactionHistoryRecyclerView;
    private TransactionHistoryAdapter mTransactionHistoryAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<TransactionHistory> userTransactionHistories;
    private CustomSwipeRefreshLayout mSwipeRefreshLayout;

    private String mMobileNumber;

    private LinearLayout serviceFilterLayout;
    private LinearLayout dateFilterLayout;

    private CheckBox mFilterRequestMoney;
    private CheckBox mFilterAddMoney;
    private CheckBox mFilterWithdrawMoney;
    private CheckBox mFilterTopUp;
    private CheckBox mFilterRequestPayment;
    private Button mClearServiceFilterButton;

    private Button mFromDateButton;
    private Button mToDateButton;
    private Button clearDateFilterButton;
    private Button filterByDateButton;
    private TextView mEmptyListTextView;

    private int historyPageCount = 0;
    private Integer type = null;
    private Calendar fromDate = null;
    private Calendar toDate = null;

    private boolean hasNext = false;
    private boolean isLoading = false;
    private boolean clearListAfterLoading;

    private final int REQUEST_MONEY_REVIEW_REQUEST = 101;
    private final int REQUEST_PAYMENT_REVIEW_REQUEST = 102;

    private Map<CheckBox, Integer> mCheckBoxTypeMap;

    private Menu menu;

    private TransactionHistoryBroadcastReceiver transactionHistoryBroadcastReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_transaction_history, container, false);
        getActivity().setTitle(R.string.transaction_history);

        mMobileNumber = ProfileInfoCacheManager.getMobileNumber();
        initializeViews(view);
        setupViewsAndActions();
        handleBackPressWhenFilterIsOn(view);

        mSwipeRefreshLayout.setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    refreshTransactionHistory();
                }
            }
        });

        return view;
    }

    private void initializeViews(View view) {
        mEmptyListTextView = (TextView) view.findViewById(R.id.empty_list_text);
        mSwipeRefreshLayout = (CustomSwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mTransactionHistoryRecyclerView = (RecyclerView) view.findViewById(R.id.list_transaction_history);
        serviceFilterLayout = (LinearLayout) view.findViewById(R.id.service_filters_layout);
        dateFilterLayout = (LinearLayout) view.findViewById(R.id.date_filter_layout);
        mClearServiceFilterButton = (Button) view.findViewById(R.id.button_clear_filter_service);

        mFilterRequestMoney = (CheckBox) view.findViewById(R.id.filter_request_money);
        mFilterAddMoney = (CheckBox) view.findViewById(R.id.filter_add_money);
        mFilterWithdrawMoney = (CheckBox) view.findViewById(R.id.filter_withdraw_money);
        mFilterTopUp = (CheckBox) view.findViewById(R.id.filter_top_up);
        mFilterRequestPayment = (CheckBox) view.findViewById(R.id.filter_request_payment);

        mFromDateButton = (Button) view.findViewById(R.id.fromButton);
        mToDateButton = (Button) view.findViewById(R.id.toButton);
        clearDateFilterButton = (Button) view.findViewById(R.id.button_clear_filter_date);
        filterByDateButton = (Button) view.findViewById(R.id.button_filter_date);
    }

    private void setupViewsAndActions() {
        setupRecyclerView();
        setupCheckboxTypeMap();
        setActionsForServiceTypeFilter();
        setActionsForDateFilter();
    }

    private void setupRecyclerView() {
        mTransactionHistoryAdapter = new TransactionHistoryAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mTransactionHistoryRecyclerView.setLayoutManager(mLayoutManager);
        mTransactionHistoryRecyclerView.setAdapter(mTransactionHistoryAdapter);
    }

    private void setupCheckboxTypeMap() {
        mCheckBoxTypeMap = new HashMap<>();
        mCheckBoxTypeMap.put(mFilterRequestMoney, Constants.TRANSACTION_HISTORY_REQUEST_MONEY);
        mCheckBoxTypeMap.put(mFilterAddMoney, Constants.TRANSACTION_HISTORY_ADD_MONEY);
        mCheckBoxTypeMap.put(mFilterWithdrawMoney, Constants.TRANSACTION_HISTORY_WITHDRAW_MONEY);
        mCheckBoxTypeMap.put(mFilterTopUp, Constants.TRANSACTION_HISTORY_TOP_UP);
        mCheckBoxTypeMap.put(mFilterRequestPayment, Constants.TRANSACTION_HISTORY_REQUEST_PAYMENT);
    }

    private void handleBackPressWhenFilterIsOn(View view) {
        // Handle back press action when action mode is on.
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (dateFilterLayout.getVisibility() == View.VISIBLE)
                        dateFilterLayout.setVisibility(View.GONE);
                    else if (serviceFilterLayout.getVisibility() == View.VISIBLE)
                        serviceFilterLayout.setVisibility(View.GONE);
                    else return false;
                }
                return true;
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getPendingTransactionHistory();
    }

    @Override
    public void onResume() {
        super.onResume();

        transactionHistoryBroadcastReceiver = new TransactionHistoryBroadcastReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(transactionHistoryBroadcastReceiver,
                new IntentFilter(Constants.PENDING_TRANSACTION_HISTORY_UPDATE_BROADCAST));
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
                clearDateFilters();
                clearServiceFilters();
                refreshTransactionHistory();
            }
        }
    }

    @Override
    public void onDestroyView() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(transactionHistoryBroadcastReceiver);
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.activity_transaction_history, menu);
        menuInflater.inflate(R.menu.clear_filter, menu);
        this.menu = menu;
        menu.findItem(R.id.action_clear_filter).setVisible(false);

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // Remove search action of contacts
        if (menu.findItem(R.id.action_search_contacts) != null)
            menu.findItem(R.id.action_search_contacts).setVisible(false);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter_by_date:
                if (serviceFilterLayout.getVisibility() == View.VISIBLE)
                    serviceFilterLayout.setVisibility(View.GONE);
                dateFilterLayout.setVisibility(View.VISIBLE);
                return true;
            case R.id.action_filter_by_service:
                if (dateFilterLayout.getVisibility() == View.VISIBLE)
                    dateFilterLayout.setVisibility(View.GONE);
                serviceFilterLayout.setVisibility(View.VISIBLE);
                return true;
            case R.id.action_clear_filter:
                clearDateFilters();
                clearServiceFilters();
                setContentShown(false);
                refreshTransactionHistory();
                menu.findItem(R.id.action_clear_filter).setVisible(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refreshTransactionHistory() {
        historyPageCount = 0;
        clearListAfterLoading = true;
        getPendingTransactionHistory();
    }

    private boolean verifyDateFilter() {
        if (mFromDateButton.getText().toString().equals("")) return false;
        else return true;
    }

    private void setActionsForDateFilter() {
        clearDateFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearDateFilters();
                clearServiceFilters();
                setContentShown(false);
                refreshTransactionHistory();
            }
        });

        filterByDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (verifyDateFilter()) {
                    menu.findItem(R.id.action_clear_filter).setVisible(true);
                    clearServiceFilters();
                    dateFilterLayout.setVisibility(View.GONE);
                    setContentShown(false);
                    refreshTransactionHistory();
                } else {
                    Toast.makeText(getActivity(), R.string.select_a_valid_date, Toast.LENGTH_LONG).show();
                }
            }
        });

        mFromDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                if (!mFromDateButton.getText().toString().equals("")) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    final Date fromDate;
                    try {
                        fromDate = sdf.parse(mFromDateButton.getText().toString().trim());
                        calendar.setTime(fromDate);
                        DatePickerDialog dpd = new DatePickerDialog(getActivity(), mFromDateSetListener, calendar.get(Calendar.YEAR)
                                , calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                        dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
                        dpd.show();

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    DatePickerDialog dpd = new DatePickerDialog(getActivity(), mFromDateSetListener, calendar.get(Calendar.YEAR)
                            , calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                    dpd.show();
                }
            }
        });

        mToDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                final Date fromDate;
                try {
                    fromDate = sdf.parse(mFromDateButton.getText().toString().trim());

                    DatePickerDialog dpd = new DatePickerDialog(getActivity(), mToDateSetListener, Constants.STARTING_YEAR
                            , Constants.STARTING_MONTH, Constants.STARTING_DATE);
                    dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
                    dpd.getDatePicker().setMinDate(fromDate.getTime());
                    dpd.show();

                } catch (ParseException e) {
                    Toast.makeText(getActivity(), R.string.select_from_date_first, Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void setActionsForServiceTypeFilter() {

        mClearServiceFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearDateFilters();
                clearServiceFilters();
                setContentShown(false);
                refreshTransactionHistory();
            }
        });

        /**
         * Add OnClickListener for all checkboxes
         */
        for (final CheckBox serviceFilter : mCheckBoxTypeMap.keySet()) {
            serviceFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menu.findItem(R.id.action_clear_filter).setVisible(true);

                    clearDateFilters();
                    if (serviceFilter.isChecked()) {
                        type = mCheckBoxTypeMap.get(serviceFilter);
                    } else {
                        type = null;
                    }

                    /**
                     * Un-check all checkboxes other than this one
                     */
                    for (final CheckBox otherServiceFilter : mCheckBoxTypeMap.keySet()) {
                        if (otherServiceFilter != serviceFilter) {
                            otherServiceFilter.setChecked(false);
                        }
                    }

                    setContentShown(false);
                    refreshTransactionHistory();
                    serviceFilterLayout.setVisibility(View.GONE);
                }
            });
        }
    }

    private void clearServiceFilters() {
        type = null;
        for (CheckBox serviceFilter : mCheckBoxTypeMap.keySet()) {
            serviceFilter.setChecked(false);
        }

        serviceFilterLayout.setVisibility(View.GONE);
    }

    private void clearDateFilters() {
        fromDate = null;
        toDate = null;
        mFromDateButton.setText("");
        mToDateButton.setText("");

        dateFilterLayout.setVisibility(View.GONE);
    }

    private final DatePickerDialog.OnDateSetListener mFromDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                    fromDate = Calendar.getInstance();
                    fromDate.clear();
                    fromDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    fromDate.set(Calendar.MONTH, monthOfYear);
                    fromDate.set(Calendar.YEAR, year);

                    toDate = Calendar.getInstance();
                    toDate.setTime(fromDate.getTime());
                    toDate.add(Calendar.DATE, 1);

                    String fromDateStr = String.format(Constants.DATE_FORMAT, dayOfMonth, monthOfYear + 1, year);

                    mFromDateButton.setText(fromDateStr);
                    mToDateButton.setText(fromDateStr);
                }
            };

    private final DatePickerDialog.OnDateSetListener mToDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                    toDate = Calendar.getInstance();
                    toDate.clear();
                    toDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    toDate.set(Calendar.MONTH, monthOfYear);
                    toDate.set(Calendar.YEAR, year);

                    // If we want to filter transactions until August 1, 2016, we actually need to set toDate to
                    // August 2, 2016 while sending request to server. Why? Because August 1, 2016 means
                    // 12:00:00 am at August 1, whereas we need to show all transactions until 11:59:59 pm.
                    // Simplest way to do this is to just show all transactions until 12:00 am in the next day.
                    toDate.add(Calendar.DATE, 1);

                    String toDateStr = String.format(Constants.DATE_FORMAT, dayOfMonth, monthOfYear + 1, year);

                    mToDateButton.setText(toDateStr);
                }
            };

    private void getPendingTransactionHistory() {
        if (mTransactionHistoryTask != null) {
            return;
        }
        TransactionHistoryRequest mTransactionHistoryRequest;
        if (fromDate != null && toDate != null) {
            mTransactionHistoryRequest = new TransactionHistoryRequest(
                    type, historyPageCount, fromDate.getTimeInMillis(), toDate.getTimeInMillis(), null);
        } else {
            mTransactionHistoryRequest = new TransactionHistoryRequest(type, historyPageCount);
        }

        Gson gson = new Gson();
        String json = gson.toJson(mTransactionHistoryRequest);
        mTransactionHistoryTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_PENDING_TRANSACTION_HISTORY,
                Constants.BASE_URL_SM + Constants.URL_TRANSACTION_HISTORY_PENDING, json, getActivity());
        mTransactionHistoryTask.mHttpResponseListener = this;
        mTransactionHistoryTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void loadTransactionHistory(List<TransactionHistory> transactionHistories, boolean hasNext) {
        if (clearListAfterLoading || userTransactionHistories == null || userTransactionHistories.size() == 0) {
            userTransactionHistories = transactionHistories;
            clearListAfterLoading = false;
        } else {
            List<TransactionHistory> tempTransactionHistories;
            tempTransactionHistories = transactionHistories;
            userTransactionHistories.addAll(tempTransactionHistories);
        }

        this.hasNext = hasNext;
        if (userTransactionHistories != null && userTransactionHistories.size() > 0)
            mEmptyListTextView.setVisibility(View.GONE);
        else
            mEmptyListTextView.setVisibility(View.VISIBLE);

        if (isLoading)
            isLoading = false;
        mTransactionHistoryAdapter.notifyDataSetChanged();
        setContentShown(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_MONEY_REVIEW_REQUEST || requestCode == REQUEST_PAYMENT_REVIEW_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                refreshTransactionHistory();
            }
        }
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mTransactionHistoryTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.fetch_info_failed, Toast.LENGTH_LONG).show();
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_GET_PENDING_TRANSACTION_HISTORY)) {

            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                try {
                    mTransactionHistoryResponse = gson.fromJson(result.getJsonString(), TransactionHistoryResponse.class);

                    loadTransactionHistory(mTransactionHistoryResponse.getTransactions(), mTransactionHistoryResponse.isHasNext());

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

    private class TransactionHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int FOOTER_VIEW = 1;

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView mTransactionDescriptionView;
            private final TextView mTimeView;
            private final TextView mReceiverView;
            private final TextView mAmountTextView;
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
                mProfileImageView = (ProfileImageView) itemView.findViewById(R.id.profile_picture);
                mOtherImageView = (ImageView) itemView.findViewById(R.id.other_image);
                mBalanceView = itemView.findViewById(R.id.balance_holder);
            }

            public void bindView(int pos) {
                final TransactionHistory transactionHistory = userTransactionHistories.get(pos);

                final String description = transactionHistory.getShortDescription(mMobileNumber);
                final String receiver = transactionHistory.getReceiver();
                final String responseTime = Utilities.formatDateWithTime(transactionHistory.getResponseTime());
                final String netAmountWithSign = transactionHistory.getNetAmountFormatted(transactionHistory.getAdditionalInfo().getUserMobileNumber());
                final Double balance = transactionHistory.getBalance();
                final String imageUrl = transactionHistory.getAdditionalInfo().getUserProfilePic();
                final int bankIcon = transactionHistory.getAdditionalInfo().getBankIcon(getContext());
                final String bankCode = transactionHistory.getAdditionalInfo().getBankCode();
                final int serviceId = transactionHistory.getServiceID();

                if (balance != null) {
                    mAmountTextView.setText(Utilities.formatTakaWithComma(balance));
                    mBalanceView.setVisibility(View.VISIBLE);
                } else mBalanceView.setVisibility(View.GONE);

                mTransactionDescriptionView.setText(description);
                if (receiver != null && !receiver.equals("")) {
                    mReceiverView.setVisibility(View.VISIBLE);
                    mReceiverView.setText(receiver);
                } else mReceiverView.setVisibility(View.GONE);

                mNetAmountView.setText(netAmountWithSign);
                mTimeView.setText(responseTime);

                if (serviceId == Constants.TRANSACTION_HISTORY_ADD_MONEY) {
                    mProfileImageView.setVisibility(View.INVISIBLE);
                    mOtherImageView.setVisibility(View.VISIBLE);
                    if (bankCode != null) mOtherImageView.setImageResource(bankIcon);
                    else mOtherImageView.setImageResource(R.drawable.ic_tran_add);
                } else if (serviceId == Constants.TRANSACTION_HISTORY_WITHDRAW_MONEY) {
                    mProfileImageView.setVisibility(View.INVISIBLE);
                    mOtherImageView.setVisibility(View.VISIBLE);
                    if (bankCode != null) mOtherImageView.setImageResource(bankIcon);
                    else mOtherImageView.setImageResource(R.drawable.ic_tran_withdraw);
                } else if (serviceId == Constants.TRANSACTION_HISTORY_TOP_UP) {
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
                    public void onClick(View v) {
                        if (!mSwipeRefreshLayout.isRefreshing()) {
                            if (serviceId == Constants.TRANSACTION_HISTORY_REQUEST_MONEY)
                                launchRequestMoneyReviewPage(transactionHistory);
                            else if (serviceId == Constants.TRANSACTION_HISTORY_REQUEST_PAYMENT || serviceId == Constants.TRANSACTION_HISTORY_INVOICE)
                                launchRequestPaymentReviewPage(transactionHistory);
                            else {
                                Intent intent = new Intent(getActivity(), TransactionDetailsActivity.class);
                                intent.putExtra(Constants.TRANSACTION_DETAILS, transactionHistory);
                                startActivity(intent);
                            }
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
                            getPendingTransactionHistory();
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

            View v;

            if (viewType == FOOTER_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_load_more_footer, parent, false);

                return new FooterViewHolder(v);
            }

            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_transaction_history, parent, false);

            return new NormalViewHolder(v);
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

    private void launchRequestMoneyReviewPage(TransactionHistory transactionHistory) {

        Intent intent = new Intent(getActivity(), SentReceivedRequestReviewActivity.class);
        intent.putExtra(Constants.AMOUNT, new BigDecimal(transactionHistory.getAmount()));
        intent.putExtra(Constants.INVOICE_RECEIVER_TAG,
                ContactEngine.formatMobileNumberBD(transactionHistory.getAdditionalInfo().getUserMobileNumber()));

        intent.putExtra(Constants.INVOICE_DESCRIPTION_TAG, transactionHistory.getPurpose());
        intent.putExtra(Constants.MONEY_REQUEST_ID, transactionHistory.getId());
        intent.putExtra(Constants.NAME, transactionHistory.getReceiver());
        intent.putExtra(Constants.PHOTO_URI, Constants.BASE_URL_FTP_SERVER + transactionHistory.getAdditionalInfo().getUserProfilePic());
        intent.putExtra(Constants.SWITCHED_FROM_TRANSACTION_HISTORY, true);

        if (ProfileInfoCacheManager.getMobileNumber().equals(transactionHistory.getOriginatingMobileNumber())) {
            intent.putExtra(Constants.IS_IN_CONTACTS,
                    new ContactSearchHelper(getActivity()).searchMobileNumber(transactionHistory.getAdditionalInfo().getUserMobileNumber()));
            intent.putExtra(Constants.REQUEST_TYPE, Constants.REQUEST_TYPE_SENT_REQUEST);
        } else {
            intent.putExtra(Constants.IS_IN_CONTACTS,
                    new ContactSearchHelper(getActivity()).searchMobileNumber(transactionHistory.getOriginatingMobileNumber()));
        }
        startActivityForResult(intent, REQUEST_MONEY_REVIEW_REQUEST);
    }

    private void launchRequestPaymentReviewPage(TransactionHistory transactionHistory) {
        Intent intent = new Intent(getActivity(), RequestPaymentActivity.class);
        intent.putExtra(Constants.REQUEST_ID, transactionHistory.getId());
        intent.putExtra(Constants.SWITCHED_FROM_TRANSACTION_HISTORY, true);

        startActivityForResult(intent, REQUEST_PAYMENT_REVIEW_REQUEST);
    }

    private class TransactionHistoryBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshTransactionHistory();
        }
    }
}

