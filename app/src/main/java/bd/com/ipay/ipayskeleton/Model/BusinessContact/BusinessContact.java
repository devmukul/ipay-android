package bd.com.ipay.ipayskeleton.Model.BusinessContact;

public class BusinessContact {
    private String businessName;
    private String businessType;
    private String mobileNumber;
    private String profilePictureUrl;
    private String AddressString;

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

    public String getAddressString() {
        return AddressString;
    }

    public void setAddressString(String addressString) {
        AddressString = addressString;
    }
}
