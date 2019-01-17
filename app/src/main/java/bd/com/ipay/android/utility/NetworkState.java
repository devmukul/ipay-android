package bd.com.ipay.android.utility;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class NetworkState {
	@NonNull
	private final NetworkStatus networkStatus;
	@Nullable
	private final Throwable networkStateError;

	@NonNull
	public static final NetworkState LOADED
			= new NetworkState(NetworkStatus.SUCCESS, null);
	@NonNull
	public static final NetworkState REFRESHING
			= new NetworkState(NetworkStatus.REFRESH, null);
	@NonNull
	public static final NetworkState LOADING
			= new NetworkState(NetworkStatus.RUNNING, null);

	public static NetworkState networkStateError(@NonNull Throwable throwable) {
		return new NetworkState(NetworkStatus.FAILED, throwable);
	}

	private NetworkState(@NonNull NetworkStatus networkStatus,
	                     @Nullable Throwable networkStateError) {
		this.networkStatus = networkStatus;
		this.networkStateError = networkStateError;
	}

	@NonNull
	public NetworkStatus getNetworkStatus() {
		return networkStatus;
	}

	@Nullable
	public Throwable getNetworkStateError() {
		return networkStateError;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		NetworkState that = (NetworkState) o;

		if (networkStateError != null && that.networkStateError != null) {
			if (!networkStateError.equals(that.networkStateError)) {
				return false;
			}
		}
		return networkStatus == that.networkStatus;
	}

	@Override
	public int hashCode() {
		return networkStatus.hashCode();
	}
}
