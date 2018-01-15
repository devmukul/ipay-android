
package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Manager;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PendingManagerListResponse {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("pendingInvitationList")
    @Expose
    private List<PendingInvitationList> pendingInvitationList = null;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<PendingInvitationList> getPendingInvitationList() {
        return pendingInvitationList;
    }

    public void setPendingInvitationList(List<PendingInvitationList> pendingInvitationList) {
        this.pendingInvitationList = pendingInvitationList;
    }

}
