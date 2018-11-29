package bd.com.ipay.android.datasource.factory;

import android.arch.paging.DataSource;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Calendar;
import java.util.concurrent.Executor;

import bd.com.ipay.android.datasource.transaction.TransactionHistoryDataSource;
import bd.com.ipay.android.utility.TransactionHistoryType;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory.TransactionHistory;

public class TransactionHistoryDataSourceFactory
		extends NetworkDataSourceFactory<Integer, TransactionHistory> {

	@NonNull
	private final Executor retryExecutor;
	@NonNull
	private final TransactionHistoryType transactionHistoryType;
	@Nullable
	private final Integer serviceId;
	@Nullable
	private final Calendar fromDate;
	@Nullable
	private final Calendar toDate;
	@Nullable
	private final String searchText;

	public TransactionHistoryDataSourceFactory(@NonNull Executor retryExecutor, @NonNull TransactionHistoryType transactionHistoryType, @Nullable Integer serviceId, @Nullable Calendar fromDate, @Nullable Calendar toDate, @Nullable String searchText) {
		this.retryExecutor = retryExecutor;
		this.transactionHistoryType = transactionHistoryType;
		this.serviceId = serviceId;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.searchText = searchText;
	}

	@Override
	public DataSource<Integer, TransactionHistory> create() {
		final TransactionHistoryDataSource transactionHistoryDataSource =
				new TransactionHistoryDataSource(retryExecutor,
						transactionHistoryType, serviceId, fromDate, toDate, searchText);
		getNetworkDataSourceMutableLiveData().postValue(transactionHistoryDataSource);
		return transactionHistoryDataSource;
	}
}
