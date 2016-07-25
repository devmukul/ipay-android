package bd.com.ipay.ipayskeleton.Model.MMModule.BusinessRuleAndServiceCharge.BusinessRule;

import android.net.Uri;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class GetBusinessRuleRequestBuilder {

    private int serviceId;
    private String generatedUri;

    public GetBusinessRuleRequestBuilder(int serviceId) {
        generateUri(serviceId);
    }

    private void generateUri(int serviceId) {
        Uri uri = Uri.parse(Constants.BASE_URL_SM + Constants.URL_BUSINESS_RULE+"/"+serviceId);
        setGeneratedUri(uri.toString());
    }

    public String getGeneratedUri() {
        return generatedUri;
    }

    private void setGeneratedUri(String generatedUri) {
        this.generatedUri = generatedUri;
    }
}