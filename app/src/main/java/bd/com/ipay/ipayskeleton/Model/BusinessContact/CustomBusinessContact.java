package bd.com.ipay.ipayskeleton.Model.BusinessContact;

public class CustomBusinessContact {

    private String typeInList;
    private String businessName;
    private String mobileNumber;
    private String outletName;
    private Long outletId;
    private String businessType;
    private String profilePictureUrl;
    private String AddressString;
    private String thanaString;
    private String districtString;

    public CustomBusinessContact() {
    }

    public CustomBusinessContact(String typeInList, String businessName, String mobileNumber, String outletName, Long outletId, String businessType, String profilePictureUrl, String addressString, String thanaString, String districtString) {
        this.typeInList = typeInList;
        this.businessName = businessName;
        this.mobileNumber = mobileNumber;
        this.outletName = outletName;
        this.outletId = outletId;
        this.businessType = businessType;
        this.profilePictureUrl = profilePictureUrl;
        AddressString = addressString;
        this.thanaString = thanaString;
        this.districtString = districtString;
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

    public String getThanaString() {
        return thanaString;
    }

    public void setThanaString(String thanaString) {
        this.thanaString = thanaString;
    }

    public String getDistrictString() {
        return districtString;
    }

    public void setDistrictString(String districtString) {
        this.districtString = districtString;
    }

    public String getOutletName() {
        return outletName;
    }

    public void setOutletName(String outletName) {
        this.outletName = outletName;
    }

    public Long getOutletId() {
        return outletId;
    }

    public void setOutletId(Long outletId) {
        this.outletId = outletId;
    }
    public String getTypeInList() {
        return typeInList;
    }

    public void setTypeInList(String typeInList) {
        this.typeInList = typeInList;
    }
}
