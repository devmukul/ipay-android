package bd.com.ipay.ipayskeleton.Model.Promotion;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

@SuppressWarnings({"unused", "WeakerAccess"})
public class MoneyBackIPayMetaData extends PromotionMetaData {

	@SerializedName("moneyBackPercentage")
	private double moneyBackPercentage;
	@SerializedName("maximumOfferAmount")
	private double maximumOfferAmount;
	@SerializedName("consumedOfferAmount")
	private double consumedOfferAmount;
	@SerializedName("remainingOfferAmount")
	private double remainingOfferAmount;
	@SerializedName("merchantList")
	private List<Integer> merchantList;

	public MoneyBackIPayMetaData() {
		super();
	}

	public MoneyBackIPayMetaData(double moneyBackPercentage, double maximumOfferAmount, double consumedOfferAmount, double remainingOfferAmount, List<Integer> merchantList) {

		this.moneyBackPercentage = moneyBackPercentage;
		this.maximumOfferAmount = maximumOfferAmount;
		this.consumedOfferAmount = consumedOfferAmount;
		this.remainingOfferAmount = remainingOfferAmount;
		this.merchantList = merchantList;
	}

	public MoneyBackIPayMetaData(Date startDate, Date endDate, double moneyBackPercentage, double maximumOfferAmount, double consumedOfferAmount, double remainingOfferAmount, List<Integer> merchantList) {
		super(startDate, endDate);
		this.moneyBackPercentage = moneyBackPercentage;
		this.maximumOfferAmount = maximumOfferAmount;
		this.consumedOfferAmount = consumedOfferAmount;
		this.remainingOfferAmount = remainingOfferAmount;
		this.merchantList = merchantList;
	}

	public double getMoneyBackPercentage() {
		return moneyBackPercentage;
	}

	public void setMoneyBackPercentage(double moneyBackPercentage) {
		this.moneyBackPercentage = moneyBackPercentage;
	}

	public double getMaximumOfferAmount() {
		return maximumOfferAmount;
	}

	public void setMaximumOfferAmount(double maximumOfferAmount) {
		this.maximumOfferAmount = maximumOfferAmount;
	}

	public double getConsumedOfferAmount() {
		return consumedOfferAmount;
	}

	public void setConsumedOfferAmount(double consumedOfferAmount) {
		this.consumedOfferAmount = consumedOfferAmount;
	}

	public double getRemainingOfferAmount() {
		return remainingOfferAmount;
	}

	public void setRemainingOfferAmount(double remainingOfferAmount) {
		this.remainingOfferAmount = remainingOfferAmount;
	}

	public List<Integer> getMerchantList() {
		return merchantList;
	}

	public void setMerchantList(List<Integer> merchantList) {
		this.merchantList = merchantList;
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
