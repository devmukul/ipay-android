package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.AddOrWithdrawMoney;

public class AddMoneyByBankInstantlyRequest {

	private final long userBankId;
	private final double amount;
	private final String description;
	private final String pin;
	private String otp;

	public AddMoneyByBankInstantlyRequest(long userBankId, double amount, String description, String pin) {
		this.userBankId = userBankId;
		this.amount = amount;
		this.description = description;
		this.pin = pin;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}


}
