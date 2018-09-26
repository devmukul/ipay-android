package bd.com.ipay.ipayskeleton.ViewHolders.Promotions;

import android.view.View;

import bd.com.ipay.ipayskeleton.Model.Promotion.Promotion;

public class MoneyBackIPayWithPromoGroupPromotionViewHolder extends MoneyBackIPayPromotionViewHolder {

	public MoneyBackIPayWithPromoGroupPromotionViewHolder(View itemView, OnOfferActionsListener onOfferActionsListener) {
		super(itemView, onOfferActionsListener);
	}

	@Override
	public void bindTo(Promotion promotion) {
		if (promotion.isActive())
			itemView.setVisibility(View.VISIBLE);
		else {
			itemView.setVisibility(View.GONE);
			return;
		}
		super.bindTo(promotion);
	}
}
