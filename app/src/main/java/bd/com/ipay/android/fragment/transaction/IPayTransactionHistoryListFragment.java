package bd.com.ipay.android.fragment.transaction;

import android.app.Application;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Locale;

import bd.com.ipay.android.adapter.TransactionHistoryAdapter;
import bd.com.ipay.android.adapter.viewholder.OnItemClickListener;
import bd.com.ipay.android.datasource.NetworkDataSource;
import bd.com.ipay.android.fragment.IPayProgressFragment;
import bd.com.ipay.android.utility.NetworkState;
import bd.com.ipay.android.utility.TransactionHistoryType;
import bd.com.ipay.android.viewmodel.RepositoryArguments;
import bd.com.ipay.android.viewmodel.TransactionHistoryRepositoryArguments;
import bd.com.ipay.android.viewmodel.TransactionHistoryRepositoryViewModel;
import bd.com.ipay.android.viewmodel.TransactionHistoryViewModel;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.TransactionDetailsActivity;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory.TransactionHistory;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.MyApplication;
import bd.com.ipay.ipayskeleton.ViewModel.ViewModelFactory;

public class IPayTransactionHistoryListFragment extends IPayProgressFragment {

	private TransactionHistoryViewModel transactionHistoryViewModel;
	private TransactionHistoryRepositoryViewModel transactionHistoryRepositoryViewModel;
	private TransactionHistoryType transactionHistoryType;

	private TransactionHistoryAdapter transactionHistoryAdapter;

	private SwipeRefreshLayout swipeRefreshLayout;

	private final Handler handler = new Handler();

	private final Runnable isTopListItemVisibleCheckRunnable = new Runnable() {
		@Override
		public void run() {
			if (isAdded()) {
				if (linearLayoutManager != null) {
					if (linearLayoutManager.findFirstVisibleItemPosition() > 0) {
						toListTopButton.setVisibility(View.VISIBLE);
					}
				}
			}
		}
	};

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Application application = MyApplication.getMyApplicationInstance();

		if (getArguments() != null) {
			transactionHistoryType = (TransactionHistoryType) getArguments()
					.getSerializable(IPayTransactionHistoryFragment.TRANSACTION_HISTORY_TYPE_KEY);
		}

		if (transactionHistoryType == null) {
			transactionHistoryType = TransactionHistoryType.COMPLETED;
		}

