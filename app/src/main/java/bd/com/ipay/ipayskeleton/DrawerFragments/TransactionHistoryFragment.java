package bd.com.ipay.ipayskeleton.DrawerFragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bd.com.ipay.ipayskeleton.Activities.TransactionDetailsActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.CustomSwipeRefreshLayout;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory.TransactionHistoryClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory.TransactionHistoryRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory.TransactionHistoryResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class TransactionHistoryFragment extends ProgressFragment implements HttpResponseListener {
    private HttpRequestPostAsyncTask mTransactionHistoryTask = null;
    private TransactionHistoryResponse mTransactionHistoryResponse;

    private RecyclerView mTransactionHistoryRecyclerView;
    private TransactionHistoryAdapter mTransactionHistoryAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<TransactionHistoryClass> userTransactionHistoryClasses;
    private CustomSwipeRefreshLayout mSwipeRefreshLayout;

    private String mMobileNumber;

    private LinearLayout serviceFilterLayout;
    private LinearLayout dateFilterLayout;

    private CheckBox mFilterOpeningBalance;
    private CheckBox mFilterSendMoney;
    private CheckBox mFilterRequestMoney;
    private CheckBox mFilterAddMoney;
    private CheckBox mFilterWithdrawMoney;
    private CheckBox mFilterTopUp;
    private CheckBox mFilterPayment;
    private CheckBox mFilterEducation;
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
    private boolean clearListAfterLoading;

    private boolean isViewShown = false;

    private Map<CheckBox, Integer> mCheckBoxTypeMap;

    private Menu menu;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_transaction_history, container, false);
        getActivity().setTitle(R.string.transaction_history);

        mEmptyListTextView = (TextView) v.findViewById(R.id.empty_list_text);

        SharedPreferences pref = getActivity()
                .getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
        mMobileNumber = ProfileInfoCacheManager.getMobileNumber();

        mSwipeRefreshLayout = (CustomSwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        mTransactionHistoryRecyclerView = (RecyclerView) v.findViewById(R.id.list_transaction_history);

        mTransactionHistoryAdapter = new TransactionHistoryAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mTransactionHistoryRecyclerView.setLayoutManager(mLayoutManager);
        mTransactionHistoryRecyclerView.setAdapter(mTransactionHistoryAdapter);

        serviceFilterLayout = (LinearLayout) v.findViewById(R.id.service_filters_layout);
        dateFilterLayout = (LinearLayout) v.findViewById(R.id.date_filter_layout);
        mClearServiceFilterButton = (Button) v.findViewById(R.id.button_clear_filter_service);

        mFilterOpeningBalance = (CheckBox) v.findViewById(R.id.filter_opening_balance);
        mFilterSendMoney = (CheckBox) v.findViewById(R.id.filter_send_money);
        mFilterAddMoney = (CheckBox) v.findViewById(R.id.filter_add_money);
        mFilterWithdrawMoney = (CheckBox) v.findViewById(R.id.filter_withdraw_money);
        mFilterTopUp = (CheckBox) v.findViewById(R.id.filter_top_up);
        mFilterPayment = (CheckBox) v.findViewById(R.id.filter_payment);
        mFilterEducation = (CheckBox) v.findViewById(R.id.filter_education);

        mCheckBoxTypeMap = new HashMap<>();
        mCheckBoxTypeMap.put(mFilterOpeningBalance, Constants.TRANSACTION_HISTORY_OPENING_BALANCE);
        mCheckBoxTypeMap.put(mFilterSendMoney, Constants.TRANSACTION_HISTORY_SEND_MONEY);
        mCheckBoxTypeMap.put(mFilterAddMoney, Constants.TRANSACTION_HISTORY_ADD_MONEY);
        mCheckBoxTypeMap.put(mFilterWithdrawMoney, Constants.TRANSACTION_HISTORY_WITHDRAW_MONEY);
        mCheckBoxTypeMap.put(mFilterTopUp, Constants.TRANSACTION_HISTORY_TOP_UP);
        mCheckBoxTypeMap.put(mFilterPayment, Constants.TRANSACTION_HISTORY_MAKE_PAYMENT);
        mCheckBoxTypeMap.put(mFilterEducation, Constants.TRANSACTION_HISTORY_EDUCATION);

        mFromDateButton = (Button) v.findViewById(R.id.fromButton);
        mToDateButton = (Button) v.findViewById(R.id.toButton);
        clearDateFilterButton = (Button) v.findViewById(R.id.button_clear_filter_date);
        filterByDateButton = (Button) v.findViewById(R.id.button_filter_date);

        setActionsForServiceTypeFilter();
        setActionsForDateFilter();

        // Handle back press action when action mode is on.
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
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

        mSwipeRefreshLayout.setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    refreshTransactionHistory();
                }
            }
        });

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mTransactionHistoryBroadcastReceiver,
                new IntentFilter(Constants.TRANSACTION_HISTORY_UPDATE_BROADCAST));

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getTransactionHistory();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getView() != null) {
            isViewShown = true;
            clearDateFilters();
            clearServiceFilters();
            refreshTransactionHistory();
        } else {
            isViewShown = false;
        }
    }

    @Override
    public void onDestroyView() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mTransactionHistoryBroadcastReceiver);

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
        getTransactionHistory();
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
                menu.findItem(R.id.action_clear_filter).setVisible(true);
                clearServiceFilters();
                dateFilterLayout.setVisibility(View.GONE);
                setContentShown(false);
                refreshTransactionHistory();
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

                    String fromDateStr = String.format("%02d/%02d/%4d", dayOfMonth, monthOfYear + 1, year);

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

                    String toDateStr = String.format("%02d/%02d/%4d", dayOfMonth, monthOfYear + 1, year);

                    mToDateButton.setText(toDateStr);
                }
            };

    private void getTransactionHistory() {
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
        mTransactionHistoryTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_TRANSACTION_HISTORY,
                Constants.BASE_URL_SM + Constants.URL_TRANSACTION_HISTORY, json, getActivity());
        mTransactionHistoryTask.mHttpResponseListener = this;
        mTransactionHistoryTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void loadTransactionHistory(List<TransactionHistoryClass> transactionHistoryClasses, boolean hasNext) {
        if (clearListAfterLoading || userTransactionHistoryClasses == null || userTransactionHistoryClasses.size() == 0) {
            userTransactionHistoryClasses = transactionHistoryClasses;
            clearListAfterLoading = false;
        } else {
            List<TransactionHistoryClass> tempTransactionHistoryClasses;
            tempTransactionHistoryClasses = transactionHistoryClasses;
            userTransactionHistoryClasses.addAll(tempTransactionHistoryClasses);
        }

        this.hasNext = hasNext;
        if (userTransactionHistoryClasses != null && userTransactionHistoryClasses.size() > 0)
            mEmptyListTextView.setVisibility(View.GONE);
        else
            mEmptyListTextView.setVisibility(View.VISIBLE);

        mTransactionHistoryAdapter.notifyDataSetChanged();
        setContentShown(true);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mTransactionHistoryTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.fetch_info_failed, Toast.LENGTH_LONG).show();
            return;
        }


        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_GET_TRANSACTION_HISTORY)) {

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
            private final TextView loadMoreTextView;
            private final TextView mAmountTextView;
            private final TextView statusDescriptionView;
            private final TextView netAmountView;
            private final ImageView otherImageView;
            private final ProfileImageView mProfileImageView;
            private final View divider;

            public ViewHolder(final View itemView) {
                super(itemView);

                mTransactionDescriptionView = (TextView) itemView.findViewById(R.id.activity_description);
                mTimeView = (TextView) itemView.findViewById(R.id.time);
                mReceiverView = (TextView) itemView.findViewById(R.id.receiver);
                loadMoreTextView = (TextView) itemView.findViewById(R.id.load_more);
                mAmountTextView = (TextView) itemView.findViewById(R.id.amount);
                netAmountView = (TextView) itemView.findViewById(R.id.net_amount);
                statusDescriptionView = (TextView) itemView.findViewById(R.id.status_description);
                mProfileImageView = (ProfileImageView) itemView.findViewById(R.id.profile_picture);
                otherImageView = (ImageView) itemView.findViewById(R.id.other_image);
                divider = itemView.findViewById(R.id.divider);
            }

            public void bindView(int pos) {

                if (pos == userTransactionHistoryClasses.size() - 1)
                    divider.setVisibility(View.GONE);
                else divider.setVisibility(View.VISIBLE);

                final TransactionHistoryClass transactionHistory = userTransactionHistoryClasses.get(pos);

                final String description = transactionHistory.getShortDescription(mMobileNumber);
                final String receiver = transactionHistory.getReceiver();
                final String responseTime = Utilities.getDateFormat(transactionHistory.getResponseTime());
                final String netAmountWithSign = transactionHistory.getNetAmountFormatted(transactionHistory.getAdditionalInfo().getUserMobileNumber());
                final Integer statusCode = transactionHistory.getStatusCode();
                final double balance = transactionHistory.getBalance();
                final String imageUrl = transactionHistory.getAdditionalInfo().getUserProfilePic();
                final int bankIcon = transactionHistory.getAdditionalInfo().getBankIcon(getContext());
                final String bankCode = transactionHistory.getAdditionalInfo().getBankCode();
                final int serviceId = transactionHistory.getServiceID();

                mAmountTextView.setText(Utilities.formatTakaWithComma(balance));

                if (statusCode == Constants.HTTP_RESPONSE_STATUS_OK) {
                    statusDescriptionView.setText(getString(R.string.transaction_successful));
                    statusDescriptionView.setTextColor(getResources().getColor(R.color.bottle_green));
                } else if (statusCode == Constants.HTTP_RESPONSE_STATUS_PROCESSING) {
                    statusDescriptionView.setText(getString(R.string.in_progress));
                    statusDescriptionView.setTextColor(getResources().getColor(R.color.colorAmber));
                } else {
                    if (serviceId != Constants.TRANSACTION_HISTORY_TOP_UP && serviceId != Constants.TRANSACTION_HISTORY_WITHDRAW_MONEY && serviceId != Constants.TRANSACTION_HISTORY_ADD_MONEY) {
                        mAmountTextView.setText(getString(R.string.not_applicable));
                    }
                    statusDescriptionView.setText(getString(R.string.transaction_failed));
                    statusDescriptionView.setTextColor(getResources().getColor(R.color.background_red));
                }

                mTransactionDescriptionView.setText(description);
                if (receiver != null && !receiver.equals("")) {
                    mReceiverView.setVisibility(View.VISIBLE);
                    mReceiverView.setText(receiver);
                } else mReceiverView.setVisibility(View.GONE);
                netAmountView.setText(netAmountWithSign);
                mTimeView.setText(responseTime);

                if (serviceId == Constants.TRANSACTION_HISTORY_ADD_MONEY) {
                    mProfileImageView.setVisibility(View.INVISIBLE);
                    otherImageView.setVisibility(View.VISIBLE);
                    if (bankCode != null) otherImageView.setImageResource(bankIcon);
                    else otherImageView.setImageResource(R.drawable.ic_tran_add);
                } else if (serviceId == Constants.TRANSACTION_HISTORY_WITHDRAW_MONEY || serviceId == Constants.TRANSACTION_HISTORY_WITHDRAW_MONEY_ROLL_BACK) {
                    mProfileImageView.setVisibility(View.INVISIBLE);
                    otherImageView.setVisibility(View.VISIBLE);
                    if (bankCode != null) otherImageView.setImageResource(bankIcon);
                    else otherImageView.setImageResource(R.drawable.ic_tran_withdraw);
                } else if (serviceId == Constants.TRANSACTION_HISTORY_OPENING_BALANCE) {
                    mProfileImageView.setVisibility(View.INVISIBLE);
                    otherImageView.setVisibility(View.VISIBLE);
                    otherImageView.setImageResource(R.drawable.ic_openingbalance);
                } else if (serviceId == Constants.TRANSACTION_HISTORY_TOP_UP || serviceId == Constants.TRANSACTION_HISTORY_TOP_UP_ROLLBACK) {
                    mProfileImageView.setVisibility(View.INVISIBLE);
                    otherImageView.setVisibility(View.VISIBLE);
                    otherImageView.setImageResource(R.drawable.ic_top_up);
                } else if (serviceId == Constants.TRANSACTION_HISTORY_EDUCATION) {
                    mProfileImageView.setVisibility(View.INVISIBLE);
                    otherImageView.setVisibility(View.VISIBLE);
                    otherImageView.setImageResource(R.drawable.ic_transaction_education);
                } else {
                    otherImageView.setVisibility(View.INVISIBLE);
                    mProfileImageView.setVisibility(View.VISIBLE);
                    mProfileImageView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + imageUrl, false);
                }

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mSwipeRefreshLayout.isRefreshing()) {
                            Intent intent = new Intent(getActivity(), TransactionDetailsActivity.class);
                            intent.putExtra(Constants.TRANSACTION_DETAILS, transactionHistory);
                            startActivity(intent);
                        }

                    }
                });

            }

            public void bindViewFooter() {
                if (hasNext) loadMoreTextView.setText(R.string.load_more);
                else loadMoreTextView.setText(R.string.no_more_results);
            }
        }

        public class FooterViewHolder extends ViewHolder {
            public FooterViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (hasNext) {
                            historyPageCount = historyPageCount + 1;
                            getTransactionHistory();
                        }
                    }
                });

                TextView loadMoreTextView = (TextView) itemView.findViewById(R.id.load_more);
                if (hasNext) loadMoreTextView.setText(R.string.load_more);
                else loadMoreTextView.setText(R.string.no_more_results);
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
            if (userTransactionHistoryClasses != null && !userTransactionHistoryClasses.isEmpty())
                return userTransactionHistoryClasses.size() + 1;
            else return 0;
        }

        @Override
        public int getItemViewType(int position) {

            if (position == userTransactionHistoryClasses.size()) {
                return FOOTER_VIEW;
            }

            return super.getItemViewType(position);
        }
    }

    private final BroadcastReceiver mTransactionHistoryBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Broadcast received", "Transaction History");
            refreshTransactionHistory();
        }
    };
}
