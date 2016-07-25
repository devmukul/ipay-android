package bd.com.ipay.ipayskeleton.Model.MMModule.Business.Owner;

public class SetBusinessInformationRequest {

    private final String businessName;
    private final int businessType;
    private final String email;
    private final String mobileNumber;

    public SetBusinessInformationRequest(String businessName, int businessType, String email, String mobileNumber) {
        this.businessName = businessName;
        this.businessType = businessType;
        this.email = email;
        this.mobileNumber = mobileNumber;
    }

    public String getBusinessName() {
        return businessName;
    }

    public int getBusinessType() {
        return businessType;
    }

    public String getEmail() {
        return email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }
}
