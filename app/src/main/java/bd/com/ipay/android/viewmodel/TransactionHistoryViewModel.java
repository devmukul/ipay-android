package bd.com.ipay.android.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import java.util.Calendar;

public class TransactionHistoryViewModel extends AndroidViewModel {

	private final MutableLiveData<Pair<SearchType, Object>> searchInitiateLiveData
			= new MutableLiveData<>();

	private final LiveData<TransactionHistoryRepositoryArguments>
			transactionHistorySearchLiveData = Transformations.map(searchInitiateLiveData,
			new Function<Pair<SearchType, Object>,
					TransactionHistoryRepositoryArguments>() {
				@Override
				public TransactionHistoryRepositoryArguments apply(Pair<SearchType, Object> input) {
					if (input != null && input.first != null) {
						switch (input.first) {
							case KEY:
								if (input.second instanceof String) {
									return createTransactionHistoryRepositoryArguments(null, null, null,
											((String) input.second));
								}
								break;
							case DATE:
								if (input.second instanceof Pair) {
									if (((Pair) input.second).first instanceof Calendar
											&& ((Pair) input.second).second instanceof Calendar) {
										return createTransactionHistoryRepositoryArguments(null,
												((Calendar) ((Pair) input.second).first), ((Calendar) ((Pair) input.second).second),
												null);
									}
								}
								break;
							case SERVICE:
								if (input.second instanceof Integer) {
									return createTransactionHistoryRepositoryArguments(((Integer) input.second), null, null,
											null);
								}
								break;
							case ALL:
								return createTransactionHistoryRepositoryArguments(
										null, null, null, null);

						}
					}
					return null;
				}
			});

	private final LiveData<SearchType> addedFilterLiveData = Transformations.map(searchInitiateLiveData,
			new Function<Pair<SearchType, Object>, SearchType>() {
				@Override
				public SearchType apply(Pair<SearchType, Object> input) {
					if (input != null) {
						return input.first;
					}
					return null;
				}
			});

	private boolean forceRefreshData;

	public TransactionHistoryViewModel(@NonNull Application application) {
		super(application);
	}

	private TransactionHistoryRepositoryArguments
	createTransactionHistoryRepositoryArguments(@Nullable final Integer serviceId,
	                                            @Nullable final Calendar fromDate,
	                                            @Nullable final Calendar toDate,
	                                            @Nullable final String searchText) {
		return new TransactionHistoryRepositoryArguments(serviceId, fromDate,
				toDate, searchText);
	}

	public LiveData<TransactionHistoryRepositoryArguments> getTransactionHistorySearchLiveData() {
		return transactionHistorySearchLiveData;
	}

	public LiveData<SearchType> getAddedFilterLiveData() {
		return addedFilterLiveData;
	}

	public void filterTransactionHistory(SearchType service, int checkedId) {
		searchInitiateLiveData.setValue(new Pair<SearchType, Object>(service, checkedId));
	}

	public void filterTransactionHistory(SearchType service, String searchKey) {
		searchInitiateLiveData.setValue(new Pair<SearchType, Object>(service, searchKey));
	}

	public void filterTransactionHistory(SearchType service, Pair<Calendar, Calendar> datePeriod) {
		searchInitiateLiveData.setValue(new Pair<SearchType, Object>(service, datePeriod));
	}

	public void clearFilter() {
		searchInitiateLiveData.setValue(new Pair<>(SearchType.ALL, null));
	}

	public void setForceRefreshData(boolean forceRefreshData) {
		this.forceRefreshData = forceRefreshData;
	}

	public boolean isForceRefreshData() {
		return forceRefreshData;
	}

	public enum SearchType {
		KEY, DATE, SERVICE, ALL
	}
}
