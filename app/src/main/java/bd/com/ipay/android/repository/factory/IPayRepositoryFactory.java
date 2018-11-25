package bd.com.ipay.android.repository.factory;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Calendar;
import java.util.concurrent.Executor;

import bd.com.ipay.android.repository.TransactionHistoryRepository;
import bd.com.ipay.android.utility.TransactionHistoryType;

public interface IPayRepositoryFactory {

	Executor getNetworkExecutor();

	Executor getDiskIOExecutor();

	TransactionHistoryRepository
	getTransactionHistoryRepository(@NonNull TransactionHistoryType transactionHistoryType,
	                                @Nullable Integer serviceId,
	                                @Nullable Calendar fromDate,
	                                @Nullable Calendar toDate,
	                                @Nullable String searchText,
	                                int pageSize);
}
