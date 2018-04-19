
package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.IPayHere;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NearbyBusinessResponseList {

    @SerializedName("accountId")
    @Expose
    private Long accountId;
    @SerializedName("coordinate")
    @Expose
    private Coordinate coordinate;
    @SerializedName("businessName")
    @Expose
    private String businessName;
    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
