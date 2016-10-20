package bd.com.ipay.ipayskeleton.Model.MMModule.Business.Employee;

public class BusinessContact {

    private String businessName;
    private String businessType;
    private String email;
    private String mobileNumber;
    private String profilePictureUrl;

    public BusinessContact(String businessName, String businessType, String email, String mobileNumber, String profilePictureUrl) {
        this.businessName = businessName;
        this.businessType = businessType;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.profilePictureUrl = profilePictureUrl;
    }


    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
}