		if (getActivity() != null) {
			transactionHistoryRepositoryViewModel = ViewModelProviders.of(getActivity()
					, ViewModelFactory.getTransactionHistoryViewModelFactory(
							transactionHistoryType, application))
					.get(getTransactionHistoryRepositoryViewModelKey(transactionHistoryType),
							TransactionHistoryRepositoryViewModel.class);

			transactionHistoryViewModel = ViewModelProviders.of(getActivity())
					.get(getTransactionHistoryViewModelKey(transactionHistoryType),
							TransactionHistoryViewModel.class);
		} else {
			transactionHistoryRepositoryViewModel = ViewModelProviders.of(this
					, ViewModelFactory.getTransactionHistoryViewModelFactory(
							transactionHistoryType, application))
					.get(getTransactionHistoryRepositoryViewModelKey(transactionHistoryType),
							TransactionHistoryRepositoryViewModel.class);

			if (getParentFragment() != null) {
				transactionHistoryViewModel = ViewModelProviders.of(getParentFragment())
						.get(getTransactionHistoryViewModelKey(transactionHistoryType),
								TransactionHistoryViewModel.class);
			} else if (getFragmentManager() != null) {
				getFragmentManager().beginTransaction().remove(this).commit();
			}
		}
		transactionHistoryAdapter = new TransactionHistoryAdapter(onRetryListener,
				onItemClickListener);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!transactionHistoryRepositoryViewModel.getPagedData().hasActiveObservers()) {
			transactionHistoryRepositoryViewModel.getPagedData().removeObservers(this);
			transactionHistoryRepositoryViewModel.getPagedData().observe(this,
					transactionHistoryListObserver);
		}
		if (!transactionHistoryRepositoryViewModel.getRefreshState().hasActiveObservers()) {
			transactionHistoryRepositoryViewModel.getRefreshState().removeObservers(this);
			transactionHistoryRepositoryViewModel.getRefreshState().observe(this,
					transactionHistoryRefreshObserver);
		}
		if (!transactionHistoryRepositoryViewModel.getNetworkState().hasActiveObservers()) {
			transactionHistoryRepositoryViewModel.getNetworkState().removeObservers(this);
			transactionHistoryRepositoryViewModel.getNetworkState().observe(this,
					transactionNetworkStateObserver);
		}
		if (!transactionHistoryViewModel.getTransactionHistorySearchLiveData().hasActiveObservers()) {
			transactionHistoryViewModel.getTransactionHistorySearchLiveData().removeObservers(this);
			transactionHistoryViewModel.getTransactionHistorySearchLiveData().observe(this,
					transactionHistorySearchLiveData);
		}
	}

	@NonNull
	@Override
	public View onCreateContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
	                                @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_ipay_transaction_history_list,
				container, false);
	}

	private String getTransactionHistoryViewModelKey(
			TransactionHistoryType transactionHistoryType) {
		return String.format(Locale.US, "%s:%s", transactionHistoryType.toString(),
				TransactionHistoryViewModel.class.getCanonicalName());
	}

	private String getTransactionHistoryRepositoryViewModelKey(
			TransactionHistoryType transactionHistoryType) {
		return String.format(Locale.US, "%s:%s", transactionHistoryType.toString(),
				TransactionHistoryRepositoryViewModel.class.getCanonicalName());
	}

	private final OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(int position, View view) {
			Intent intent = new Intent(getActivity(), TransactionDetailsActivity.class);
			intent.putExtra(Constants.TRANSACTION_DETAILS,
					transactionHistoryRepositoryViewModel.getItem(position));
			startActivity(intent);
		}
	};
	private LinearLayoutManager linearLayoutManager;
	private Button toListTopButton;

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setProgressText(R.string.please_wait);
		swipeRefreshLayout = view.findViewById(R.id.transaction_history_refresh_layout);
		final RecyclerView transactionHistoryRecyclerView
				= view.findViewById(R.id.transaction_history_recycler_view);
		toListTopButton = view.findViewById(R.id.to_list_top_button);

		toListTopButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				toListTopButton.setVisibility(View.GONE);
				linearLayoutManager.smoothScrollToPosition(transactionHistoryRecyclerView,
						new RecyclerView.State(), 0);
			}
		});

		linearLayoutManager = (LinearLayoutManager) transactionHistoryRecyclerView.getLayoutManager();
		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				transactionHistoryRepositoryViewModel.refreshData();
			}
		});

		transactionHistoryRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
			}

			@Override
			public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				if (toListTopButton.getVisibility() == View.VISIBLE) {
					if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
						toListTopButton.setVisibility(View.GONE);
					}
				}
			}
		});

		transactionHistoryRecyclerView.setAdapter(transactionHistoryAdapter);

		if (transactionHistoryRepositoryViewModel.getPagedData().getValue() != null &&
				!transactionHistoryRepositoryViewModel.getPagedData().getValue().isEmpty()) {
			transactionHistoryAdapter.submitList(transactionHistoryRepositoryViewModel
					.getPagedData().getValue());
			showContentView();
		} else {
			transactionHistoryRepositoryViewModel.fetchData(new TransactionHistoryRepositoryArguments(
					RepositoryArguments.DEFAULT_PAGE_SIZE,
					null, null, null, null));
			hideContentView();
		}
	}

	private final Observer<PagedList<TransactionHistory>> transactionHistoryListObserver =
			new Observer<PagedList<TransactionHistory>>() {
				@Override
				public void onChanged(@Nullable PagedList<TransactionHistory>
						                      transactionHistories) {
					swipeRefreshLayout.setRefreshing(false);
					transactionHistoryAdapter.submitList(transactionHistories);
					handler.postDelayed(isTopListItemVisibleCheckRunnable, 500);
					showContentView();
				}
			};

	private final NetworkDataSource.OnRetryListener onRetryListener
			= new NetworkDataSource.OnRetryListener() {

		@Override
		public void onRetry() {
			transactionHistoryRepositoryViewModel.retry();
		}
	};

	private final Observer<NetworkState> transactionHistoryRefreshObserver
			= new Observer<NetworkState>() {
		@Override
		public void onChanged(@Nullable NetworkState networkState) {
			if (NetworkState.LOADED.equals(networkState)) {
				swipeRefreshLayout.setRefreshing(false);
			} else if (swipeRefreshLayout.isRefreshing()) {
				swipeRefreshLayout.setRefreshing(
						NetworkState.LOADING.equals(networkState) ||
								NetworkState.REFRESHING.equals(networkState));
			}
		}
	};

	private final Observer<NetworkState> transactionNetworkStateObserver
			= new Observer<NetworkState>() {
		@Override
		public void onChanged(@Nullable NetworkState networkState) {
			transactionHistoryAdapter.setNetworkSate(networkState);
		}
	};

	private final Observer<TransactionHistoryRepositoryArguments> transactionHistorySearchLiveData
			= new Observer<TransactionHistoryRepositoryArguments>() {
		@Override
		public void onChanged(@Nullable TransactionHistoryRepositoryArguments arguments) {
			if (arguments != null && !transactionHistoryRepositoryViewModel
					.isSameRepositoryArguments(arguments)) {
				swipeRefreshLayout.setRefreshing(true);
				transactionHistoryRepositoryViewModel.fetchData(arguments);
			}
		}
	};
}
