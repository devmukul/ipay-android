package bd.com.ipay.ipayskeleton.SourceOfFund.models;


public class AcceptOrRejectSponsorRequest {

    private String pin;
    private String status;

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public AcceptOrRejectSponsorRequest(String pin, String status) {

        this.pin = pin;
        this.status = status;
    }
}
