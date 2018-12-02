package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TopUp;

public class TopupRequest {

	private final String receiverMobileNumber;
	private final int mobileNumberType;
	private final String operatorCode;
	private final double amount;
	private final String pin;
	private String otp;

	public TopupRequest(String receiverMobileNumber,
	                    int mobileNumberType, String operatorCode, double amount, String pin) {
		this.receiverMobileNumber = receiverMobileNumber;
		this.mobileNumberType = mobileNumberType;
		this.operatorCode = operatorCode;
		this.amount = amount;
		this.pin = pin;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

}
