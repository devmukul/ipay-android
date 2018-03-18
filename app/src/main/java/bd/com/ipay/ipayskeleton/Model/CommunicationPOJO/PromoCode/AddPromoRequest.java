package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.PromoCode;

public class AddPromoRequest {

    private final String promoCode;

    public AddPromoRequest(String promoCode) {
        this.promoCode = promoCode;
    }

    public String getPromoCode() {
        return promoCode;
    }
}