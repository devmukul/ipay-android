package bd.com.ipay.ipayskeleton.DrawerFragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.CustomSwipeRefreshLayout;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory.TransactionHistoryClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory.TransactionHistoryRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory.TransactionHistoryResponse;
import bd.com.ipay.ipayskeleton.R;
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

    private LinearLayout eventFilterLayout;
    private LinearLayout dateFilterLayout;

    private CheckBox mFilterOpeningBalance;
    private CheckBox mFilterSendMoney;
    private CheckBox mFilterRequestMoney;
    private CheckBox mFilterAddMoney;
    private CheckBox mFilterWithdrawMoney;
    private CheckBox mFilterTopUp;
    private CheckBox mFilterPayment;
    private CheckBox mFilterEducation;
    private Button mClearEventFilterButton;

    private EditText mFromDateEditText;
    private EditText mToDateEditText;
    private ImageView mFromDatePicker;
    private ImageView mToDatePicker;
    private Button clearDateFilterButton;
    private Button filterByDateButton;
    private TextView mEmptyListTextView;

    private int historyPageCount = 0;
    private Integer type = null;
    private Calendar fromDate = null;
    private Calendar toDate = null;
    private int mYear;
    private int mMonth;
    private int mDay;

    private boolean hasNext = false;

    private Map<CheckBox, Integer> mCheckBoxTypeMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_transaction_history, container, false);
        getActivity().setTitle(R.string.transaction_history);

        mEmptyListTextView = (TextView) v.findViewById(R.id.empty_list_text);

        SharedPreferences pref = getActivity()
                .getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
        mMobileNumber = pref.getString(Constants.USERID, "");

        mSwipeRefreshLayout = (CustomSwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        mTransactionHistoryRecyclerView = (RecyclerView) v.findViewById(R.id.list_transaction_history);

        mTransactionHistoryAdapter = new TransactionHistoryAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mTransactionHistoryRecyclerView.setLayoutManager(mLayoutManager);
        mTransactionHistoryRecyclerView.setAdapter(mTransactionHistoryAdapter);

        eventFilterLayout = (LinearLayout) v.findViewById(R.id.event_filters_layout);
        dateFilterLayout = (LinearLayout) v.findViewById(R.id.date_filter_layout);
        mClearEventFilterButton = (Button) v.findViewById(R.id.button_clear_filter_event);

        mFilterOpeningBalance = (CheckBox) v.findViewById(R.id.filter_opening_balance);
        mFilterSendMoney = (CheckBox) v.findViewById(R.id.filter_send_money);
        mFilterRequestMoney = (CheckBox) v.findViewById(R.id.filter_request_money);
        mFilterAddMoney = (CheckBox) v.findViewById(R.id.filter_add_money);
        mFilterWithdrawMoney = (CheckBox) v.findViewById(R.id.filter_withdraw_money);
        mFilterTopUp = (CheckBox) v.findViewById(R.id.filter_top_up);
        mFilterPayment = (CheckBox) v.findViewById(R.id.filter_payment);
        mFilterEducation = (CheckBox) v.findViewById(R.id.filter_education);

        mCheckBoxTypeMap = new HashMap<>();
        mCheckBoxTypeMap.put(mFilterOpeningBalance, Constants.TRANSACTION_HISTORY_OPENING_BALANCE);
        mCheckBoxTypeMap.put(mFilterSendMoney, Constants.TRANSACTION_HISTORY_SEND_MONEY);
        mCheckBoxTypeMap.put(mFilterRequestMoney, Constants.TRANSACTION_HISTORY_REQUEST_MONEY);
        mCheckBoxTypeMap.put(mFilterAddMoney, Constants.TRANSACTION_HISTORY_ADD_MONEY);
        mCheckBoxTypeMap.put(mFilterWithdrawMoney, Constants.TRANSACTION_HISTORY_WITHDRAW_MONEY);
        mCheckBoxTypeMap.put(mFilterTopUp, Constants.TRANSACTION_HISTORY_TOP_UP);
        mCheckBoxTypeMap.put(mFilterPayment, Constants.TRANSACTION_HISTORY_PAYMENT);
        mCheckBoxTypeMap.put(mFilterEducation, Constants.TRANSACTION_HISTORY_EDUCATION);

        mFromDateEditText = (EditText) v.findViewById(R.id.fromEditText);
        mToDateEditText = (EditText) v.findViewById(R.id.toEditText);
        mFromDatePicker = (ImageView) v.findViewById(R.id.fromDatePicker);
        mToDatePicker = (ImageView) v.findViewById(R.id.toDatePicker);
        clearDateFilterButton = (Button) v.findViewById(R.id.button_clear_filter_date);
        filterByDateButton = (Button) v.findViewById(R.id.button_filter_date);

        setActionsForEventTypeFilter();
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
                    else if (eventFilterLayout.getVisibility() == View.VISIBLE)
                        eventFilterLayout.setVisibility(View.GONE);
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

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setContentShown(false);
        getTransactionHistory();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.activity_transaction_history, menu);
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
                if (eventFilterLayout.getVisibility() == View.VISIBLE)
                    eventFilterLayout.setVisibility(View.GONE);
                dateFilterLayout.setVisibility(View.VISIBLE);
                Utilities.setLayoutAnim_slideDown(dateFilterLayout, getActivity());
                return true;
            case R.id.action_filter_by_event:
                if (dateFilterLayout.getVisibility() == View.VISIBLE)
                    dateFilterLayout.setVisibility(View.GONE);
                eventFilterLayout.setVisibility(View.VISIBLE);
                Utilities.setLayoutAnim_slideDown(eventFilterLayout, getActivity());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refreshTransactionHistory() {
        setContentShown(false);

        historyPageCount = 0;
        if (userTransactionHistoryClasses != null) userTransactionHistoryClasses.clear();
        getTransactionHistory();
    }

    private void setActionsForDateFilter() {

        clearDateFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearDateFilters();
                refreshTransactionHistory();
            }
        });

        filterByDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateFilterLayout.setVisibility(View.GONE);
                refreshTransactionHistory();
            }
        });

        mFromDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                final Date fromDateStart;
                try {
                    fromDateStart = sdf.parse(Constants.STARTING_DATE_OF_IPAY);

                    DatePickerDialog dpd = new DatePickerDialog(getActivity(), mFromDateSetListener, Constants.STARTING_YEAR
                            , Constants.STARTING_MONTH, Constants.STARTING_DATE);
                    dpd.getDatePicker().setMinDate(fromDateStart.getTime());
                    dpd.show();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });


        mToDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                final Date fromDate;
                try {
                    fromDate = sdf.parse(mFromDateEditText.getText().toString().trim());

                    DatePickerDialog dpd = new DatePickerDialog(getActivity(), mToDateSetListener, Constants.STARTING_YEAR
                            , Constants.STARTING_MONTH, Constants.STARTING_DATE);
                    dpd.getDatePicker().setMinDate(fromDate.getTime());
                    dpd.show();

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void setActionsForEventTypeFilter() {

        mClearEventFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearEventFilters();
                refreshTransactionHistory();
            }
        });

        /**
         * Add OnClickListener for all checkboxes
         */
        for (final CheckBox eventFilter : mCheckBoxTypeMap.keySet()) {
            eventFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (eventFilter.isChecked()) {
                        type = mCheckBoxTypeMap.get(eventFilter);
                    } else {
                        type = null;
                    }

                    /**
                     * Un-check all checkboxes other than this one
                     */
                    for (final CheckBox otherEventFilter : mCheckBoxTypeMap.keySet()) {
                        if (otherEventFilter != eventFilter) {
                            otherEventFilter.setChecked(false);
                        }
                    }

                    refreshTransactionHistory();
                    eventFilterLayout.setVisibility(View.GONE);
                }
            });
        }
    }

    private void clearEventFilters() {
        type = null;
        for (CheckBox eventFilter : mCheckBoxTypeMap.keySet()) {
            eventFilter.setChecked(false);
        }

        eventFilterLayout.setVisibility(View.GONE);
    }

    private void clearDateFilters() {
        fromDate = null;
        toDate = null;
        mFromDateEditText.setText("");
        mToDateEditText.setText("");

        dateFilterLayout.setVisibility(View.GONE);
    }

    private DatePickerDialog.OnDateSetListener mFromDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear + 1;
                    mDay = dayOfMonth;

                    fromDate = Calendar.getInstance();
                    fromDate.clear();
                    fromDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    fromDate.set(Calendar.MONTH, monthOfYear);
                    fromDate.set(Calendar.YEAR, year);

                    String fromDatePicker, fromMonthPicker, fromYearPicker;
                    if (mDay < 10) fromDatePicker = "0" + mDay;
                    else fromDatePicker = mDay + "";
                    if (mMonth < 10) fromMonthPicker = "0" + mMonth;
                    else fromMonthPicker = mMonth + "";
                    fromYearPicker = mYear + "";

                    mFromDateEditText.setText(fromDatePicker + "/" + fromMonthPicker + "/" + fromYearPicker);
                }
            };

    private DatePickerDialog.OnDateSetListener mToDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear + 1;
                    mDay = dayOfMonth;

                    toDate = Calendar.getInstance();
                    toDate.clear();
                    toDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    toDate.set(Calendar.MONTH, monthOfYear);
                    toDate.set(Calendar.YEAR, year);
                    toDate.add(Calendar.DATE, 1);

                    String toDatePicker, toMonthPicker, toYearPicker;
                    if (mDay < 10) toDatePicker = "0" + mDay;
                    else toDatePicker = mDay + "";
                    if (mMonth < 10) toMonthPicker = "0" + mMonth;
                    else toMonthPicker = mMonth + "";
                    toYearPicker = mYear + "";

                    mToDateEditText.setText(toDatePicker + "/" + toMonthPicker + "/" + toYearPicker);
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

    private void showTransactionHistoryDialogue(double amount, double fee, double netAmount,double balance, String purpose, String time, Integer statusCode,
                                                 String description, String transactionID, String receiverMobileNumber, String receiverName, String photoUri,
                                                 int serviceId, String mBankName, String mBankAccountNumber, String receiver) {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.transaction_details)
                .customView(R.layout.dialog_transaction_details, true)
                .negativeText(R.string.ok)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();

        View view = dialog.getCustomView();
        final TextView descriptionTextView = (TextView) view.findViewById(R.id.description);
        final TextView timeTextView = (TextView) view.findViewById(R.id.time);
        final TextView amountTextView = (TextView) view.findViewById(R.id.amount);
        final TextView feeTextView = (TextView) view.findViewById(R.id.fee);
        final TextView transactionIDTextView = (TextView) view.findViewById(R.id.transaction_id);
        final TextView netAmountTextView = (TextView) view.findViewById(R.id.netAmount);
        final TextView balanceTextView = (TextView) view.findViewById(R.id.balance);
        final TextView purposeTextView = (TextView) view.findViewById(R.id.purpose);
        final TextView statusTextView = (TextView) view.findViewById(R.id.status);
        final LinearLayout purposeLayout = (LinearLayout) view.findViewById(R.id.purpose_layout);

        final ProfileImageView mProfileImageView = (ProfileImageView) view.findViewById(R.id.profile_picture);
        final ImageView otherImageView = (ImageView) view.findViewById(R.id.other_image);
        final TextView mNameView = (TextView) view.findViewById(R.id.textview_name);
        final TextView mMobileNumberView = (TextView) view.findViewById(R.id.textview_mobile_number);

        descriptionTextView.setText(description);
        timeTextView.setText(time);
        amountTextView.setText(Utilities.formatTaka(amount));
        feeTextView.setText(Utilities.formatTaka(fee));
        transactionIDTextView.setText(getString(R.string.transaction_id) + " " + transactionID);
        netAmountTextView.setText(Utilities.formatTaka(netAmount));
        balanceTextView.setText(Utilities.formatTaka(balance));
        if (serviceId == Constants.TRANSACTION_HISTORY_TOP_UP_ROLLBACK  || serviceId == Constants.TRANSACTION_HISTORY_TOP_UP) {
            purposeLayout.setVisibility(View.GONE);
        }
        else if (purpose != null && purpose.length() > 0) purposeTextView.setText(purpose);
        else purposeLayout.setVisibility(View.GONE);

        if(serviceId == Constants.TRANSACTION_HISTORY_ADD_MONEY) {
            mNameView.setVisibility(View.VISIBLE);
            mNameView.setText(mBankName);
            mMobileNumberView.setText(mBankAccountNumber);
            mProfileImageView.setVisibility(View.GONE);
            otherImageView.setVisibility(View.VISIBLE);
            otherImageView.setImageResource(R.drawable.ic_add_money_large);

        } else if(serviceId == Constants.TRANSACTION_HISTORY_WITHDRAW_MONEY) {
            mNameView.setVisibility(View.VISIBLE);
            mNameView.setText(mBankName);
            mProfileImageView.setVisibility(View.GONE);
            otherImageView.setVisibility(View.VISIBLE);
            otherImageView.setImageResource(R.drawable.ic_withdraw_money_large);

        } else if(serviceId == Constants.TRANSACTION_HISTORY_OPENING_BALANCE) {
            mNameView.setVisibility(View.VISIBLE);
            mNameView.setText(R.string.opening_balance_to);
            mMobileNumberView.setText(mMobileNumber);
            mProfileImageView.setVisibility(View.GONE);
            otherImageView.setVisibility(View.VISIBLE);
            otherImageView.setImageResource(R.drawable.ic_opening_balance);

        } else if(serviceId == Constants.TRANSACTION_HISTORY_TOP_UP) {
            mNameView.setVisibility(View.VISIBLE);
            mNameView.setText(R.string.recharge_to);
            mMobileNumberView.setText(receiver);
            mProfileImageView.setVisibility(View.GONE);
            otherImageView.setVisibility(View.VISIBLE);
            otherImageView.setImageResource(R.drawable.ic_mobile_recharge_large);

        } else {
            if (receiverName == null || receiverName.isEmpty()) {
                mNameView.setVisibility(View.GONE);

            } else {
                mNameView.setVisibility(View.VISIBLE);
                mNameView.setText(receiverName);

            }
            mMobileNumberView.setText(receiverMobileNumber);
            otherImageView.setVisibility(View.GONE);
            mProfileImageView.setVisibility(View.VISIBLE);
            mProfileImageView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + photoUri, false);
        }


        if (statusCode == Constants.HTTP_RESPONSE_STATUS_OK) {
            statusTextView.setText(getString(R.string.transaction_successful));
            statusTextView.setTextColor(getResources().getColor(R.color.bottle_green));
        } else if (statusCode == Constants.HTTP_RESPONSE_STATUS_PROCESSING) {
            statusTextView.setText(getString(R.string.in_progress));
        } else {
            statusTextView.setText(getString(R.string.transaction_failed));
            statusTextView.setTextColor(getResources().getColor(R.color.background_red));
        }

    }

    private void loadTransactionHistory(List<TransactionHistoryClass> transactionHistoryClasses,
                                        boolean hasNext, boolean clearOldTransactions) {
        if (clearOldTransactions || userTransactionHistoryClasses == null || userTransactionHistoryClasses.size() == 0) {
            userTransactionHistoryClasses = transactionHistoryClasses;
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
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
					|| result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mTransactionHistoryTask = null;
            mSwipeRefreshLayout.setRefreshing(false);
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.fetch_info_failed, Toast.LENGTH_LONG).show();
            return;
        }


        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_GET_TRANSACTION_HISTORY)) {

            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                try {
                    mTransactionHistoryResponse = gson.fromJson(result.getJsonString(), TransactionHistoryResponse.class);

                    loadTransactionHistory(mTransactionHistoryResponse.getTransactions(), mTransactionHistoryResponse.isHasNext(), false);

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
            setContentShown(true);
        }
    }

    private class TransactionHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int FOOTER_VIEW = 1;

        public TransactionHistoryAdapter() {
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView mTransactionDescriptionView;
            private TextView mTimeView;
            private TextView mReceiverView;
            private TextView loadMoreTextView;
            private TextView mAmountTextView;
            private TextView statusDescriptionView;
            private TextView netAmountView;
            private ImageView otherImageView;
            private ProfileImageView mProfileImageView;
            private View divider;

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

                if (pos == userTransactionHistoryClasses.size() -1) divider.setVisibility(View.GONE);
                else divider.setVisibility(View.VISIBLE);

                final String detailDescription = userTransactionHistoryClasses.get(pos).getDescription(mMobileNumber);
                final String description = userTransactionHistoryClasses.get(pos).getShortDescription(mMobileNumber);
                final String receiver = userTransactionHistoryClasses.get(pos).getReceiver();
                final String responseTime = new SimpleDateFormat("dd/MM/yy, h:mm a").format(userTransactionHistoryClasses.get(pos).getResponseTime());
                final double amountWithoutProcessing = userTransactionHistoryClasses.get(pos).getAmount();
                final double fee = userTransactionHistoryClasses.get(pos).getFee();
                final String netAmountWithSign = userTransactionHistoryClasses.get(pos).getNetAmountFormatted(userTransactionHistoryClasses.get(pos).getAdditionalInfo().getUserMobileNumber());
                final double netAmount = userTransactionHistoryClasses.get(pos).getNetAmount();
                final String transactionID = userTransactionHistoryClasses.get(pos).getTransactionID();
                final String purpose = userTransactionHistoryClasses.get(pos).getPurpose();
                final Integer statusCode = userTransactionHistoryClasses.get(pos).getStatusCode();
                final double balance = userTransactionHistoryClasses.get(pos).getBalance();
                final String imageUrl = userTransactionHistoryClasses.get(pos).getAdditionalInfo().getUserProfilePic();
                final String name = userTransactionHistoryClasses.get(pos).getAdditionalInfo().getUserName();
                final String mobileNumber = userTransactionHistoryClasses.get(pos).getAdditionalInfo().getUserMobileNumber();
                final String bankName = userTransactionHistoryClasses.get(pos).getAdditionalInfo().getBankAccountName();
                final String bankAccountNumber = userTransactionHistoryClasses.get(pos).getAdditionalInfo().getBankAccountNumber();
                final int serviceId = userTransactionHistoryClasses.get(pos).getServiceID();
                //final Drawable icon =  getResources().getDrawable(userTransactionHistoryClasses.get(pos).getAdditionalInfo().getBankIcon(getContext()));

                mAmountTextView.setText(Utilities.formatTakaWithComma(balance));

                if (statusCode == Constants.HTTP_RESPONSE_STATUS_OK) {
                    statusDescriptionView.setText(getString(R.string.transaction_successful));
                    statusDescriptionView.setTextColor(getResources().getColor(R.color.bottle_green));
                } else if (statusCode == Constants.HTTP_RESPONSE_STATUS_PROCESSING) {
                    statusDescriptionView.setText(getString(R.string.in_progress));
                    statusDescriptionView.setTextColor(getResources().getColor(R.color.colorAmber));
                } else {
                    statusDescriptionView.setText(getString(R.string.transaction_failed));
                    statusDescriptionView.setTextColor(getResources().getColor(R.color.background_red));
                }

                mTransactionDescriptionView.setText(description);
                mReceiverView.setText(receiver);
                netAmountView.setText(netAmountWithSign);
                mTimeView.setText(responseTime);

                if(serviceId == Constants.TRANSACTION_HISTORY_ADD_MONEY) {
                    mProfileImageView.setVisibility(View.INVISIBLE);
                    otherImageView.setVisibility(View.VISIBLE);
                    otherImageView.setImageResource(R.drawable.ic_add_money_large);
                } else if(serviceId == Constants.TRANSACTION_HISTORY_WITHDRAW_MONEY || serviceId == Constants.TRANSACTION_HISTORY_WITHDRAW_MONEY_ROLL_BACK) {
                    mProfileImageView.setVisibility(View.INVISIBLE);
                    otherImageView.setVisibility(View.VISIBLE);
                    otherImageView.setImageResource(R.drawable.ic_withdraw_money_large);
                } else if(serviceId == Constants.TRANSACTION_HISTORY_OPENING_BALANCE) {
                    mProfileImageView.setVisibility(View.INVISIBLE);
                    otherImageView.setVisibility(View.VISIBLE);
                    otherImageView.setImageResource(R.drawable.ic_opening_balance);
                } else if(serviceId == Constants.TRANSACTION_HISTORY_TOP_UP || serviceId == Constants.TRANSACTION_HISTORY_TOP_UP_ROLLBACK) {
                    mProfileImageView.setVisibility(View.INVISIBLE);
                    otherImageView.setVisibility(View.VISIBLE);
                    otherImageView.setImageResource(R.drawable.ic_topup);
                } else {
                    otherImageView.setVisibility(View.INVISIBLE);
                    mProfileImageView.setVisibility(View.VISIBLE);
                    mProfileImageView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + imageUrl, false);
                }

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mSwipeRefreshLayout.isRefreshing())
                            showTransactionHistoryDialogue(amountWithoutProcessing, fee, netAmount, balance, purpose, responseTime,
                                    statusCode, detailDescription, transactionID,mobileNumber,name,imageUrl,serviceId,bankName,bankAccountNumber, receiver);
                    }
                });

            }

            public void bindViewFooter(int pos) {
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

        // Now define the viewholder for Normal list item
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

                FooterViewHolder vh = new FooterViewHolder(v);

                return vh;
            }

            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_transaction_history, parent, false);

            NormalViewHolder vh = new NormalViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            try {
                if (holder instanceof NormalViewHolder) {
                    NormalViewHolder vh = (NormalViewHolder) holder;
                    vh.bindView(position);
                } else if (holder instanceof FooterViewHolder) {
                    FooterViewHolder vh = (FooterViewHolder) holder;
                    vh.bindViewFooter(position);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if (userTransactionHistoryClasses != null)
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
}
