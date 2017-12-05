package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRoles;

import java.util.List;

public class BusinessAccountDetails {
    private String message;
    private long id;
    private long businessAccountId;
    private String businessName;
    private String businessProfilePictureUrl;
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

    public String getBusinessProfilePictureUrl() {
        return businessProfilePictureUrl;
    }

    public String getRoleName() {
        return roleName;
    }

    public List<BusinessService> getServiceList() {
        return serviceList;
    }
}
