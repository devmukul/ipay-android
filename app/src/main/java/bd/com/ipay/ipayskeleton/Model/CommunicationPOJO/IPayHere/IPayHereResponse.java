
package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.IPayHere;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IPayHereResponse {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("nearbyBusinessResponseList")
    @Expose
    private List<NearbyBusinessResponseList> nearbyBusinessResponseList = null;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<NearbyBusinessResponseList> getNearbyBusinessResponseList() {
        return nearbyBusinessResponseList;
    }

    public void setNearbyBusinessResponseList(List<NearbyBusinessResponseList> nearbyBusinessResponseList) {
        this.nearbyBusinessResponseList = nearbyBusinessResponseList;
    }

}
