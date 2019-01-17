package bd.com.ipay.android.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import bd.com.ipay.android.repository.InMemoryPageRepository;
import bd.com.ipay.android.utility.TransactionHistoryType;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory.TransactionHistory;

public class TransactionHistoryRepositoryViewModel
		extends IPayRepositoryViewModel<Integer, TransactionHistory> {

	private final TransactionHistoryType transactionHistoryType;

	public TransactionHistoryRepositoryViewModel(@NonNull TransactionHistoryType
			                                             transactionHistoryType,
	                                             @NonNull Application application) {
		super(application);
		this.transactionHistoryType = transactionHistoryType;
	}

	@Override
	protected final InMemoryPageRepository<Integer, TransactionHistory> createRepository(
			@NonNull RepositoryArguments arguments) {
		this.setRepositoryArguments(arguments);
		if (arguments instanceof TransactionHistoryRepositoryArguments) {
			final TransactionHistoryRepositoryArguments repositoryArguments
					= (TransactionHistoryRepositoryArguments) arguments;
			return iPayRepositoryFactory.getTransactionHistoryRepository(
					transactionHistoryType,
					repositoryArguments.getServiceId(),
					repositoryArguments.getFromDate(),
					repositoryArguments.getToDate(),
					repositoryArguments.getSearchText(),
					repositoryArguments.getPageSize());
		} else {
			return iPayRepositoryFactory.getTransactionHistoryRepository(
					transactionHistoryType,
					null,
					null,
					null,
					null,
					arguments.getPageSize());
		}
	}
}
