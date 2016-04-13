package bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Customview.CustomSwipeRefreshLayout;
import bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory.TransactionHistoryClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory.TransactionHistoryRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory.TransactionHistoryResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class TransactionHistoryFragment extends Fragment implements HttpResponseListener {

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.activity_transaction_history, menu);

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_transaction_history, container, false);
        getActivity().setTitle(R.string.transaction_history);

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

        // Refresh balance each time home page appears
        if (Utilities.isConnectionAvailable(getActivity())) {
            getTransactionHistory();
        }

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
                    historyPageCount = 0;
                    if (userTransactionHistoryClasses != null)
                        userTransactionHistoryClasses.clear();
                    getTransactionHistory();
                }
            }
        });

        return v;
    }

    private void refreshTransactionHistory() {
        historyPageCount = 0;
        if (userTransactionHistoryClasses != null) userTransactionHistoryClasses.clear();
        getTransactionHistory();
    }

    private void setActionsForDateFilter() {

        clearDateFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateFilterLayout.setVisibility(View.GONE);
                fromDate = null;
                toDate = null;
                mFromDateEditText.setText("");
                mToDateEditText.setText("");
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
                type = null;
                for (CheckBox eventFilter : mCheckBoxTypeMap.keySet()) {
                    eventFilter.setChecked(false);
                }

                refreshTransactionHistory();
                eventFilterLayout.setVisibility(View.GONE);
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
                    type, historyPageCount, fromDate.getTimeInMillis(), toDate.getTimeInMillis());
        } else {
            mTransactionHistoryRequest = new TransactionHistoryRequest(type, historyPageCount);
        }

        Gson gson = new Gson();
        String json = gson.toJson(mTransactionHistoryRequest);
        mTransactionHistoryTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_TRANSACTION_HISTORY,
                Constants.BASE_URL + Constants.URL_TRANSACTION_HISTORY, json, getActivity());
        mTransactionHistoryTask.mHttpResponseListener = this;
        mTransactionHistoryTask.execute();
    }

    @Override
    public void httpResponseReceiver(String result) {

        if (result == null) {
            mTransactionHistoryTask = null;
            mSwipeRefreshLayout.setRefreshing(false);
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.fetch_info_failed, Toast.LENGTH_LONG).show();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_GET_TRANSACTION_HISTORY)) {
            if (resultList.size() > 2) {
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {

                    try {
                        mTransactionHistoryResponse = gson.fromJson(resultList.get(2), TransactionHistoryResponse.class);

                        if (userTransactionHistoryClasses == null || userTransactionHistoryClasses.size() == 0) {
                            userTransactionHistoryClasses = mTransactionHistoryResponse.getTransactions();
                        } else {
                            List<TransactionHistoryClass> tempTransactionHistoryClasses;
                            tempTransactionHistoryClasses = mTransactionHistoryResponse.getTransactions();
                            userTransactionHistoryClasses.addAll(tempTransactionHistoryClasses);
                        }

                        hasNext = mTransactionHistoryResponse.isHasNext();
                        mTransactionHistoryAdapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        e.printStackTrace();
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.transaction_history_get_failed, Toast.LENGTH_LONG).show();
                    }

                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.transaction_history_get_failed, Toast.LENGTH_LONG).show();
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.transaction_history_get_failed, Toast.LENGTH_LONG).show();

            mSwipeRefreshLayout.setRefreshing(false);
            mTransactionHistoryTask = null;
        }
    }

    private class TransactionHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int FOOTER_VIEW = 1;

        public TransactionHistoryAdapter() {
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView mTransactionDescription;
            private TextView mTime;
            private TextView loadMoreTextView;
            private RoundedImageView mPortrait;
            private TextView mAmountTextView;
            private ImageView statusView;

            public ViewHolder(final View itemView) {
                super(itemView);

                mTransactionDescription = (TextView) itemView.findViewById(R.id.activity_description);
                mTime = (TextView) itemView.findViewById(R.id.time);
                loadMoreTextView = (TextView) itemView.findViewById(R.id.load_more);
                mAmountTextView = (TextView) itemView.findViewById(R.id.amount);
                statusView = (ImageView) itemView.findViewById(R.id.status);
                mPortrait = (RoundedImageView) itemView.findViewById(R.id.portrait);
            }

            public void bindView(int pos) {
                double amount = userTransactionHistoryClasses.get(pos).getAmount(mMobileNumber);

                String description = userTransactionHistoryClasses.get(pos).getDescription(mMobileNumber);
                String time = new SimpleDateFormat("EEE, MMM d, ''yy, h:mm a").format(userTransactionHistoryClasses.get(pos).getTime());

                // Handle debit credit
                if (amount > 0)
                    mAmountTextView.setText("+" + String.format("%.2f", amount) + " Tk."); // TODO: Set currency later, remove + for credit
                else
                    mAmountTextView.setText(String.format("%.2f", amount) + " Tk.");     // TODO: Set taka unicode character

                mTransactionDescription.setText(description);
                mTime.setText(time);

                if (userTransactionHistoryClasses.get(pos).getStatusCode().toString()
                        .equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    mAmountTextView.setTextColor(getResources().getColor(R.color.colorTextPrimary));
                    statusView.setColorFilter(Color.GREEN);
                    statusView.setImageResource(R.drawable.ic_check_circle_black_24dp);

                } else if (userTransactionHistoryClasses.get(pos).getStatusCode().toString()
                        .equals(Constants.HTTP_RESPONSE_STATUS_PROCESSING)) {
                    mAmountTextView.setTextColor(getResources().getColor(R.color.text_gray));
                    statusView.setColorFilter(Color.GRAY);
                    statusView.setImageResource(R.drawable.ic_cached_black_24dp);

                } else {
                    mAmountTextView.setTextColor(getResources().getColor(R.color.background_red));
                }


                // TODO: uncomment this when pro pic will be available
//                Set<UserProfilePictureClass> userProfilePictureClassSet = userTransactionHistoryClasses.
//                        get(pos).getOtherUserprofilePictures();


//                if (userProfilePictureClassSet.size() > 0) {
//                    for (Iterator<UserProfilePictureClass> it = userProfilePictureClassSet.iterator(); it.hasNext(); ) {
//                        UserProfilePictureClass temp = it.next();
//                        Glide.with(getActivity())
//                                .load(Constants.BASE_URL_IMAGE + temp.getUrl())
//                                .into(mPortrait);
//                        break;
//                    }
//                } else {
//                    Glide.with(getActivity())
//                            .load(R.drawable.ic_transaction_history)
//                            .into(mPortrait);
//                }

                //TODO: remove this when pro pic came
                Glide.with(getActivity())
                        .load(R.drawable.ic_transaction_history)
                        .into(mPortrait);
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
                // This is where we'll add footer.
                return FOOTER_VIEW;
            }

            return super.getItemViewType(position);
        }
    }
}
