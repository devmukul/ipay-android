package bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment;

import android.net.Uri;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class GetSingleInvoiceRequestBuilder {
    private final long invoiceId;
    private String generatedUri;

    public GetSingleInvoiceRequestBuilder(long invoiceId) {
        this.invoiceId = invoiceId;
        generateUri();
    }

    private void generateUri() {
        Uri uri = Uri.parse(Constants.BASE_URL_SM + Constants.URL_PAYMENT_GET_INVOICE + invoiceId +"/");
        setGeneratedUri(uri.toString());
    }

    public String getGeneratedUri() {
        return generatedUri;
    }

    private void setGeneratedUri(String generatedUri) {
        this.generatedUri = generatedUri;
    }
}
