package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill;


public class DPDCUserInfoGetRequest {
    private String accountNumber;
    private String locationCode;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public DPDCUserInfoGetRequest(String accountNumber, String locationCode) {

        this.accountNumber = accountNumber;
        this.locationCode = locationCode;
    }
}
