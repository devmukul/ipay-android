package bd.com.ipay.ipayskeleton.Model.SqLiteDatabase;

public class BusinessAccountEntry {
    public String mobileNumber;
    public String businessName;
    public String email;
    public int businessType;
    public String profilePictureUrl;
    public String profilePictureUrlMedium;
    public String profilePictureUrlHigh;
    public int businessId;

    public BusinessAccountEntry(String mobileNumber, String businessName, String email,
                                int businessType, String profilePictureUrl, String profilePictureUrlMedium,
                                String profilePictureUrlHigh, int businessId) {
        this.mobileNumber = mobileNumber;
        this.businessName = businessName;
        this.email = email;
        this.businessType = businessType;
        this.profilePictureUrl = profilePictureUrl;
        this.profilePictureUrlMedium = profilePictureUrlMedium;
        this.profilePictureUrlHigh = profilePictureUrlHigh;
        this.businessId = businessId;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getBusinessType() {
        return businessType;
    }

    public void setBusinessType(int businessType) {
        this.businessType = businessType;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public int getBusinessId() {
        return businessId;
    }

    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }

    public String getProfilePictureUrlMedium() {
        return profilePictureUrlMedium;
    }

    public void setProfilePictureUrlMedium(String profilePictureUrlMedium) {
        this.profilePictureUrlMedium = profilePictureUrlMedium;
    }

    public String getProfilePictureUrlHigh() {
        return profilePictureUrlHigh;
    }

    public void setProfilePictureUrlHigh(String profilePictureUrlHigh) {
        this.profilePictureUrlHigh = profilePictureUrlHigh;
    }

    @Override
    public String toString() {
        return "BusinessAccountEntry{" +
                "mobileNumber='" + mobileNumber + '\'' +
                ", businessName='" + businessName + '\'' +
                ", email='" + email + '\'' +
                ", businessType=" + businessType +
                ", profilePictureUrl='" + profilePictureUrl + '\'' +
                ", profilePictureUrlMedium='" + profilePictureUrlMedium + '\'' +
                ", profilePictureUrlHigh='" + profilePictureUrlHigh + '\'' +
                ", businessId=" + businessId +
                '}';
    }
}
