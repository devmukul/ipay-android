package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill;


public class DozeCustomerInfoResponse {
    private String message;
    private String currentPackageRate;
    private String name;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCurrentPackageRate() {
        return currentPackageRate;
    }

    public void setCurrentPackageRate(String currentPackageRate) {
        this.currentPackageRate = currentPackageRate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DozeCustomerInfoResponse(String message, String currentPackageRate, String name) {

        this.message = message;
        this.currentPackageRate = currentPackageRate;
        this.name = name;
    }
}
