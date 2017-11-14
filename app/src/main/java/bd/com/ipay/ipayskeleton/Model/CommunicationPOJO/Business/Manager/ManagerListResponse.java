
package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Manager;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ManagerListResponse {

    @SerializedName("managerList")
    private List<ManagerList> managerList = null;

    public ManagerListResponse() {
    }

    public ManagerListResponse(List<ManagerList> managerList) {
        super();
        this.managerList = managerList;
    }

    public List<ManagerList> getManagerList() {
        return managerList;
    }

    public void setManagerList(List<ManagerList> managerList) {
        this.managerList = managerList;
    }


}
