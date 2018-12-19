package bd.com.ipay.android.adapter.viewholder;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.UnknownHostException;

import bd.com.ipay.android.datasource.NetworkDataSource;
import bd.com.ipay.android.utility.NetworkState;
import bd.com.ipay.android.utility.NetworkStatus;
import bd.com.ipay.ipayskeleton.R;

public class NetworkStateViewHolder extends PagedListViewHolder<NetworkState> {

	private final ProgressBar progressBar;
	private final Button retryButton;
	private final TextView messageTextView;

	private NetworkStateViewHolder(@NonNull View itemView,
	                               @NonNull final NetworkDataSource.OnRetryListener
			                               onRetryListener) {
		super(itemView);
		retryButton = itemView.findViewById(R.id.retry_button);
		progressBar = itemView.findViewById(R.id.progress_bar);
		messageTextView = itemView.findViewById(R.id.message_text_view);
		retryButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onRetryListener.onRetry();
			}
		});
	}

	public static NetworkStateViewHolder create(@NonNull ViewGroup parent,
	                                            NetworkDataSource.OnRetryListener onRetryListener) {
		final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
		return new NetworkStateViewHolder(
				layoutInflater.inflate(R.layout.list_item_network_state,
						parent, false), onRetryListener);
	}

	@Override
	public void bindTo(NetworkState networkState) {
		progressBar.setVisibility(networkState.getNetworkStatus() == NetworkStatus.RUNNING ?
				View.VISIBLE : View.GONE);
		retryButton.setVisibility(networkState.getNetworkStatus() == NetworkStatus.FAILED ?
				View.VISIBLE : View.GONE);
		messageTextView.setVisibility(networkState.getNetworkStatus() == NetworkStatus.REFRESH ?
				View.GONE : View.VISIBLE);

		if (networkState.getNetworkStatus() == NetworkStatus.FAILED) {
			if (networkState.getNetworkStateError() instanceof UnknownHostException) {
				messageTextView.setText(R.string.no_internet_connection);
			} else if (networkState.getNetworkStateError() != null &&
					networkState.getNetworkStateError().getMessage() != null) {
				messageTextView.setText(networkState.getNetworkStateError().getMessage());
			} else {
				messageTextView.setText(R.string.unknown_error);
			}
		} else if (networkState.getNetworkStatus() == NetworkStatus.SUCCESS) {
			messageTextView.setText(R.string.no_more_results);
		} else if (networkState.getNetworkStatus() == NetworkStatus.RUNNING) {
			messageTextView.setText(R.string.loading);
		}
	}
}
