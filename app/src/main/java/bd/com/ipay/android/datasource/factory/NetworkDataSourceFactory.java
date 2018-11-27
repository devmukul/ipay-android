package bd.com.ipay.android.datasource.factory;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

import bd.com.ipay.android.datasource.NetworkDataSource;

public abstract class NetworkDataSourceFactory<Key, Value> extends DataSource.Factory<Key, Value> {

	private final MutableLiveData<NetworkDataSource<Key, Value>> networkDataSourceMutableLiveData
			= new MutableLiveData<>();

	public MutableLiveData<NetworkDataSource<Key, Value>> getNetworkDataSourceMutableLiveData() {
		return networkDataSourceMutableLiveData;
	}
}
