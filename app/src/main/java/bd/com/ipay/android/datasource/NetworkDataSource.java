package bd.com.ipay.android.datasource;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.concurrent.Executor;

import bd.com.ipay.android.utility.NetworkState;

public abstract class NetworkDataSource<Key, Value> extends PageKeyedDataSource<Key, Value> {

	/**
	 * A reference to a function for the retry event
	 */
	@Nullable
	protected OnRetryListener onRetryListener;

	/**
	 * There is no sync on the state because paging will always call loadInitial first then wait
	 * for it to return some success value before calling loadAfter.
	 */
	@NonNull
	private final Executor retryExecutor;

	@NonNull
	private final MutableLiveData<NetworkState> networkState = new MutableLiveData<>();

	@NonNull
	private final MutableLiveData<NetworkState> initialLoad = new MutableLiveData<>();

	public interface OnRetryListener {
		void onRetry();
	}

	public interface OnRefreshListener {
		void onRefresh();
	}

	protected NetworkDataSource(@NonNull Executor retryExecutor) {
		this.retryExecutor = retryExecutor;
	}

	public void retryAllFailed() {
		final OnRetryListener previousRetryListener = onRetryListener;
		onRetryListener = null;
		if (previousRetryListener != null) {
			this.retryExecutor.execute(new Runnable() {
				@Override
				public void run() {
					previousRetryListener.onRetry();
				}
			});
		}
	}

	@NonNull
	public MutableLiveData<NetworkState> getNetworkState() {
		return networkState;
	}

	@NonNull
	public MutableLiveData<NetworkState> getInitialLoad() {
		return initialLoad;
	}
}
