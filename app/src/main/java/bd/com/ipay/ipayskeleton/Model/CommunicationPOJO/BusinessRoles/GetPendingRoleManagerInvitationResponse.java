package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRoles;


import java.util.List;

import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Notification.BusinessRoleManagerInvitation;

public class GetPendingRoleManagerInvitationResponse {

    private List<BusinessRoleManagerInvitation> invitationList;
    private String Message;

    public List<BusinessRoleManagerInvitation> getInvitationList() {
        return invitationList;
    }

    public String getMessage() {
        return Message;
    }
}
