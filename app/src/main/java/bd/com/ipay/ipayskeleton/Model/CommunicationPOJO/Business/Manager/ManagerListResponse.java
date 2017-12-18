
package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Manager;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ManagerListResponse {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("managerList")
    @Expose
    private List<ManagerList> managerList = null;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ManagerList> getManagerList() {
        return managerList;
    }

    public void setManagerList(List<ManagerList> managerList) {
        this.managerList = managerList;
    }

}
