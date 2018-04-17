
package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Offer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OfferResponse {

    @SerializedName("promotions")
    @Expose
    private List<Promotion> promotions = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public OfferResponse() {
    }

    /**
     *
     * @param promotions
     */
    public OfferResponse(List<Promotion> promotions) {
        super();
        this.promotions = promotions;
    }

    public List<Promotion> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<Promotion> promotions) {
        this.promotions = promotions;
    }

}
