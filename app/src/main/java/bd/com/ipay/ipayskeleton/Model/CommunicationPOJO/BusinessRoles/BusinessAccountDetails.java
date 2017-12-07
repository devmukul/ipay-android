package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRoles;

import java.util.List;

import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Manager.ProfilePicture;

public class BusinessAccountDetails {
    private String message;
    private long id;
    private long businessAccountId;
    private String businessName;
    private List<ProfilePicture> profilePictures = null;
    private String roleName;
    private long createdAt;
    private List<BusinessService> serviceList;

    public long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getBusinessName() {
        return businessName;
    }

    public long getBusinessAccountId() {
        return businessAccountId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public List<ProfilePicture> getProfilePictures() {
        return profilePictures;
    }

    public String getRoleName() {
        return roleName;
    }

    public List<BusinessService> getServiceList() {
        return serviceList;
    }
}
