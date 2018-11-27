package bd.com.ipay.android.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import bd.com.ipay.android.model.Listing;
import bd.com.ipay.android.repository.InMemoryPageRepository;
import bd.com.ipay.android.repository.factory.IPayRepositoryFactory;
import bd.com.ipay.android.repository.factory.SimpleIPayRepositoryFactory;
import bd.com.ipay.android.utility.NetworkState;

abstract class IPayRepositoryViewModel<Key, Model> extends AndroidViewModel {

	static final IPayRepositoryFactory iPayRepositoryFactory =
			SimpleIPayRepositoryFactory.getINSTANCE();

	private RepositoryArguments repositoryArguments;

	@NonNull
	private final MutableLiveData<Listing<Model>> repositoryResult = new MutableLiveData<Listing<Model>>();

	@NonNull
	private final LiveData<PagedList<Model>> pagedData = Transformations.switchMap(repositoryResult,
			new Function<Listing<Model>, LiveData<PagedList<Model>>>() {
				@Override
				public LiveData<PagedList<Model>> apply(Listing<Model> input) {
					return input.getPagedList();
				}
			});
	@NonNull
	private final LiveData<NetworkState> networkState = Transformations.switchMap(repositoryResult,
			new Function<Listing<Model>, LiveData<NetworkState>>() {
				@Override
				public LiveData<NetworkState> apply(Listing<Model> input) {
					return input.getNetworkState();
				}
			});
	@NonNull
	private final LiveData<NetworkState> refreshState = Transformations.switchMap(repositoryResult,
			new Function<Listing<Model>, LiveData<NetworkState>>() {
				@Override
				public LiveData<NetworkState> apply(Listing<Model> input) {
					return input.getRefreshState();
				}
			});

	public IPayRepositoryViewModel(@NonNull Application application) {
		super(application);
	}

	public void refreshData() {
		if (repositoryResult.getValue() != null) {
			repositoryResult.getValue().getRefresh().onRefresh();
		}
	}

	public void retry() {
		if (repositoryResult.getValue() != null) {
			repositoryResult.getValue().getRetry().onRetry();
		}
	}

	public final void fetchData(@NonNull RepositoryArguments arguments) {
		InMemoryPageRepository<Key, Model> repository = createRepository(arguments);
		repositoryResult.setValue(repository.getPagedListing());
	}

	public RepositoryArguments getRepositoryArguments() {
		return repositoryArguments;
	}

	public void setRepositoryArguments(RepositoryArguments repositoryArguments) {
		this.repositoryArguments = repositoryArguments;
	}

	protected abstract InMemoryPageRepository<Key, Model>
	createRepository(@NonNull RepositoryArguments arguments);

	@Nullable
	public Model getItem(int position) {
		if (pagedData.getValue() != null && !pagedData.getValue().isEmpty())
			return pagedData.getValue().get(position);
		else
			return null;
	}

	public boolean isSameRepositoryArguments(@Nullable RepositoryArguments repositoryArguments) {
		if (this.repositoryArguments != null) {
			return this.repositoryArguments.equals(repositoryArguments);
		} else {
			return false;
		}
	}

	@NonNull
	public LiveData<PagedList<Model>> getPagedData() {
		return pagedData;
	}

	@NonNull
	public LiveData<NetworkState> getNetworkState() {
		return networkState;
	}

	@NonNull
	public LiveData<NetworkState> getRefreshState() {
		return refreshState;
	}
}