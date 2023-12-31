package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRoles;

import java.util.List;

import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.UserProfilePictureClass;


public class BusinessManagerInvitationDetailsResponse {
    private String message;
    private long id;
    private String businessName;
    private String roleName;
    private long createdAt;
    private List<UserProfilePictureClass> profilePictures;
    private List<BusinessService> serviceList;

    public BusinessManagerInvitationDetailsResponse(String message, long id, String businessName, String roleName, long createdAt, List<UserProfilePictureClass> profilePictures, List<BusinessService> serviceList) {
        this.message = message;
        this.id = id;
        this.businessName = businessName;
        this.roleName = roleName;
        this.createdAt = createdAt;
        this.profilePictures = profilePictures;
        this.serviceList = serviceList;
    }

    public long getId() {
        return id;
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

    public List<UserProfilePictureClass> getProfilePictures() {
        return profilePictures;
    }

    public List<BusinessService> getServiceList() {
        return serviceList;
    }

    public String getMessage() {
        return message;
    }

}
