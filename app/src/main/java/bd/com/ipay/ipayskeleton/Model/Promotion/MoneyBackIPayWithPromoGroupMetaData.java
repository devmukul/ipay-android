package bd.com.ipay.ipayskeleton.Model.Promotion;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

@SuppressWarnings("unused")
public class MoneyBackIPayWithPromoGroupMetaData extends MoneyBackIPayMetaData {
	@SerializedName("promoCode")
	private String promoCode;

	public MoneyBackIPayWithPromoGroupMetaData() {
	}

	public MoneyBackIPayWithPromoGroupMetaData(String promoCode) {

		this.promoCode = promoCode;
	}

	public MoneyBackIPayWithPromoGroupMetaData(double moneyBackPercentage, double maximumOfferAmount, double consumedOfferAmount, double remainingOfferAmount, List<Integer> merchantList, String promoCode) {
		super(moneyBackPercentage, maximumOfferAmount, consumedOfferAmount, remainingOfferAmount, merchantList);
		this.promoCode = promoCode;
	}

	public MoneyBackIPayWithPromoGroupMetaData(Date startDate, Date endDate, double moneyBackPercentage, double maximumOfferAmount, double consumedOfferAmount, double remainingOfferAmount, List<Integer> merchantList, String promoCode) {
		super(startDate, endDate, moneyBackPercentage, maximumOfferAmount, consumedOfferAmount, remainingOfferAmount, merchantList);
		this.promoCode = promoCode;
	}

	public String getPromoCode() {

		return promoCode;
	}

	public void setPromoCode(String promoCode) {
		this.promoCode = promoCode;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		MoneyBackIPayWithPromoGroupMetaData that = (MoneyBackIPayWithPromoGroupMetaData) o;

		return promoCode != null ? promoCode.equals(that.promoCode) : that.promoCode == null;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (promoCode != null ? promoCode.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "MoneyBackIPayWithPromoGroupMetaData{" +
				"promoCode='" + promoCode + '\'' +
				"} " + super.toString();
	}
}
