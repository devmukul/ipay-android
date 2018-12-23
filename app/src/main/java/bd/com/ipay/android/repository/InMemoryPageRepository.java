package bd.com.ipay.android.repository;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import java.util.concurrent.Executor;

import bd.com.ipay.android.datasource.NetworkDataSource;
import bd.com.ipay.android.datasource.factory.NetworkDataSourceFactory;
import bd.com.ipay.android.model.Listing;
import bd.com.ipay.android.utility.NetworkState;

public abstract class InMemoryPageRepository<Key, Model> {
	private final NetworkDataSourceFactory<Key, Model> dataSourceFactory;
	private final LiveData<PagedList<Model>> livePagedList;

	InMemoryPageRepository(final int pageSize,
	                       final NetworkDataSourceFactory<Key, Model> dataSourceFactory,
	                       Executor networkExecutor) {
		PagedList.Config pagedListConfig = new PagedList.Config.Builder()
				.setInitialLoadSizeHint(pageSize)
				.setPageSize(pageSize)
				.setEnablePlaceholders(true)
				.build();
		this.dataSourceFactory = dataSourceFactory;
		livePagedList = new LivePagedListBuilder<>(this.dataSourceFactory, pagedListConfig)
				.setFetchExecutor(networkExecutor).build();
	}

	public Listing<Model> getPagedListing() {
		return new Listing<>(livePagedList,
				Transformations.switchMap(dataSourceFactory.getNetworkDataSourceMutableLiveData(),
						new Function<NetworkDataSource<Key, Model>, LiveData<NetworkState>>() {
							@Override
							public LiveData<NetworkState> apply(NetworkDataSource<Key, Model> input) {
								return input.getInitialLoad();
							}
						}),
				Transformations.switchMap(dataSourceFactory.getNetworkDataSourceMutableLiveData(),
						new Function<NetworkDataSource<Key, Model>, LiveData<NetworkState>>() {
							@Override
							public LiveData<NetworkState> apply(NetworkDataSource<Key, Model> input) {
								return input.getNetworkState();
							}
						}),
				Transformations.switchMap(dataSourceFactory.getNetworkDataSourceMutableLiveData(),
						new Function<NetworkDataSource<Key, Model>, LiveData<NetworkState>>() {
							@Override
							public LiveData<NetworkState> apply(NetworkDataSource<Key, Model> input) {
								return input.getNetworkState();
							}
						}), new NetworkDataSource.OnRefreshListener() {
			@Override
			public void onRefresh() {
				if (dataSourceFactory.getNetworkDataSourceMutableLiveData().getValue() != null)
					dataSourceFactory.getNetworkDataSourceMutableLiveData()
							.getValue().invalidate();
			}
		}, new NetworkDataSource.OnRetryListener() {
			@Override
			public void onRetry() {
				if (dataSourceFactory.getNetworkDataSourceMutableLiveData().getValue() != null)
					dataSourceFactory.getNetworkDataSourceMutableLiveData()
							.getValue().retryAllFailed();

			}
		});
	}
}
