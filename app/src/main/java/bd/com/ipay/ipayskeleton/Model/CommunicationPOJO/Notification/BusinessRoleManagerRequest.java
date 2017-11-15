package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Notification;


import java.util.List;

import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Manager.ProfilePicture;

public class BusinessRoleManagerRequest implements Notification {
    private long id;
    private long managerAccountId;
    private String managerMobileNumber;
    private String roleName;
    private long createdAt;
    private List<ProfilePicture> proflePictures;

    @Override
    public String getNotificationTitle() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getImageUrl() {
        return null;
    }

    public long getId() {
        return id;
    }

    public long getManagerAccountId() {
        return managerAccountId;
    }

    public String getManagerMobileNumber() {
        return managerMobileNumber;
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
        return 0;
    }

    @Override
    public int getNotificationType() {
        return 0;
    }
}
