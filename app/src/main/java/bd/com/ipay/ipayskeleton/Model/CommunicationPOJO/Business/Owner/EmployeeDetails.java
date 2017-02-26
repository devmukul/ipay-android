package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Owner;

public class EmployeeDetails {
    private String designation;
    private long id;
    private String mobileNumber;
    private String name;
    private int roleId;
    private String profilePictureUrl;
    private String status;

    public String getDesignation() {
        if(designation != null) return designation;
        else return "";
    }

    public long getId() {
        return id;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getName() {
        return name;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public String getStatus() {
        return status;
    }

    public int getRoleId() {
        return roleId;
    }
}
