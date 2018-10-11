package bd.com.ipay.ipayskeleton.Model.Promotion;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

@SuppressWarnings({"unused", "WeakerAccess"})
public class MoneyBackIPayMetaData extends PromotionMetaData {

	@SerializedName("moneyBackPercentage")
	private int moneyBackPercentage;
	@SerializedName("maximumOfferAmount")
	private int maximumOfferAmount;
	@SerializedName("consumedOfferAmount")
	private int consumedOfferAmount;
	@SerializedName("remainingOfferAmount")
	private int remainingOfferAmount;
	@SerializedName("merchantList")
	private List<Integer> merchantList;

	public MoneyBackIPayMetaData() {
		super();
	}

	public MoneyBackIPayMetaData(int moneyBackPercentage, int maximumOfferAmount, int consumedOfferAmount, int remainingOfferAmount, List<Integer> merchantList) {

		this.moneyBackPercentage = moneyBackPercentage;
		this.maximumOfferAmount = maximumOfferAmount;
		this.consumedOfferAmount = consumedOfferAmount;
		this.remainingOfferAmount = remainingOfferAmount;
		this.merchantList = merchantList;
	}

	public MoneyBackIPayMetaData(Date startDate, Date endDate, int moneyBackPercentage, int maximumOfferAmount, int consumedOfferAmount, int remainingOfferAmount, List<Integer> merchantList) {
		super(startDate, endDate);
		this.moneyBackPercentage = moneyBackPercentage;
		this.maximumOfferAmount = maximumOfferAmount;
		this.consumedOfferAmount = consumedOfferAmount;
		this.remainingOfferAmount = remainingOfferAmount;
		this.merchantList = merchantList;
	}

	public int getMoneyBackPercentage() {

		return moneyBackPercentage;
	}

	public void setMoneyBackPercentage(int moneyBackPercentage) {
		this.moneyBackPercentage = moneyBackPercentage;
	}

	public int getMaximumOfferAmount() {
		return maximumOfferAmount;
	}

	public void setMaximumOfferAmount(int maximumOfferAmount) {
		this.maximumOfferAmount = maximumOfferAmount;
	}

	public int getConsumedOfferAmount() {
		return consumedOfferAmount;
	}

	public void setConsumedOfferAmount(int consumedOfferAmount) {
		this.consumedOfferAmount = consumedOfferAmount;
	}

	public int getRemainingOfferAmount() {
		return remainingOfferAmount;
	}

	public void setRemainingOfferAmount(int remainingOfferAmount) {
		this.remainingOfferAmount = remainingOfferAmount;
	}

	public List<Integer> getMerchantList() {
		return merchantList;
	}

	public void setMerchantList(List<Integer> merchantList) {
		this.merchantList = merchantList;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		MoneyBackIPayMetaData that = (MoneyBackIPayMetaData) o;

		if (moneyBackPercentage != that.moneyBackPercentage) return false;
		if (maximumOfferAmount != that.maximumOfferAmount) return false;
		if (consumedOfferAmount != that.consumedOfferAmount) return false;
		if (remainingOfferAmount != that.remainingOfferAmount) return false;
		return merchantList != null ? merchantList.equals(that.merchantList) : that.merchantList == null;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + moneyBackPercentage;
		result = 31 * result + maximumOfferAmount;
		result = 31 * result + consumedOfferAmount;
		result = 31 * result + remainingOfferAmount;
		result = 31 * result + (merchantList != null ? merchantList.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "MoneyBackIPayMetaData{" +
				"moneyBackPercentage=" + moneyBackPercentage +
				", maximumOfferAmount=" + maximumOfferAmount +
				", consumedOfferAmount=" + consumedOfferAmount +
				", remainingOfferAmount=" + remainingOfferAmount +
				", merchantList=" + merchantList +
				"} " + super.toString();
	}
}
