package bd.com.ipay.ipayskeleton.Model.MMModule.ServiceCharge;

public class GetServiceChargeRequest {

    public int serviceID;
    public int accountType;
    public int accountClass;

    public GetServiceChargeRequest(int serviceID, int accountType, int accountClass) {
        this.serviceID = serviceID;
        this.accountType = accountType;
        this.accountClass = accountClass;
    }
}
