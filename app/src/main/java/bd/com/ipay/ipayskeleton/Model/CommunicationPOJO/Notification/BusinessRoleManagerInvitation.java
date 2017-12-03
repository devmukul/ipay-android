package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Notification;


import java.util.List;

import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Manager.ProfilePicture;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class BusinessRoleManagerInvitation implements Notification {
    private long id;
    private long businessAccountId;
    private String businessName;
    private String roleName;
    private long createdAt;
    private List<ProfilePicture> profilePictures;

    @Override
    public String getNotificationTitle() {
        return "<b>" + "<font color='#000000'>" + getBusinessName() + "</b>" + "</font>" + " Has made you an  <b><font color='#000000'>" +
                "Admin</b></font> of their business account";
    }

    @Override
    public String getName() {
        return roleName;
    }

    @Override
    public String getImageUrl() {
        return profilePictures.get(0).getUrl();
    }

    public long getId() {
        return id;
    }

    public long getBusinessAccountId() {
        return businessAccountId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public String getRoleName() {
        return roleName;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public List<ProfilePicture> getProflePictures() {
        return profilePictures;
    }

    @Override
    public long getTime() {
        return createdAt;
    }

    @Override
    public int getNotificationType() {
        return Constants.NOTIFICATION_TYPE_PENDING_ROLE_MANAGER_REQUEST;
    }
}

