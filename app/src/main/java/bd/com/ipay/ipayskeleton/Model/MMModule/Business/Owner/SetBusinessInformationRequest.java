package bd.com.ipay.ipayskeleton.Model.MMModule.Business.Owner;

public class SetBusinessInformationRequest {

    private final String businessName;
    private final int businessType;

    public SetBusinessInformationRequest(String businessName, int businessType) {
        this.businessName = businessName;
        this.businessType = businessType;
    }

    public String getBusinessName() {
        return businessName;
    }

    public int getBusinessType() {
        return businessType;
    }
}
