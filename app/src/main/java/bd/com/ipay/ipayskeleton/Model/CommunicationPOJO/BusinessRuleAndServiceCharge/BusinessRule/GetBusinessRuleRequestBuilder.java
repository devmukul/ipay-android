package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule;

import android.net.Uri;

import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;

public class GetBusinessRuleRequestBuilder {

	private int serviceId;
	private String generatedUri;

	public GetBusinessRuleRequestBuilder(int serviceId) {
		generateUri(serviceId);
	}

	private void generateUri(int serviceId) {
		final Uri uri;
		if (serviceId == ServiceIdConstants.UTILITY_BILL_PAYMENT) {
			uri = Uri.parse(Constants.BASE_URL_SM + Constants.URL_BUSINESS_RULE_V2 + "/" + serviceId);
		} else {
			uri = Uri.parse(Constants.BASE_URL_SM + Constants.URL_BUSINESS_RULE + "/" + serviceId);
		}
		setGeneratedUri(uri.toString());
	}

	public String getGeneratedUri() {
		return generatedUri;
	}

	private void setGeneratedUri(String generatedUri) {
		this.generatedUri = generatedUri;
	}
}