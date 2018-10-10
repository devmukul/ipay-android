package bd.com.ipay.ipayskeleton.ViewHolders.Promotions;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import bd.com.ipay.ipayskeleton.Model.Promotion.MoneyBackIPayMetaData;
import bd.com.ipay.ipayskeleton.Model.Promotion.Promotion;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class MoneyBackIPayPromotionViewHolder extends PromotionViewHolder {
	private final TextView promotionTitleTextView;
	private final TextView promotionSubDetailsTextView;
	private final TextView offerRedeemCountTextView;
	private final ImageView promotionImageView;
	private final ProgressBar totalTransactionProgressBar;

	public MoneyBackIPayPromotionViewHolder(View itemView, OnOfferActionsListener onOfferActionsListener) {
		super(itemView, onOfferActionsListener);
		promotionTitleTextView = itemView.findViewById(R.id.promotion_title_text_view);
		promotionSubDetailsTextView = itemView.findViewById(R.id.promotion_sub_details_text_view);
		offerRedeemCountTextView = itemView.findViewById(R.id.offer_redeem_count_text_view);
		promotionImageView = itemView.findViewById(R.id.promotion_image_view);
		totalTransactionProgressBar = itemView.findViewById(R.id.total_transaction_progress_bar);

		final ImageButton termsButton = itemView.findViewById(R.id.terms_button);

		termsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mOnOfferActionsListener.onTermAction(getAdapterPosition());
			}
		});
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public void bindTo(Promotion promotion) {
		promotionTitleTextView.setText(promotion.getCampaignTitle());
		promotionSubDetailsTextView.setText(promotion.getPromotionDetails());
		Glide.with(itemView.getContext()).load(Constants.BASE_URL_FTP_SERVER + promotion.getImageUrl()).into(promotionImageView);
		MoneyBackIPayMetaData moneyBackIPayMetaData = promotion.getMedata(MoneyBackIPayMetaData.class);

		offerRedeemCountTextView.setText(promotion.getConsumptionDetails());
		totalTransactionProgressBar.setProgress((int) ((moneyBackIPayMetaData.getConsumedOfferAmount() / (moneyBackIPayMetaData.getMaximumOfferAmount() * 1.0)) * 100));
	}
}
