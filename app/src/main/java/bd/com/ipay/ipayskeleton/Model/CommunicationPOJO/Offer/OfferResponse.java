
package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Offer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OfferResponse {

    @SerializedName("promotions")
    @Expose
    private List<NewsRoom> newsRooms = null;

    /**
     * No args constructor for use in serialization
     */
    public OfferResponse() {
    }

    /**
     * @param newsRooms
     */
    public OfferResponse(List<NewsRoom> newsRooms) {
        super();
        this.newsRooms = newsRooms;
    }

    public List<NewsRoom> getNewsRooms() {
        return newsRooms;
    }

    public void setNewsRooms(List<NewsRoom> newsRooms) {
        this.newsRooms = newsRooms;
    }
}
