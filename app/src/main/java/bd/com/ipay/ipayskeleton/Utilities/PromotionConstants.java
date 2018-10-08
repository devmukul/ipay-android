package bd.com.ipay.ipayskeleton.Utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import bd.com.ipay.ipayskeleton.R;

public class PromotionConstants {

	private PromotionConstants() {
		// Prohibiting from creating an instance
	}

	public static final int INVALID_VIEW_TYPE = -1;
	private static final String GIFT_FROM_IPAY = "GIFT-FROM-IPAY";
	private static final String MONEYBACK_IPAY = "MONEYBACK-IPAY";
	private static final String MONEYBACK_IPAY_WITH_PROMO_GROUP = "MONEYBACK-IPAY-WITH-PROMO-GROUP";

	public static final Gson promotionConverterGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

	public static int getItemViewType(String campaignType) {
		switch (campaignType) {
			case GIFT_FROM_IPAY:
				return R.layout.list_item_gift_from_ipay_promotion;
			case MONEYBACK_IPAY:
				return R.layout.list_item_money_back_ipay_promotion;
			case MONEYBACK_IPAY_WITH_PROMO_GROUP:
				return R.layout.list_item_money_back_ipay_with_promo_group_promotion;
			default:
				return INVALID_VIEW_TYPE;
		}
	}
}
