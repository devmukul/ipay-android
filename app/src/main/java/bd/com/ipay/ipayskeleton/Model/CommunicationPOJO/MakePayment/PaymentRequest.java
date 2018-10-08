package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment;


import com.google.gson.annotations.SerializedName;

public class PaymentRequest {

    private final String mobileNumber;
    private final double amount;
    private final String description;
    private final String ref;
    private final Long outletId;
    private String pin;
    private String otp;
    @SerializedName("lat")
    private Double latitude;
    @SerializedName("lng")
    private Double longitude;

    public PaymentRequest(String mobileNumber, String amount, String description, String ref, Long outletId, double latitude, double longitude) {
        this.mobileNumber = mobileNumber;
        this.amount = Double.parseDouble(amount);
        this.description = description;
        this.ref = ref;
        this.outletId = outletId;
        if (latitude != 0.0 && longitude != 0.0) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
