package bd.com.ipay.ipayskeleton.ViewHolders.Promotions;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Calendar;
import java.util.Date;

import bd.com.ipay.ipayskeleton.Model.Promotion.GiftFromIPayMetaData;
import bd.com.ipay.ipayskeleton.Model.Promotion.Promotion;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class GiftFromIPayPromotionViewHolder extends PromotionViewHolder {

	private TextView promotionTitleTextView;
	private TextView promotionSubDetailsTextView;
	private ImageView promotionImageView;
	private TextView availableRedeemCountTextView;
	private TextView offerRedeemCountTextView;
	private RatingBar totalTransactionCountBar;
	private Button claimButton;

	public GiftFromIPayPromotionViewHolder(View itemView, OnOfferActionsListener onOfferActionsListener) {
		super(itemView, onOfferActionsListener);
		promotionTitleTextView = itemView.findViewById(R.id.promotion_title_text_view);
		promotionSubDetailsTextView = itemView.findViewById(R.id.promotion_sub_details_text_view);
		promotionImageView = itemView.findViewById(R.id.promotion_image_view);
		availableRedeemCountTextView = itemView.findViewById(R.id.available_redeem_count_text_view);
		totalTransactionCountBar = itemView.findViewById(R.id.total_transaction_count_bar);
		offerRedeemCountTextView = itemView.findViewById(R.id.offer_redeem_count_text_view);
		claimButton = itemView.findViewById(R.id.claim_button);

		final ImageButton termsButton = itemView.findViewById(R.id.terms_button);

		termsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mOnOfferActionsListener.onTermAction(getAdapterPosition());
			}
		});
		claimButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mOnOfferActionsListener.onClaimAction(getAdapterPosition());
			}
		});
	}

	@Override
	public void bindTo(Promotion promotion) {
		if (promotion.isActive())
			itemView.setVisibility(View.VISIBLE);
		else {
			itemView.setVisibility(View.GONE);
			return;
		}
		GiftFromIPayMetaData giftFromIPayMetaData = promotion.getMedata(GiftFromIPayMetaData.class);
		promotionTitleTextView.setText(promotion.getCampaignTitle());
		promotionSubDetailsTextView.setText(promotion.getPromotionDetails());
		Glide.with(itemView.getContext()).load(Constants.BASE_URL_FTP_SERVER + promotion.getImageUrl()).into(promotionImageView);

		totalTransactionCountBar.setStepSize(1);
		totalTransactionCountBar.setNumStars(giftFromIPayMetaData.getTransactionCountPerRedeem());
		totalTransactionCountBar.setRating(giftFromIPayMetaData.getTransactionCountPerRedeem() - giftFromIPayMetaData.getTransactionRequiredForNextRedeem());
		offerRedeemCountTextView.setText(promotion.getConsumptionDetails());
		final Date currentDate = Calendar.getInstance().getTime();
		if (promotion.isActive() &&
				(giftFromIPayMetaData.getStartDate().before(currentDate) && giftFromIPayMetaData.getEndDate().after(currentDate)) &&
				giftFromIPayMetaData.getRedeemAvailable() > 0) {
			availableRedeemCountTextView.setVisibility(View.VISIBLE);
			totalTransactionCountBar.setVisibility(View.GONE);
			availableRedeemCountTextView.setText(itemView.getContext().getString(R.string.you_have_already_redeemed_this_offer_times, giftFromIPayMetaData.getRedeemAvailable()));
			claimButton.setVisibility(View.VISIBLE);
		} else {
			availableRedeemCountTextView.setVisibility(View.GONE);
			totalTransactionCountBar.setVisibility(View.VISIBLE);
			claimButton.setVisibility(View.GONE);
		}
	}
}