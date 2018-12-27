
package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RailwayTickets;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetTrainListResponse implements Serializable
{

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("trainList")
    @Expose
    private List<TrainList> trainList = null;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<TrainList> getTrainList() {
        return trainList;
    }

    public void setTrainList(List<TrainList> trainList) {
        this.trainList = trainList;
    }

}
