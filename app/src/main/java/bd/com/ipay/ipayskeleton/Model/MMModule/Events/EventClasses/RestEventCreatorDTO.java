package bd.com.ipay.ipayskeleton.Model.MMModule.Events.EventClasses;

public class RestEventCreatorDTO {

	private Long id;
	private String ipayAccountId;
	private String ipayAccountName;

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
