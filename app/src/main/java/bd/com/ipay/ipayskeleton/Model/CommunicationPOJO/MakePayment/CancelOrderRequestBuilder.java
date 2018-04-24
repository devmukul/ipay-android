package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment;


import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class CancelOrderRequestBuilder {
    private String orderId;

    public CancelOrderRequestBuilder(String orderId) {
        this.orderId = orderId;
    }

    public String getGeneratedUri() {
        return Constants.BASE_URL_PG + orderId;
    }
}
