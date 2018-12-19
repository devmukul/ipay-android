package bd.com.ipay.android.model;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

import bd.com.ipay.android.datasource.NetworkDataSource;
import bd.com.ipay.android.utility.NetworkState;

public class Listing<Model> {
	@NonNull
	private final LiveData<PagedList<Model>> pagedList;
	@NonNull
	private final LiveData<NetworkState> initialLoadState;
	@NonNull
	private final LiveData<NetworkState> networkState;
	@NonNull
	private final LiveData<NetworkState> refreshState;
	@NonNull
	private final NetworkDataSource.OnRefreshListener refresh;
	@NonNull
	private final NetworkDataSource.OnRetryListener retry;

	public Listing(@NonNull LiveData<PagedList<Model>> pagedList,
	               @NonNull LiveData<NetworkState> initialLoadState,
	               @NonNull LiveData<NetworkState> networkState,
	               @NonNull LiveData<NetworkState> refreshState,
	               @NonNull NetworkDataSource.OnRefreshListener refresh,
	               @NonNull NetworkDataSource.OnRetryListener retry) {
		this.pagedList = pagedList;
		this.initialLoadState = initialLoadState;
		this.networkState = networkState;
		this.refreshState = refreshState;
		this.refresh = refresh;
		this.retry = retry;
	}

	@NonNull
	public final LiveData<PagedList<Model>> getPagedList() {
		return this.pagedList;
	}

	@NonNull
	public LiveData<NetworkState> getInitialLoadState() {
		return initialLoadState;
	}

	@NonNull
	public final LiveData<NetworkState> getNetworkState() {
		return this.networkState;
	}

	@NonNull
	public final LiveData<NetworkState> getRefreshState() {
		return this.refreshState;
	}

	@NonNull
	public final NetworkDataSource.OnRefreshListener getRefresh() {
		return this.refresh;
	}

	@NonNull
	public final NetworkDataSource.OnRetryListener getRetry() {
		return this.retry;
	}
}
