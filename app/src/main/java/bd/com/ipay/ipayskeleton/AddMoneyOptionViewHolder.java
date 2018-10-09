package bd.com.ipay.ipayskeleton;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import bd.com.ipay.ipayskeleton.Adapters.OnItemClickListener;
import bd.com.ipay.ipayskeleton.Model.AddMoneyOption;
import bd.com.ipay.ipayskeleton.ViewHolders.IPayViewHolder;

public class AddMoneyOptionViewHolder extends IPayViewHolder<AddMoneyOption> {

	private final ImageView optionIconImageView;
	private final TextView optionTitleTextView;
	private final TextView optionDescriptionTextView;

	private AddMoneyOptionViewHolder(final View itemView, final OnItemClickListener onItemClickListener) {
		super(itemView);
		optionIconImageView = this.itemView.findViewById(R.id.option_icon_image_view);
		optionTitleTextView = this.itemView.findViewById(R.id.option_title_text_view);
		optionDescriptionTextView = this.itemView.findViewById(R.id.option_description_text_view);
		this.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onItemClickListener != null) {
					onItemClickListener.onItemClick(getAdapterPosition(), AddMoneyOptionViewHolder.this.itemView);
				}
			}
		});
	}

	public static AddMoneyOptionViewHolder create(@NonNull View itemView, @Nullable OnItemClickListener onItemClickListener) {
		return new AddMoneyOptionViewHolder(itemView, onItemClickListener);
	}

	@Override
	public void bindTo(AddMoneyOption addMoneyOption) {
		optionIconImageView.setImageResource(addMoneyOption.getOptionIconResourceId());
		optionTitleTextView.setText(addMoneyOption.getOptionTitleResourceId());
		optionDescriptionTextView.setText(addMoneyOption.getOptionDescriptionResourceId());
	}
}
