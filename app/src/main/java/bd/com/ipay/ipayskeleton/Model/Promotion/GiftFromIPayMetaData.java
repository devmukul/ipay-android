package bd.com.ipay.ipayskeleton.Model.Promotion;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

@SuppressWarnings("unused")
public class GiftFromIPayMetaData extends PromotionMetaData {


	@SerializedName("transactionCount")
	private int transactionCount;
	@SerializedName("transactionCountPerRedeem")
	private int transactionCountPerRedeem;
	@SerializedName("redeemAvailable")
	private int redeemAvailable;
	@SerializedName("redeemConsumed")
	private int redeemConsumed;
	@SerializedName("transactionRequiredForNextRedeem")
	private int transactionRequiredForNextRedeem;
	@SerializedName("minimumTransactionAmount")
	private int minimumTransactionAmount;
	@SerializedName("businessAccountId")
	private int businessAccountId;
	@SerializedName("redeemPath")
	private String redeemPath;

	public GiftFromIPayMetaData() {
		super();
	}

	public GiftFromIPayMetaData(int transactionCount, int transactionCountPerRedeem, int redeemAvailable, int redeemConsumed, int transactionRequiredForNextRedeem, int minimumTransactionAmount, int businessAccountId, String redeemPath) {
		this.transactionCount = transactionCount;
		this.transactionCountPerRedeem = transactionCountPerRedeem;
		this.redeemAvailable = redeemAvailable;
		this.redeemConsumed = redeemConsumed;
		this.transactionRequiredForNextRedeem = transactionRequiredForNextRedeem;
		this.minimumTransactionAmount = minimumTransactionAmount;
		this.businessAccountId = businessAccountId;
		this.redeemPath = redeemPath;
	}

	public GiftFromIPayMetaData(Date startDate, Date endDate, int transactionCount, int transactionCountPerRedeem, int redeemAvailable, int redeemConsumed, int transactionRequiredForNextRedeem, int minimumTransactionAmount, int businessAccountId, String redeemPath) {
		super(startDate, endDate);
		this.transactionCount = transactionCount;
		this.transactionCountPerRedeem = transactionCountPerRedeem;
		this.redeemAvailable = redeemAvailable;
		this.redeemConsumed = redeemConsumed;
		this.transactionRequiredForNextRedeem = transactionRequiredForNextRedeem;
		this.minimumTransactionAmount = minimumTransactionAmount;
		this.businessAccountId = businessAccountId;
		this.redeemPath = redeemPath;
	}

	public int getTransactionCount() {

		return transactionCount;
	}

	public void setTransactionCount(int transactionCount) {
		this.transactionCount = transactionCount;
	}

	public int getTransactionCountPerRedeem() {
		return transactionCountPerRedeem;
	}

	public void setTransactionCountPerRedeem(int transactionCountPerRedeem) {
		this.transactionCountPerRedeem = transactionCountPerRedeem;
	}

	public int getRedeemAvailable() {
		return redeemAvailable;
	}

	public void setRedeemAvailable(int redeemAvailable) {
		this.redeemAvailable = redeemAvailable;
	}

	public int getRedeemConsumed() {
		return redeemConsumed;
	}

	public void setRedeemConsumed(int redeemConsumed) {
		this.redeemConsumed = redeemConsumed;
	}

	public int getTransactionRequiredForNextRedeem() {
		return transactionRequiredForNextRedeem;
	}

	public void setTransactionRequiredForNextRedeem(int transactionRequiredForNextRedeem) {
		this.transactionRequiredForNextRedeem = transactionRequiredForNextRedeem;
	}

	public int getMinimumTransactionAmount() {
		return minimumTransactionAmount;
	}

	public void setMinimumTransactionAmount(int minimumTransactionAmount) {
		this.minimumTransactionAmount = minimumTransactionAmount;
	}

	public int getBusinessAccountId() {
		return businessAccountId;
	}

	public void setBusinessAccountId(int businessAccountId) {
		this.businessAccountId = businessAccountId;
	}

	public String getRedeemPath() {
		return redeemPath;
	}

	public void setRedeemPath(String redeemPath) {
		this.redeemPath = redeemPath;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		GiftFromIPayMetaData that = (GiftFromIPayMetaData) o;

		if (transactionCount != that.transactionCount) return false;
		if (transactionCountPerRedeem != that.transactionCountPerRedeem) return false;
		if (redeemAvailable != that.redeemAvailable) return false;
		if (redeemConsumed != that.redeemConsumed) return false;
		if (transactionRequiredForNextRedeem != that.transactionRequiredForNextRedeem) return false;
		if (minimumTransactionAmount != that.minimumTransactionAmount) return false;
		if (businessAccountId != that.businessAccountId) return false;
		return redeemPath != null ? redeemPath.equals(that.redeemPath) : that.redeemPath == null;
	}

	@Override
	public int hashCode() {
		int result = transactionCount;
		result = 31 * result + transactionCountPerRedeem;
		result = 31 * result + redeemAvailable;
		result = 31 * result + redeemConsumed;
		result = 31 * result + transactionRequiredForNextRedeem;
		result = 31 * result + minimumTransactionAmount;
		result = 31 * result + businessAccountId;
		result = 31 * result + (redeemPath != null ? redeemPath.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "GiftFromIPayMetaData{" +
				"transactionCount=" + transactionCount +
				", transactionCountPerRedeem=" + transactionCountPerRedeem +
				", redeemAvailable=" + redeemAvailable +
				", redeemConsumed=" + redeemConsumed +
				", transactionRequiredForNextRedeem=" + transactionRequiredForNextRedeem +
				", minimumTransactionAmount=" + minimumTransactionAmount +
				", businessAccountId=" + businessAccountId +
				", redeemPath='" + redeemPath + '\'' +
				"} " + super.toString();
	}
}
