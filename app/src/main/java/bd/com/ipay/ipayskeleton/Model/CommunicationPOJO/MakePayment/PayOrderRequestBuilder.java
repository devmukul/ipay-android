package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment;


import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class PayOrderRequestBuilder {

    private String orderId;

    public PayOrderRequestBuilder(String orderId) {
        this.orderId = orderId;
    }

    public String getGeneratedUri() {
        String url = Constants.URL_PAY_BY_DEEP_LINK.replace("orderId", orderId);
        return Constants.BASE_URL_PG + url;
    }
}
