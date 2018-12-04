package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Balance;

import java.math.BigDecimal;

public class CreditBalanceResponse {

	private BigDecimal creditLimit = BigDecimal.ZERO;
	private BigDecimal availableCredit = BigDecimal.ZERO;
	private boolean isEntitledForInstantMoney = false;
	private String message;

	public CreditBalanceResponse() {

	}

	public CreditBalanceResponse(BigDecimal creditLimit, BigDecimal availableCredit, boolean isEntitledForInstantMoney, String message) {
		this.creditLimit = creditLimit;
		this.availableCredit = availableCredit;
		this.isEntitledForInstantMoney = isEntitledForInstantMoney;
		this.message = message;
	}

	public BigDecimal getCreditLimit() {
		return creditLimit;
	}

	public void setCreditLimit(BigDecimal creditLimit) {
		this.creditLimit = creditLimit;
	}

	public BigDecimal getAvailableCredit() {
		return availableCredit;
	}

	public void setAvailableCredit(BigDecimal availableCredit) {
		this.availableCredit = availableCredit;
	}

	public boolean isEntitledForInstantMoney() {
		return isEntitledForInstantMoney;
	}

	public void setEntitledForInstantMoney(boolean entitledForInstantMoney) {
		isEntitledForInstantMoney = entitledForInstantMoney;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CreditBalanceResponse that = (CreditBalanceResponse) o;

		if (isEntitledForInstantMoney != that.isEntitledForInstantMoney) return false;
		if (creditLimit != null ? !creditLimit.equals(that.creditLimit) : that.creditLimit != null)
			return false;
		if (availableCredit != null ? !availableCredit.equals(that.availableCredit) : that.availableCredit != null)
			return false;
		return message != null ? message.equals(that.message) : that.message == null;
	}

	@Override
	public int hashCode() {
		int result = creditLimit != null ? creditLimit.hashCode() : 0;
		result = 31 * result + (availableCredit != null ? availableCredit.hashCode() : 0);
		result = 31 * result + (isEntitledForInstantMoney ? 1 : 0);
		result = 31 * result + (message != null ? message.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "RefreshBalanceResponse{" +
				"creditLimit=" + creditLimit +
				", availableCredit=" + availableCredit +
				", isEntitledForInstantMoney=" + isEntitledForInstantMoney +
				", message='" + message + '\'' +
				'}';
	}
}
