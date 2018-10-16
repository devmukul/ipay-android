package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Owner;

public class SetBusinessInformationRequest {

    private final String businessName;
    private final String companyName;
    private final int businessType;
    private final String mobileNumber;

    public SetBusinessInformationRequest(String businessName, String companyName, int businessType, String mobileNumber) {
        this.businessName = businessName;
        this.companyName = companyName;
        this.businessType = businessType;
        this.mobileNumber = mobileNumber;
    }

    public String getBusinessName() {
        return businessName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public int getBusinessType() {
        return businessType;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }
}
