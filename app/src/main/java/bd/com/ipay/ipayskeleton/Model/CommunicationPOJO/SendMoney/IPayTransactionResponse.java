package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.SendMoney;

import com.google.gson.annotations.SerializedName;

public class IPayTransactionResponse {

	@SerializedName("message")
	private String message;
	@SerializedName("otpValidFor")
	private long otpValidFor;
	@SerializedName("statusCode")
	private int statusCode;
	@SerializedName("transactionId")
	private String transactionId;

	public IPayTransactionResponse() {
	}

	public IPayTransactionResponse(String message, long otpValidFor, int statusCode, String transactionId) {

		this.message = message;
		this.otpValidFor = otpValidFor;
		this.statusCode = statusCode;
		this.transactionId = transactionId;
	}

	public String getMessage() {

		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getOtpValidFor() {
		return otpValidFor;
	}

	public void setOtpValidFor(long otpValidFor) {
		this.otpValidFor = otpValidFor;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		IPayTransactionResponse that = (IPayTransactionResponse) o;

		if (otpValidFor != that.otpValidFor) return false;
		if (statusCode != that.statusCode) return false;
		if (message != null ? !message.equals(that.message) : that.message != null) return false;
		return transactionId != null ? transactionId.equals(that.transactionId) : that.transactionId == null;
	}

	@Override
	public int hashCode() {
		int result = message != null ? message.hashCode() : 0;
		result = 31 * result + (int) (otpValidFor ^ (otpValidFor >>> 32));
		result = 31 * result + statusCode;
		result = 31 * result + (transactionId != null ? transactionId.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "IPayTransactionResponse{" +
				"message='" + message + '\'' +
				", otpValidFor=" + otpValidFor +
				", statusCode=" + statusCode +
				", transactionId='" + transactionId + '\'' +
				'}';
	}
}
