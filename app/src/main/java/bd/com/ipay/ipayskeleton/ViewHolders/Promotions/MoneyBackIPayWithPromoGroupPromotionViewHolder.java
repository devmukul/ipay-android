package bd.com.ipay.ipayskeleton.ViewHolders.Promotions;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import bd.com.ipay.ipayskeleton.Model.Promotion.Promotion;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class MoneyBackIPayWithPromoGroupPromotionViewHolder extends MoneyBackIPayPromotionViewHolder {

	private final TextView promotionTitleTextView;
	private final TextView promotionSubDetailsTextView;
	private final ImageView promotionImageView;

	public MoneyBackIPayWithPromoGroupPromotionViewHolder(View itemView, OnOfferActionsListener onOfferActionsListener) {
		super(itemView, onOfferActionsListener);
		promotionTitleTextView = itemView.findViewById(R.id.promotion_title_text_view);
		promotionSubDetailsTextView = itemView.findViewById(R.id.promotion_sub_details_text_view);
		promotionImageView = itemView.findViewById(R.id.promotion_image_view);
		final ImageButton termsButton = itemView.findViewById(R.id.terms_button);

		termsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mOnOfferActionsListener.onTermAction(getAdapterPosition());
			}
		});
	}

	@Override
	public void bindTo(Promotion promotion) {
		super.bindTo(promotion);
		promotionTitleTextView.setText(promotion.getCampaignTitle());
		promotionSubDetailsTextView.setText(promotion.getPromotionDetails());
		Glide.with(itemView.getContext()).load(Constants.BASE_URL_FTP_SERVER + promotion.getImageUrl()).into(promotionImageView);
	}
}
