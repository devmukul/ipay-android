package bd.com.ipay.android.adapter;

import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.view.ViewGroup;

import bd.com.ipay.android.adapter.viewholder.NetworkStateViewHolder;
import bd.com.ipay.android.adapter.viewholder.OnItemClickListener;
import bd.com.ipay.android.adapter.viewholder.PagedListViewHolder;
import bd.com.ipay.android.adapter.viewholder.transaction.TransactionHistoryViewHolder;
import bd.com.ipay.android.datasource.NetworkDataSource;
import bd.com.ipay.android.utility.NetworkState;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory.TransactionHistory;
import bd.com.ipay.ipayskeleton.R;

public class TransactionHistoryAdapter
		extends PagedListAdapter<TransactionHistory, PagedListViewHolder<?>> {

	private static final DiffUtil.ItemCallback<TransactionHistory> TRANSACTION_HISTORY_DIFF
			= new DiffUtil.ItemCallback<TransactionHistory>() {

		@Override
		public boolean areItemsTheSame(@NonNull TransactionHistory transactionHistoryOne,
		                               @NonNull TransactionHistory transactionHistoryTwo) {
			return transactionHistoryOne.getTransactionID()
					.equals(transactionHistoryTwo.getTransactionID());
		}

		@Override
		public boolean areContentsTheSame(@NonNull TransactionHistory transactionHistoryOne,
		                                  @NonNull TransactionHistory transactionHistoryTwo) {
			return transactionHistoryOne.equals(transactionHistoryTwo);
		}
	};

	@NonNull
	private final OnItemClickListener onItemClickListener;
	@NonNull
	private final NetworkDataSource.OnRetryListener onRetryListener;
	@Nullable
	private NetworkState networkState = null;

	public TransactionHistoryAdapter(@NonNull NetworkDataSource.OnRetryListener onRetryListener,
	                                 @NonNull OnItemClickListener onItemClickListener) {
		super(TRANSACTION_HISTORY_DIFF);
		this.onRetryListener = onRetryListener;
		this.onItemClickListener = onItemClickListener;
	}

	@NonNull
	@Override
	public PagedListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup,
	                                              int itemViewType) {
		switch (itemViewType) {
			case R.layout.list_item_transaction_history_new:
				return TransactionHistoryViewHolder.create(viewGroup, onItemClickListener);
			case R.layout.list_item_network_state:
				return NetworkStateViewHolder.create(viewGroup, onRetryListener);
			default:
				throw new IllegalArgumentException(String.format("unknown view type %d",
						itemViewType));

		}
	}

	@Override
	public void onBindViewHolder(@NonNull PagedListViewHolder<?> pagedListViewHolder,
	                             int position) {
		if (pagedListViewHolder instanceof TransactionHistoryViewHolder) {
			((TransactionHistoryViewHolder) pagedListViewHolder).bindTo(getItem(position));
		} else if (pagedListViewHolder instanceof NetworkStateViewHolder) {
			((NetworkStateViewHolder) pagedListViewHolder).bindTo(networkState);
		}
	}

	@Override
	public int getItemViewType(int position) {
		if (hasExtraRow() && position == getItemCount() - 1) {
			return R.layout.list_item_network_state;
		} else {
			return R.layout.list_item_transaction_history_new;
		}
	}

	@Override
	public int getItemCount() {
		return super.getItemCount() + (hasExtraRow() ? 1 : 0);
	}

	public void setNetworkSate(NetworkState newNetworkSate) {
		final NetworkState previousNetworkState = this.networkState;
		final boolean hadExtraRow = hasExtraRow();
		this.networkState = newNetworkSate;
		final boolean hasExtraRow = hasExtraRow();

		//noinspection ConstantConditions
		if (hadExtraRow != hasExtraRow) {
			if (hadExtraRow) {
				notifyItemRemoved(super.getItemCount());
			} else {
				notifyItemInserted(super.getItemCount());
			}
		} else if (hasExtraRow && !newNetworkSate.equals(previousNetworkState)) {
			notifyDataSetChanged();
		}
	}

	private boolean hasExtraRow() {
		return networkState != null && (!NetworkState.LOADED.equals(networkState)
				|| !NetworkState.REFRESHING.equals(networkState));
	}
}
