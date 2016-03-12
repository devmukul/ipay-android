package bd.com.ipay.ipayskeleton.DrawerFragments;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import java.util.Date;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.HomeActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.MMModule.UserActivity.UserActivityClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.UserActivity.UserActivityRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.UserActivity.UserActivityResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class ActivityHistoryFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mUserActivityTask = null;
    private UserActivityResponse mUserActivityResponse;

    private String[] activityLogTypes;
    private RecyclerView mActivityLogRecyclerView;
    private ActivityLogAdapter mActivityLogAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<UserActivityClass> userActivityResponsesList;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private LinearLayout eventFilterLayout;
    private LinearLayout dateFilterLayout;

    private CheckBox mChangeProfileCheckBox;
    private CheckBox mSystemEventCheckBox;
    private CheckBox mSecurityChangeCheckBox;
    private CheckBox mVerificationCheckBox;
    private CheckBox mMoneySentCheckBox;
    private CheckBox mMoneyReceivedCheckBox;
    private Button mClearEventFilterButton;

    private EditText mFromDateEditText;
    private EditText mToDateEditText;
    private ImageView mFromDatePicker;
    private ImageView mToDatePicker;
    private Button clearDateFilterButton;
    private Button filterByDateButton;

    private int historyPageCount = 0;
    private Integer type = null;
    private String fromDate = null;
    private String toDate = null;
    private int mYear;
    private int mMonth;
    private int mDay;

    private boolean hasNext = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.activity_history, menu);
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
        View v = inflater.inflate(R.layout.fragment_user_activity, container, false);
        ((HomeActivity) getActivity()).setTitle(R.string.activity_log);

        activityLogTypes = getResources().getStringArray(R.array.activity_log_types);
        mActivityLogRecyclerView = (RecyclerView) v.findViewById(R.id.list_recent_activity_logs);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);

        mActivityLogAdapter = new ActivityLogAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mActivityLogRecyclerView.setLayoutManager(mLayoutManager);
        mActivityLogRecyclerView.setAdapter(mActivityLogAdapter);

        eventFilterLayout = (LinearLayout) v.findViewById(R.id.event_filters_layout);
        dateFilterLayout = (LinearLayout) v.findViewById(R.id.date_filter_layout);
        mClearEventFilterButton = (Button) v.findViewById(R.id.button_clear_filter_event);
        mChangeProfileCheckBox = (CheckBox) v.findViewById(R.id.filter_profile_changes);
        mMoneySentCheckBox = (CheckBox) v.findViewById(R.id.filter_money_out);
        mMoneyReceivedCheckBox = (CheckBox) v.findViewById(R.id.filter_money_in);
        mSecurityChangeCheckBox = (CheckBox) v.findViewById(R.id.filter_security_changes);
        mVerificationCheckBox = (CheckBox) v.findViewById(R.id.filter_verification_changes);
        mSystemEventCheckBox = (CheckBox) v.findViewById(R.id.filter_system_event);

        mFromDateEditText = (EditText) v.findViewById(R.id.fromEditText);
        mToDateEditText = (EditText) v.findViewById(R.id.toEditText);
        mFromDatePicker = (ImageView) v.findViewById(R.id.fromDatePicker);
        mToDatePicker = (ImageView) v.findViewById(R.id.toDatePicker);
        clearDateFilterButton = (Button) v.findViewById(R.id.button_clear_filter_date);
        filterByDateButton = (Button) v.findViewById(R.id.button_filter_date);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    historyPageCount = 0;
                    if (userActivityResponsesList != null) userActivityResponsesList.clear();
                    getUserActivities();
                }
            }
        });

        if (Utilities.isConnectionAvailable(getActivity())) {
            getUserActivities();
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

        return v;
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
                historyPageCount = 0;
                if (userActivityResponsesList != null) userActivityResponsesList.clear();
                getUserActivities();
            }
        });

        filterByDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateFilterLayout.setVisibility(View.GONE);
                fromDate = mFromDateEditText.getText().toString().trim();
                toDate = mToDateEditText.getText().toString().trim();
                historyPageCount = 0;
                if (userActivityResponsesList != null) userActivityResponsesList.clear();
                getUserActivities();
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
                mChangeProfileCheckBox.setChecked(false);
                mVerificationCheckBox.setChecked(false);
                mSecurityChangeCheckBox.setChecked(false);
                mMoneySentCheckBox.setChecked(false);
                mMoneyReceivedCheckBox.setChecked(false);
                mSystemEventCheckBox.setChecked(false);

                historyPageCount = 0;
                if (userActivityResponsesList != null) userActivityResponsesList.clear();
                getUserActivities();
                eventFilterLayout.setVisibility(View.GONE);
            }
        });

        mChangeProfileCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mChangeProfileCheckBox.isChecked()) {
                    type = Constants.ACTIVITY_TYPE_CHANGE_PROFILE;
                    mVerificationCheckBox.setChecked(false);
                    mSecurityChangeCheckBox.setChecked(false);
                    mMoneySentCheckBox.setChecked(false);
                    mMoneyReceivedCheckBox.setChecked(false);
                    mSystemEventCheckBox.setChecked(false);
                } else type = null;

                historyPageCount = 0;
                if (userActivityResponsesList != null) userActivityResponsesList.clear();
                getUserActivities();
                eventFilterLayout.setVisibility(View.GONE);
            }
        });

        mMoneyReceivedCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMoneyReceivedCheckBox.isChecked()) {
                    type = Constants.ACTIVITY_TYPE_MONEY_IN;
                    mVerificationCheckBox.setChecked(false);
                    mSecurityChangeCheckBox.setChecked(false);
                    mChangeProfileCheckBox.setChecked(false);
                    mMoneySentCheckBox.setChecked(false);
                    mSystemEventCheckBox.setChecked(false);
                } else type = null;

                historyPageCount = 0;
                if (userActivityResponsesList != null) userActivityResponsesList.clear();
                getUserActivities();
                eventFilterLayout.setVisibility(View.GONE);
            }
        });

        mMoneySentCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMoneySentCheckBox.isChecked()) {
                    type = Constants.ACTIVITY_TYPE_MONEY_OUT;
                    mVerificationCheckBox.setChecked(false);
                    mSecurityChangeCheckBox.setChecked(false);
                    mMoneyReceivedCheckBox.setChecked(false);
                    mChangeProfileCheckBox.setChecked(false);
                    mSystemEventCheckBox.setChecked(false);
                } else type = null;

                historyPageCount = 0;
                if (userActivityResponsesList != null) userActivityResponsesList.clear();
                getUserActivities();
                eventFilterLayout.setVisibility(View.GONE);
            }
        });

        mSecurityChangeCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSecurityChangeCheckBox.isChecked()) {
                    type = Constants.ACTIVITY_TYPE_CHANGE_SECURITY;
                    mVerificationCheckBox.setChecked(false);
                    mChangeProfileCheckBox.setChecked(false);
                    mMoneyReceivedCheckBox.setChecked(false);
                    mMoneySentCheckBox.setChecked(false);
                    mSystemEventCheckBox.setChecked(false);
                } else type = null;

                historyPageCount = 0;
                if (userActivityResponsesList != null) userActivityResponsesList.clear();
                getUserActivities();
                eventFilterLayout.setVisibility(View.GONE);
            }
        });

        mVerificationCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVerificationCheckBox.isChecked()) {
                    type = Constants.ACTIVITY_TYPE_VERIFICATION;
                    mChangeProfileCheckBox.setChecked(false);
                    mSecurityChangeCheckBox.setChecked(false);
                    mMoneyReceivedCheckBox.setChecked(false);
                    mMoneySentCheckBox.setChecked(false);
                    mSystemEventCheckBox.setChecked(false);
                } else type = null;

                historyPageCount = 0;
                if (userActivityResponsesList != null) userActivityResponsesList.clear();
                getUserActivities();
                eventFilterLayout.setVisibility(View.GONE);
            }
        });

        mSystemEventCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSystemEventCheckBox.isChecked()) {
                    type = Constants.ACTIVITY_TYPE_SYSTEM_EVENT;
                    mChangeProfileCheckBox.setChecked(false);
                    mSecurityChangeCheckBox.setChecked(false);
                    mMoneyReceivedCheckBox.setChecked(false);
                    mMoneySentCheckBox.setChecked(false);
                    mChangeProfileCheckBox.setChecked(false);
                } else type = null;

                historyPageCount = 0;
                if (userActivityResponsesList != null) userActivityResponsesList.clear();
                getUserActivities();
                eventFilterLayout.setVisibility(View.GONE);
            }
        });

    }

    private DatePickerDialog.OnDateSetListener mFromDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear + 1;
                    mDay = dayOfMonth;

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

                    String toDatePicker, toMonthPicker, toYearPicker;
                    if (mDay < 10) toDatePicker = "0" + mDay;
                    else toDatePicker = mDay + "";
                    if (mMonth < 10) toMonthPicker = "0" + mMonth;
                    else toMonthPicker = mMonth + "";
                    toYearPicker = mYear + "";

                    mToDateEditText.setText(toDatePicker + "/" + toMonthPicker + "/" + toYearPicker);
                }
            };

    private void getUserActivities() {
        if (mUserActivityTask != null) {
            return;
        }

        UserActivityRequest mUserActivityRequest = new UserActivityRequest(type, historyPageCount, fromDate, toDate);
        Gson gson = new Gson();
        String json = gson.toJson(mUserActivityRequest);
        mUserActivityTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_USER_ACTIVITIES,
                Constants.BASE_URL_POST_MM + Constants.URL_USER_ACTIVITY, json, getActivity());
        mUserActivityTask.mHttpResponseListener = this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mUserActivityTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            mUserActivityTask.execute((Void) null);
        }
    }

    @Override
    public void httpResponseReceiver(String result) {

        if (result == null) {
            mUserActivityTask = null;
            mSwipeRefreshLayout.setRefreshing(false);
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.fetch_info_failed, Toast.LENGTH_LONG).show();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_GET_USER_ACTIVITIES)) {

            if (resultList.size() > 2) {
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {

                    try {
                        mUserActivityResponse = gson.fromJson(resultList.get(2), UserActivityResponse.class);

                        if (userActivityResponsesList == null || userActivityResponsesList.size() == 0) {
                            userActivityResponsesList = mUserActivityResponse.getActivities();
                        } else {
                            List<UserActivityClass> tempUserActivityResponsesList;
                            tempUserActivityResponsesList = mUserActivityResponse.getActivities();
                            userActivityResponsesList.addAll(tempUserActivityResponsesList);
                        }

                        hasNext = mUserActivityResponse.isHasNext();
                        mActivityLogAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.user_activity_get_failed, Toast.LENGTH_LONG).show();
                    }

                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.user_activity_get_failed, Toast.LENGTH_LONG).show();
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.user_activity_get_failed, Toast.LENGTH_LONG).show();

            mSwipeRefreshLayout.setRefreshing(false);
            mUserActivityTask = null;
        }
    }

    public class ActivityLogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int FOOTER_VIEW = 1;

        public ActivityLogAdapter() {
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView mTransactionType;
            private RoundedImageView mPortrait;
            private TextView mTransactionDescription;
            private TextView mTime;
            private TextView loadMoreTextView;

            public ViewHolder(final View itemView) {
                super(itemView);

                mTransactionType = (TextView) itemView.findViewById(R.id.transaction_type);
                mTransactionDescription = (TextView) itemView.findViewById(R.id.activity_description);
                mTime = (TextView) itemView.findViewById(R.id.time);
                mPortrait = (RoundedImageView) itemView.findViewById(R.id.portrait);
                loadMoreTextView = (TextView) itemView.findViewById(R.id.load_more);
            }

            public void bindView(int pos) {
                String type = activityLogTypes[userActivityResponsesList.get(pos).getType()];
                String description = userActivityResponsesList.get(pos).getDescription();
                String time = new SimpleDateFormat("EEE, MMM d, ''yy, H:MM a").format(userActivityResponsesList.get(pos).getTime());
                mTransactionType.setText(type);
                mTransactionDescription.setText(description);
                mTime.setText(time);

                // Set icon for activity type
                if (userActivityResponsesList.get(pos).getType() == 0) {
                    Glide.with(getActivity())
                            .load(R.drawable.ic_face_black_24dp)
                            .into(mPortrait);
                } else if (userActivityResponsesList.get(pos).getType() == 1) {
                    Glide.with(getActivity())
                            .load(R.drawable.ic_activity_cash_in)
                            .into(mPortrait);
                } else if (userActivityResponsesList.get(pos).getType() == 2) {
                    Glide.with(getActivity())
                            .load(R.drawable.ic_cash_activity)
                            .into(mPortrait);
                } else if (userActivityResponsesList.get(pos).getType() == 3) {
                    Glide.with(getActivity())
                            .load(R.drawable.ic_system_activity)
                            .into(mPortrait);
                } else if (userActivityResponsesList.get(pos).getType() == 4) {
                    Glide.with(getActivity())
                            .load(R.drawable.ic_settings)
                            .into(mPortrait);
                } else if (userActivityResponsesList.get(pos).getType() == 5) {
                    Glide.with(getActivity())
                            .load(R.drawable.ic_face_black_24dp)
                            .into(mPortrait);
                }
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
                            getUserActivities();
                        }
                    }
                });

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

            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_activity_log, parent, false);

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
            if (userActivityResponsesList != null)
                return userActivityResponsesList.size() + 1;
            else return 0;
        }

        @Override
        public int getItemViewType(int position) {

            if (position == userActivityResponsesList.size()) {
                // This is where we'll add footer.
                return FOOTER_VIEW;
            }

            return super.getItemViewType(position);
        }
    }
}
