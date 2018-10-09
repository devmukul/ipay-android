package bd.com.ipay.ipayskeleton.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import bd.com.ipay.ipayskeleton.AddMoneyOptionViewHolder;
import bd.com.ipay.ipayskeleton.Model.AddMoneyOption;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.ViewHolders.IPayViewHolder;

public class AddMoneyOptionAdapter extends RecyclerView.Adapter<IPayViewHolder<AddMoneyOption>> {

	@Nullable
	private final OnItemClickListener onItemClickListener;
	private final LayoutInflater layoutInflater;
	private List<AddMoneyOption> addMoneyOptionList;

	public AddMoneyOptionAdapter(@NonNull final Context context, @Nullable final OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
		layoutInflater = LayoutInflater.from(context);
	}

	@NonNull
	@Override
	public AddMoneyOptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return AddMoneyOptionViewHolder.create(layoutInflater.inflate(R.layout.list_item_add_money_option, parent, false), onItemClickListener);
	}

	@Override
	public void onBindViewHolder(@NonNull IPayViewHolder<AddMoneyOption> holder, int position) {
		if (addMoneyOptionList != null) {
			final AddMoneyOption addMoneyOption = addMoneyOptionList.get(position);
			holder.bindTo(addMoneyOption);
		}
	}

	public void setItemList(List<AddMoneyOption> addMoneyOptionList) {
		this.addMoneyOptionList = addMoneyOptionList;
	}

	@Override
	public int getItemCount() {
		return this.addMoneyOptionList != null ? this.addMoneyOptionList.size() : 0;
	}
}