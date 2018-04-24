package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment;


import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class GetOrderDetailsRequestBuilder {

    private String orderId;

    public GetOrderDetailsRequestBuilder(String orderId) {
        this.orderId = orderId;
    }

    public String getGeneratedUri() {
        String url = Constants.URL_GET_ORDER_DETAILS.replace("orderId", orderId);
        return Constants.BASE_URL_PG + url;
    }
}
