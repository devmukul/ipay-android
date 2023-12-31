package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment;

import android.net.Uri;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class GetSinglePaymentRequestDetailRequestBuilder {
    private final long invoiceId;
    private String generatedUri;

    public GetSinglePaymentRequestDetailRequestBuilder(long invoiceId) {
        this.invoiceId = invoiceId;
        generateUri();
    }

    private void generateUri() {
        Uri uri = Uri.parse(Constants.BASE_URL_SM + Constants.URL_GET_SINGLE_REQUEST_PAYMENT + invoiceId +"/");
        setGeneratedUri(uri.toString());
    }

    public String getGeneratedUri() {
        return generatedUri;
    }

    private void setGeneratedUri(String generatedUri) {
        this.generatedUri = generatedUri;
    }
}
