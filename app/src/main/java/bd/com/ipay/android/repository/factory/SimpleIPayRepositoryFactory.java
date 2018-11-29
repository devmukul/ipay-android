package bd.com.ipay.android.repository.factory;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Calendar;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import bd.com.ipay.android.repository.TransactionHistoryRepository;
import bd.com.ipay.android.utility.TransactionHistoryType;

public class SimpleIPayRepositoryFactory implements IPayRepositoryFactory {

	private static final Object LOCK = new Object();
	private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
	private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;

	private static final ExecutorService DISK_IO
			= Executors.newSingleThreadExecutor();
	private static final ExecutorService NETWORK_IO
			= Executors.newFixedThreadPool(MAXIMUM_POOL_SIZE);

	private static final IPayRepositoryFactory INSTANCE = new SimpleIPayRepositoryFactory();

	private SimpleIPayRepositoryFactory() {

	}

	@Override
	public Executor getNetworkExecutor() {
		return NETWORK_IO;
	}

	@Override
	public Executor getDiskIOExecutor() {
		return DISK_IO;
	}

	@Override
	public TransactionHistoryRepository
	getTransactionHistoryRepository(@NonNull TransactionHistoryType transactionHistoryType,
	                                @Nullable Integer serviceId,
	                                @Nullable Calendar fromDate,
	                                @Nullable Calendar toDate,
	                                @Nullable String searchText,
	                                int pageSize) {
		return new TransactionHistoryRepository(getNetworkExecutor(), transactionHistoryType,
				serviceId, fromDate, toDate,searchText, pageSize);
	}

	public static IPayRepositoryFactory getINSTANCE() {
		return INSTANCE;
	}
}
