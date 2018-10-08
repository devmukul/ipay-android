package bd.com.ipay.ipayskeleton.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Bank.BankAccountList;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.ViewHolders.UserBankListViewHolder;

public class UserBankListAdapter extends ListAdapter<BankAccountList, UserBankListViewHolder> {

	private static final DiffUtil.ItemCallback<BankAccountList> BANK_ACCOUNT_LIST_DIFF = new DiffUtil.ItemCallback<BankAccountList>() {
		@Override
		public boolean areItemsTheSame(BankAccountList oldItem, BankAccountList newItem) {
			return oldItem.getBankAccountId().equals(newItem.getBankAccountId());
		}

		@Override
		public boolean areContentsTheSame(BankAccountList oldItem, BankAccountList newItem) {
			return oldItem.getBankAccountId().equals(newItem.getBankAccountId());
		}
	};

	private final OnItemClickListener onItemClickListener;
	private final LayoutInflater layoutInflater;

	public UserBankListAdapter(@NonNull final Context context, @Nullable final OnItemClickListener onItemClickListener) {
		super(BANK_ACCOUNT_LIST_DIFF);
		this.onItemClickListener = onItemClickListener;
		this.layoutInflater = LayoutInflater.from(context);
	}

	@NonNull
	@Override
	public UserBankListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return UserBankListViewHolder.create(layoutInflater.inflate(R.layout.list_item_bank_account_option, parent, false), onItemClickListener);
	}

	@Override
	public void onBindViewHolder(@NonNull UserBankListViewHolder holder, int position) {
		final BankAccountList bankAccountList = getItem(position);
		holder.bindTo(bankAccountList);
	}
}
