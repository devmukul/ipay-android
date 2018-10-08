package bd.com.ipay.ipayskeleton.ViewHolders.Promotions;

import android.view.View;

import bd.com.ipay.ipayskeleton.Model.Promotion.Promotion;

public class NotImplementedPromotionViewHolder extends PromotionViewHolder {

	public NotImplementedPromotionViewHolder(View itemView, OnOfferActionsListener onOfferActionsListener) {
		super(itemView, onOfferActionsListener);
	}

	@Override
	public void bindTo(Promotion promotion) {
		itemView.setVisibility(View.GONE);
	}
}