package bd.com.ipay.ipayskeleton.ViewHolders.Promotions;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import bd.com.ipay.ipayskeleton.Model.Promotion.Promotion;

public abstract class PromotionViewHolder extends RecyclerView.ViewHolder {

	OnOfferActionsListener mOnOfferActionsListener;

	PromotionViewHolder(View itemView, final OnOfferActionsListener onOfferActionsListener) {
		super(itemView);
		this.mOnOfferActionsListener = onOfferActionsListener;
	}

	public abstract void bindTo(Promotion promotion);

	public interface OnOfferActionsListener {
		void onClaimAction(int promotionPosition);

		void onTermAction(int promotionPosition);
	}
}