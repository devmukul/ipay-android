package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.SendMoney;

public class SendMoneyRequest {


	private final String receiverMobileNumber;
	private final double amount;
	private final String description;
	private String pin;
	private String otp;

	public SendMoneyRequest(String receiverMobileNumber, String amount, String description) {
		this.receiverMobileNumber = receiverMobileNumber;
		this.amount = Double.parseDouble(amount);
		this.description = description;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getReceiverMobileNumber() {
		return receiverMobileNumber;
	}

	public double getAmount() {
		return amount;
	}

	public String getDescription() {
		return description;
	}

	public String getOtp() {
		return otp;
	}
}
