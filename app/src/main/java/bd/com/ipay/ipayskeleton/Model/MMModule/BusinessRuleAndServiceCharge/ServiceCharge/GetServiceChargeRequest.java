package bd.com.ipay.ipayskeleton.Model.MMModule.BusinessRuleAndServiceCharge.ServiceCharge;

public class GetServiceChargeRequest {

    private final int serviceID;
    private final int accountType;
    private final int accountClass;

    public GetServiceChargeRequest(int serviceID, int accountType, int accountClass) {
        this.serviceID = serviceID;
        this.accountType = accountType;
        this.accountClass = accountClass;
    }
}
