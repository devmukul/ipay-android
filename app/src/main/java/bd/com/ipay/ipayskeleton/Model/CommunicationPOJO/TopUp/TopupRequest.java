package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TopUp;

public class TopupRequest {

	private final String receiverMobileNumber;
	private final int mobileNumberType;
	private final String operatorCode;
	private final int amount;
	private String pin;
	private String otp;

	public TopupRequest(String receiverMobileNumber,
	                    int mobileNumberType, String operatorCode, int amount) {
		this.receiverMobileNumber = receiverMobileNumber;
		this.mobileNumberType = mobileNumberType;
		this.operatorCode = operatorCode;
		this.amount = amount;
	}

	public String getOtp() {
		return otp;
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
}
