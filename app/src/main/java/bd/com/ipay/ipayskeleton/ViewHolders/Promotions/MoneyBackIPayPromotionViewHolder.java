package bd.com.ipay.ipayskeleton.ViewHolders.Promotions;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import bd.com.ipay.ipayskeleton.Model.Promotion.Promotion;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class MoneyBackIPayPromotionViewHolder extends PromotionViewHolder {
	private TextView promotionTitleTextView;
	private TextView promotionSubDetailsTextView;
	private ImageView promotionImageView;

	public MoneyBackIPayPromotionViewHolder(View itemView, OnOfferActionsListener onOfferActionsListener) {
		super(itemView, onOfferActionsListener);
		promotionTitleTextView = itemView.findViewById(R.id.promotion_title_text_view);
		promotionSubDetailsTextView = itemView.findViewById(R.id.promotion_sub_details_text_view);
		promotionImageView = itemView.findViewById(R.id.promotion_image_view);
	}

	@Override
	public void bindTo(Promotion promotion) {
		if (promotion.isActive())
			itemView.setVisibility(View.VISIBLE);
		else {
			itemView.setVisibility(View.GONE);
			return;
		}
		promotionTitleTextView.setText(promotion.getCampaignTitle());
		promotionSubDetailsTextView.setText(promotion.getPromotionDetails());
		Glide.with(itemView.getContext()).load(Constants.BASE_URL_FTP_SERVER + promotion.getImageUrl()).into(promotionImageView);
	}
}
