package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment;


import com.google.gson.annotations.SerializedName;

public class PaymentRequest {

    private final String mobileNumber;
    private final double amount;
    private final String description;
    private final String pin;
    private final String ref;
    private String otp;
    @SerializedName("lat")
    private Double latitude;
    @SerializedName("lng")
    private Double longitude;

    public PaymentRequest(String mobileNumber, String amount, String description, String pin, String ref, double latitude, double longitude) {
        this.mobileNumber = mobileNumber;
        this.amount = Double.parseDouble(amount);
        this.description = description;
        this.pin = pin;
        this.ref = ref;
        if (latitude != 0.0 && longitude != 0.0) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
