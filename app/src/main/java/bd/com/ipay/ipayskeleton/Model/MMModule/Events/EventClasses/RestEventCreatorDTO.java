package bd.com.ipay.ipayskeleton.Model.MMModule.Events.EventClasses;

public class RestEventCreatorDTO {

	public Long id;
	public String ipayAccountId;
	public String ipayAccountName;

	public RestEventCreatorDTO() {
	}

	public Long getId() {
		return id;
	}

	public String getIpayAccountId() {
		return ipayAccountId;
	}

	public String getIpayAccountName() {
		return ipayAccountName;
	}
}
