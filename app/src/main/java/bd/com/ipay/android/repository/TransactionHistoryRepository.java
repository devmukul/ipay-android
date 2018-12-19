package bd.com.ipay.android.repository;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Calendar;
import java.util.concurrent.Executor;

import bd.com.ipay.android.datasource.factory.TransactionHistoryDataSourceFactory;
import bd.com.ipay.android.utility.TransactionHistoryType;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory.TransactionHistory;

public class TransactionHistoryRepository extends InMemoryPageRepository<Integer, TransactionHistory> {

	public TransactionHistoryRepository(@NonNull Executor networkExecutor,
	                                    @NonNull TransactionHistoryType transactionHistoryType,
	                                    @Nullable Integer serviceId,
	                                    @Nullable Calendar fromDate,
	                                    @Nullable Calendar toDate,
	                                    @Nullable String searchText,
	                                    int pageSize) {
		super(pageSize, new TransactionHistoryDataSourceFactory(networkExecutor,
				transactionHistoryType, serviceId, fromDate, toDate, searchText), networkExecutor);

	}
}
