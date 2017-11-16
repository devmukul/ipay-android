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
    private List<ProfilePicture> proflePictures;

    @Override
    public String getNotificationTitle() {
        return "Added you as a manager";
    }

    @Override
    public String getName() {
        return roleName;
    }

    @Override
    public String getImageUrl() {
        return proflePictures.get(0).getUrl();
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
        return proflePictures;
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

