package bd.com.ipay.android.model;

public class TransactionServiceFilterOption {
	private final int serviceId;
	private final String serviceName;

	public TransactionServiceFilterOption(int serviceId, String serviceName) {
		this.serviceId = serviceId;
		this.serviceName = serviceName;
	}

	public int getServiceId() {
		return serviceId;
	}

	public String getServiceName() {
		return serviceName;
	}
}