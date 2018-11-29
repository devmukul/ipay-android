package bd.com.ipay.ipayskeleton.HomeFragments.TransactionHistoryFragments;

import android.app.Activity;
import android.app.Application;
import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import bd.com.ipay.android.utility.TransactionHistoryType;
import bd.com.ipay.android.viewmodel.TransactionHistoryRepositoryViewModel;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.SentReceivedRequestPaymentReviewActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.SentReceivedRequestReviewActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.TransactionDetailsActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.CustomView.CustomSwipeRefreshLayout;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory.TransactionHistory;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory.TransactionHistoryPendingRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory.TransactionHistoryResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ACLManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.ContactSearchHelper;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.MyApplication;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;
import bd.com.ipay.ipayskeleton.ViewModel.ViewModelFactory;

public class TransactionHistoryPendingFragment extends ProgressFragment implements
		HttpResponseListener, PopupMenu.OnMenuItemClickListener {
	private HttpRequestGetAsyncTask mTransactionHistoryTask = null;

	private RecyclerView mTransactionHistoryRecyclerView;
	private TransactionHistoryAdapter mTransactionHistoryAdapter;
	private LinearLayoutManager mLayoutManager;
	private List<TransactionHistory> userTransactionHistories;
	private CustomSwipeRefreshLayout mSwipeRefreshLayout;

	private RelativeLayout serviceFilterLayout;
	private LinearLayout dateFilterLayout;

	private ImageButton filterOptionPopupMenuButton;
	private ImageButton clearAllFilterButton;
	private Button removeFilterButton;

	private Button mFromDateButton;
	private Button mToDateButton;
	private Button filterByDateButton;
	private TextView mEmptyListTextView;

	private TextView mFilterTitle;

	private int historyPageCount = 1;
	private Integer type = null;
	private Calendar fromDate = null;
	private Calendar toDate = null;

	private boolean hasNext = false;
	private boolean isLoading = false;
	private boolean clearListAfterLoading;
	private boolean mIsScrolled = false;
	private int mTotalItemCount = 0;
	private int mPastVisibleItems;
	private int mVisibleItem;

	private PopupMenu popupMenu;

	private final int REQUEST_MONEY_REVIEW_REQUEST = 101;
	private final int REQUEST_PAYMENT_REVIEW_REQUEST = 102;

	private SparseIntArray mCheckBoxTypeMap;

	private TransactionHistoryRepositoryViewModel transactionHistoryRepositoryViewModel;

	private final BroadcastReceiver transactionHistoryBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			refreshTransactionHistory();
		}
	};

	private final View.OnClickListener buttonClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
				case R.id.filter_option_popup_menu_button:
					if (popupMenu != null) {
						popupMenu.show();
					}
					break;
				case R.id.clear_filter_date_button:
				case R.id.clear_all_filter_button:
				case R.id.remove_filter_button:
					clearDateFilters();
					clearServiceFilters();
					setContentShown(false);
					refreshTransactionHistory();
					mFilterTitle.setText(getString(R.string.pending_transaction_list));

					mFilterTitle.setVisibility(View.VISIBLE);
					filterOptionPopupMenuButton.setVisibility(View.VISIBLE);
					clearAllFilterButton.setVisibility(View.GONE);
					removeFilterButton.setVisibility(View.GONE);
					break;
				case R.id.service_filter_clear_button:
					clearServiceFilters();
					setContentShown(false);
					refreshTransactionHistory();
					break;
			}
		}
	};

	private Tracker mTracker;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Application application = MyApplication.getMyApplicationInstance();
		if (getActivity() != null) {
			transactionHistoryRepositoryViewModel = ViewModelProviders.of(getActivity(),
					ViewModelFactory.getTransactionHistoryViewModelFactory(
							TransactionHistoryType.PENDING, application))
					.get(TransactionHistoryRepositoryViewModel.class);
			mTracker = Utilities.getTracker(getActivity());
		} else {
			transactionHistoryRepositoryViewModel = ViewModelProviders.of(this,
					ViewModelFactory.getTransactionHistoryViewModelFactory(
							TransactionHistoryType.PENDING, application))
					.get(TransactionHistoryRepositoryViewModel.class);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (getActivity() != null) {

			LocalBroadcastManager.getInstance(getActivity()).registerReceiver(transactionHistoryBroadcastReceiver,
					new IntentFilter(Constants.PENDING_TRANSACTION_HISTORY_UPDATE_BROADCAST));
			Utilities.sendScreenTracker(mTracker, getString(R.string.screen_name_transaction_history_pending));
		} else {

		}

	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_pending_transaction_history, container, false);
		if (getActivity() != null)
			getActivity().setTitle(R.string.transaction_history);

		initializeViews(view);
		setupViewsAndActions();
		handleBackPressWhenFilterIsOn(view);


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
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		final RadioGroup radioGroup = view.findViewById(R.id.filter_radio_group);

		final Button serviceFilterClearButton = view.findViewById(R.id.service_filter_clear_button);
		final Button clearDateFilterButton = view.findViewById(R.id.clear_filter_date_button);
		filterOptionPopupMenuButton = view.findViewById(R.id.filter_option_popup_menu_button);
		clearAllFilterButton = view.findViewById(R.id.clear_all_filter_button);
		removeFilterButton = view.findViewById(R.id.remove_filter_button);

		if (getActivity() != null) {
			popupMenu = new PopupMenu(getActivity(), filterOptionPopupMenuButton);
			popupMenu.getMenuInflater().inflate(R.menu.activity_transaction_history, popupMenu.getMenu());
			popupMenu.setOnMenuItemClickListener(TransactionHistoryPendingFragment.this);
		}

		serviceFilterClearButton.setOnClickListener(buttonClickListener);
		filterOptionPopupMenuButton.setOnClickListener(buttonClickListener);
		clearAllFilterButton.setOnClickListener(buttonClickListener);
		removeFilterButton.setOnClickListener(buttonClickListener);
		clearDateFilterButton.setOnClickListener(buttonClickListener);

		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				filterOptionPopupMenuButton.setVisibility(View.INVISIBLE);
				clearAllFilterButton.setVisibility(View.INVISIBLE);
				removeFilterButton.setVisibility(View.VISIBLE);

				clearDateFilters();
				type = mCheckBoxTypeMap.get(checkedId);
				setContentShown(false);
				refreshTransactionHistory();
				serviceFilterLayout.setVisibility(View.GONE);
				radioGroup.setOnCheckedChangeListener(null);
				radioGroup.clearCheck();
				radioGroup.setOnCheckedChangeListener(this);
			}
		});
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getPendingTransactionHistory();
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
		super.onDestroyView();

		if (getActivity() != null)
			LocalBroadcastManager.getInstance(getActivity())
					.unregisterReceiver(transactionHistoryBroadcastReceiver);
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

		if (HttpErrorHandler.isErrorFound(result, getContext(), null)) {
			mTransactionHistoryTask = null;
			setContentShown(true);
			return;
		}

		Gson gson = new Gson();

		if (result.getApiCommand().equals(Constants.COMMAND_GET_PENDING_TRANSACTION_HISTORY)) {

			if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

				try {
					TransactionHistoryResponse mTransactionHistoryResponse = gson.fromJson(result.getJsonString(), TransactionHistoryResponse.class);
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

	@Override
	public boolean onMenuItemClick(MenuItem item) {

		switch (item.getItemId()) {
			case R.id.action_filter_by_date:
				mFilterTitle.setText(getString(R.string.filter_by_date));

				dateFilterLayout.setVisibility(View.VISIBLE);
				serviceFilterLayout.setVisibility(View.GONE);
				mFilterTitle.setVisibility(View.VISIBLE);
				filterOptionPopupMenuButton.setVisibility(View.GONE);
				clearAllFilterButton.setVisibility(View.GONE);
				removeFilterButton.setVisibility(View.VISIBLE);
				return true;
			case R.id.action_filter_by_service:
				mFilterTitle.setText(getString(R.string.filter_by_service));

				dateFilterLayout.setVisibility(View.GONE);
				serviceFilterLayout.setVisibility(View.VISIBLE);
				mFilterTitle.setVisibility(View.VISIBLE);
				filterOptionPopupMenuButton.setVisibility(View.GONE);
				clearAllFilterButton.setVisibility(View.GONE);
				removeFilterButton.setVisibility(View.VISIBLE);
				return true;
			default:
				mFilterTitle.setText(getString(R.string.pending_transaction_list));

				dateFilterLayout.setVisibility(View.GONE);
				serviceFilterLayout.setVisibility(View.GONE);
				mFilterTitle.setVisibility(View.VISIBLE);
				filterOptionPopupMenuButton.setVisibility(View.VISIBLE);
				clearAllFilterButton.setVisibility(View.GONE);
				removeFilterButton.setVisibility(View.GONE);
				return false;
		}
	}

	private void initializeViews(View view) {
		mEmptyListTextView = view.findViewById(R.id.empty_list_text);
		mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
		mTransactionHistoryRecyclerView = view.findViewById(R.id.list_transaction_history);
		serviceFilterLayout = view.findViewById(R.id.service_filters_layout);
		dateFilterLayout = view.findViewById(R.id.date_filter_layout);

		mFromDateButton = view.findViewById(R.id.fromButton);
		mToDateButton = view.findViewById(R.id.toButton);
		filterByDateButton = view.findViewById(R.id.button_filter_date);

		mFilterTitle = view.findViewById(R.id.filter_title);
	}

	private void setupViewsAndActions() {
		setupRecyclerView();
		setupCheckboxTypeMap();
		setActionsForDateFilter();
		implementScrollListener();
	}

	private void setupRecyclerView() {
		mTransactionHistoryAdapter = new TransactionHistoryAdapter();
		mLayoutManager = new LinearLayoutManager(getActivity());
		mTransactionHistoryRecyclerView.setLayoutManager(mLayoutManager);
		mTransactionHistoryRecyclerView.setAdapter(mTransactionHistoryAdapter);
	}

	private void setupCheckboxTypeMap() {
		mCheckBoxTypeMap = new SparseIntArray();
		mCheckBoxTypeMap.put(R.id.request_money_filter_radio_button, Constants.TRANSACTION_HISTORY_REQUEST_MONEY);
		mCheckBoxTypeMap.put(R.id.add_money_by_bank_filter_radio_button, Constants.TRANSACTION_HISTORY_ADD_MONEY_BY_BANK);
		mCheckBoxTypeMap.put(R.id.add_money_by_credit_filter_radio_button, Constants.TRANSACTION_HISTORY_ADD_MONEY_BY_CREDIT_OR_DEBIT_CARD);
		mCheckBoxTypeMap.put(R.id.withdraw_money_filter_radio_button, Constants.TRANSACTION_HISTORY_WITHDRAW_MONEY);
		mCheckBoxTypeMap.put(R.id.top_up_filter_radio_button, Constants.TRANSACTION_HISTORY_TOP_UP);
		mCheckBoxTypeMap.put(R.id.request_payment_filter_radio_button, Constants.TRANSACTION_HISTORY_REQUEST_PAYMENT);
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

	private void refreshTransactionHistory() {
		historyPageCount = 1;
		clearListAfterLoading = true;
		getPendingTransactionHistory();
	}

	private void setActionsForDateFilter() {
		filterByDateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!TextUtils.isEmpty(mFromDateButton.getText())) {
					dateFilterLayout.setVisibility(View.GONE);
					serviceFilterLayout.setVisibility(View.GONE);
					mFilterTitle.setVisibility(View.VISIBLE);
					filterOptionPopupMenuButton.setVisibility(View.GONE);
					clearAllFilterButton.setVisibility(View.GONE);
					removeFilterButton.setVisibility(View.VISIBLE);

					clearServiceFilters();
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
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
					final Date fromDate;
					try {
						fromDate = sdf.parse(mFromDateButton.getText().toString().trim());
						calendar.setTime(fromDate);
						if (getActivity() != null) {
							DatePickerDialog dpd = new DatePickerDialog(getActivity(), mFromDateSetListener, calendar.get(Calendar.YEAR)
									, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
							dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
							dpd.show();
						}

					} catch (ParseException e) {
						e.printStackTrace();
					}
				} else {
					if (getActivity() != null) {
						DatePickerDialog dpd = new DatePickerDialog(getActivity(), mFromDateSetListener, calendar.get(Calendar.YEAR)
								, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
						dpd.show();
					}
				}
			}
		});

		mToDateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
				final Date fromDate;
				try {
					fromDate = sdf.parse(mFromDateButton.getText().toString().trim());
					if (getActivity() != null) {
						DatePickerDialog dpd = new DatePickerDialog(getActivity(), mToDateSetListener, Constants.STARTING_YEAR
								, Constants.STARTING_MONTH, Constants.STARTING_DATE);
						dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
						dpd.getDatePicker().setMinDate(fromDate.getTime());
						dpd.show();

					}
				} catch (ParseException e) {
					Toast.makeText(getActivity(), R.string.select_from_date_first, Toast.LENGTH_LONG).show();
				}
			}
		});

	}

	private void clearServiceFilters() {
		type = null;
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

					String fromDateStr = String.format(Locale.US, Constants.DATE_FORMAT, dayOfMonth, monthOfYear + 1, year);

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

					String toDateStr = String.format(Locale.US, Constants.DATE_FORMAT, dayOfMonth, monthOfYear + 1, year);

					mToDateButton.setText(toDateStr);
				}
			};

	private void getPendingTransactionHistory() {
		if (mTransactionHistoryTask != null) {
			return;
		}

		String url = TransactionHistoryPendingRequest.generateUri(type,
				fromDate, toDate, historyPageCount, Constants.ACTIVITY_LOG_COUNT);

		mTransactionHistoryTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_PENDING_TRANSACTION_HISTORY,
				url, getActivity(), false);
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

	private void implementScrollListener() {
		mTransactionHistoryRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

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
				mTotalItemCount = mLayoutManager.getItemCount();
				mPastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();
				if (mIsScrolled
						&& (mVisibleItem + mPastVisibleItems) == mTotalItemCount && hasNext && mTransactionHistoryTask == null) {
					isLoading = true;
					mIsScrolled = false;
					historyPageCount = historyPageCount + 1;
					mTransactionHistoryAdapter.notifyDataSetChanged();
					getPendingTransactionHistory();
				}

			}

		});

	}

	private class TransactionHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

		private static final int FOOTER_VIEW = 1;

		public class ViewHolder extends RecyclerView.ViewHolder {
			private final TextView mTransactionDescriptionView;
			private final TextView mTimeView;
			private final TextView mReceiverView;
			private final TextView mNetAmountView;
			private final ImageView mOtherImageView;
			private final ProfileImageView mProfileImageView;
			private ImageView mStatusIconView;

			public ViewHolder(final View itemView) {
				super(itemView);

				mTransactionDescriptionView = itemView.findViewById(R.id.activity_description);
				mTimeView = itemView.findViewById(R.id.time);
				mReceiverView = itemView.findViewById(R.id.receiver);
				mNetAmountView = itemView.findViewById(R.id.net_amount);
				mProfileImageView = itemView.findViewById(R.id.profile_picture);
				mOtherImageView = itemView.findViewById(R.id.other_image);
				mStatusIconView = itemView.findViewById(R.id.status_description_icon);
			}

			public void bindView(int pos) {
				final TransactionHistory transactionHistory = userTransactionHistories.get(pos);
				final String description = transactionHistory.getShortDescription();
				final String receiver = transactionHistory.getReceiver();
				String responseTime = Utilities.formatDayMonthYear(transactionHistory.getTime());
				final String netAmountWithSign = String.valueOf(Utilities.formatTakaFromString(transactionHistory.getNetAmountFormatted()));
				final int serviceId = transactionHistory.getServiceId();
				final String outletName = transactionHistory.getOutletName();

				mTransactionDescriptionView.setText(description);
				if (receiver != null && !receiver.equals("")) {
					mReceiverView.setVisibility(View.VISIBLE);
					if (!TextUtils.isEmpty(outletName)) {
						mReceiverView.setText(String.format("%s (%s)", receiver, outletName));
					} else {
						mReceiverView.setText(receiver);
					}
				} else mReceiverView.setVisibility(View.GONE);

				mNetAmountView.setText(netAmountWithSign);
				if (DateUtils.isToday(transactionHistory.getTime())) {
					responseTime = "Today, " + Utilities.formatTimeOnly(transactionHistory.getTime());
				}

				mTimeView.setText(responseTime);

				mStatusIconView.setImageDrawable(getResources().getDrawable(R.drawable.pending));

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
					public void onClick(View v) {
						if (!mSwipeRefreshLayout.isRefreshing()) {
							if (serviceId == Constants.TRANSACTION_HISTORY_REQUEST_MONEY)
								launchRequestMoneyReviewPage(transactionHistory);
							else if (serviceId == Constants.TRANSACTION_HISTORY_REQUEST_PAYMENT)
								launchRequestPaymentReviewPage(transactionHistory);
							else {
								if (ACLManager.hasServicesAccessibility(ServiceIdConstants.TRANSACTION_DETAILS)) {
									Intent intent = new Intent(getActivity(), TransactionDetailsActivity.class);
									intent.putExtra(Constants.TRANSACTION_DETAILS, transactionHistory);
									startActivity(intent);
								} else {
									DialogUtils.showServiceNotAllowedDialog(getContext());
								}
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

				mLoadMoreTextView = itemView.findViewById(R.id.load_more);
				mLoadMoreProgressBar = itemView.findViewById(R.id.progress_bar);
			}

			void bindViewFooter() {
				setItemVisibilityOfFooterView();
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

		@NonNull
		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

			View v;

			if (viewType == FOOTER_VIEW) {
				v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_load_more_footer, parent, false);

				return new FooterViewHolder(v);
			}

			v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_transaction_history_pending, parent, false);

			return new NormalViewHolder(v);
		}

		@Override
		public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
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
	}

	private void launchRequestMoneyReviewPage(TransactionHistory transactionHistory) {
		Intent intent = new Intent(getActivity(), SentReceivedRequestReviewActivity.class);
		intent.putExtra(Constants.AMOUNT, new BigDecimal(transactionHistory.getAmount()));
		intent.putExtra(Constants.RECEIVER_MOBILE_NUMBER,
				ContactEngine.formatMobileNumberBD(transactionHistory.getAdditionalInfo().getNumber()));

		intent.putExtra(Constants.DESCRIPTION_TAG, transactionHistory.getPurpose());
		intent.putExtra(Constants.TRANSACTION_ID, transactionHistory.getTransactionID());
		intent.putExtra(Constants.NAME, transactionHistory.getReceiver());
		intent.putExtra(Constants.PHOTO_URI, Constants.BASE_URL_FTP_SERVER + transactionHistory.getAdditionalInfo().getUserProfilePic());
		intent.putExtra(Constants.SWITCHED_FROM_TRANSACTION_HISTORY, true);
		intent.putExtra(Constants.IS_IN_CONTACTS,
				new ContactSearchHelper(getActivity()).searchMobileNumber(transactionHistory.getAdditionalInfo().getNumber()));

		if (transactionHistory.getType().equalsIgnoreCase(Constants.TRANSACTION_TYPE_CREDIT)) {
			intent.putExtra(Constants.REQUEST_TYPE, Constants.REQUEST_TYPE_SENT_REQUEST);
		}
		startActivityForResult(intent, REQUEST_MONEY_REVIEW_REQUEST);
	}

	private void launchRequestPaymentReviewPage(TransactionHistory transactionHistory) {
		Intent intent = new Intent(getActivity(), SentReceivedRequestPaymentReviewActivity.class);
		intent.putExtra(Constants.AMOUNT, new BigDecimal(transactionHistory.getAmount()));
		intent.putExtra(Constants.RECEIVER_MOBILE_NUMBER,
				ContactEngine.formatMobileNumberBD(transactionHistory.getAdditionalInfo().getNumber()));

		intent.putExtra(Constants.DESCRIPTION_TAG, transactionHistory.getPurpose());
		intent.putExtra(Constants.TRANSACTION_ID, transactionHistory.getTransactionID());
		intent.putExtra(Constants.NAME, transactionHistory.getReceiver());
		intent.putExtra(Constants.STATUS, Constants.HTTP_RESPONSE_STATUS_PROCESSING);
		intent.putExtra(Constants.PHOTO_URI, Constants.BASE_URL_FTP_SERVER + transactionHistory.getAdditionalInfo().getUserProfilePic());
		intent.putExtra(Constants.SWITCHED_FROM_TRANSACTION_HISTORY, true);
		intent.putExtra(Constants.IS_IN_CONTACTS,
				new ContactSearchHelper(getActivity()).searchMobileNumber(transactionHistory.getAdditionalInfo().getNumber()));

		if (transactionHistory.getType().equalsIgnoreCase(Constants.TRANSACTION_TYPE_CREDIT)) {
			intent.putExtra(Constants.REQUEST_TYPE, Constants.REQUEST_TYPE_SENT_REQUEST);
		}
		startActivityForResult(intent, REQUEST_PAYMENT_REVIEW_REQUEST);
	}
}

