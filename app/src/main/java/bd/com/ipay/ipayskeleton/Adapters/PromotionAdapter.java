package bd.com.ipay.ipayskeleton.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import bd.com.ipay.ipayskeleton.Model.Promotion.Promotion;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.PromotionConstants;
import bd.com.ipay.ipayskeleton.ViewHolders.Promotions.GiftFromIPayPromotionViewHolder;
import bd.com.ipay.ipayskeleton.ViewHolders.Promotions.MoneyBackIPayPromotionViewHolder;
import bd.com.ipay.ipayskeleton.ViewHolders.Promotions.NotImplementedPromotionViewHolder;
import bd.com.ipay.ipayskeleton.ViewHolders.Promotions.PromotionViewHolder;

public class PromotionAdapter extends RecyclerView.Adapter<PromotionViewHolder> {

	private LayoutInflater layoutInflater;
	private List<Promotion> mPromotionList;
	private PromotionViewHolder.OnOfferActionsListener mOnOfferActionsListener;

	public PromotionAdapter(final Context context, final PromotionViewHolder.OnOfferActionsListener onOfferActionsListener) {
		layoutInflater = LayoutInflater.from(context);
		this.mOnOfferActionsListener = onOfferActionsListener;
	}

	@NonNull
	@Override
	public PromotionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		switch (viewType) {
			case R.layout.list_item_gift_from_ipay_promotion:
				return new GiftFromIPayPromotionViewHolder(layoutInflater.inflate(R.layout.list_item_gift_from_ipay_promotion, parent, false), mOnOfferActionsListener);
			case R.layout.list_item_money_back_ipay_promotion:
				return new MoneyBackIPayPromotionViewHolder(layoutInflater.inflate(R.layout.list_item_gift_from_ipay_promotion, parent, false), mOnOfferActionsListener);
			case PromotionConstants.INVALID_VIEW_TYPE:
			default:
				return new NotImplementedPromotionViewHolder(layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false), mOnOfferActionsListener);
		}
	}

	@Override
	public void onBindViewHolder(@NonNull PromotionViewHolder holder, int position) {
		holder.bindTo(mPromotionList.get(position));
	}

	public void setItem(List<Promotion> promotionList) {
		this.mPromotionList = promotionList;
	}

	@Override
	public int getItemViewType(int position) {
		return PromotionConstants.getItemViewType(this.mPromotionList.get(position).getCampaignType());
	}

	@Override
	public int getItemCount() {
		if (mPromotionList == null)
			return 0;
		else
			return mPromotionList.size();
	}
}