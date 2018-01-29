package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment;

import com.google.gson.annotations.SerializedName;

public class PaymentAcceptRejectOrCancelRequest {

    private final Long requestId;
    private final String transactionId;
    private final String pin;
    private String otp;
    @SerializedName("lat")
    private Double latitude;
    @SerializedName("lng")
    private Double longitude;

    public PaymentAcceptRejectOrCancelRequest(long requestId, String pin) {
        this(requestId, null, pin);
    }

    public PaymentAcceptRejectOrCancelRequest(String transactionId, String pin) {
        this(null, transactionId, pin);
    }


    public PaymentAcceptRejectOrCancelRequest(long requestId) {
        this(requestId, null);
    }

    public PaymentAcceptRejectOrCancelRequest(String transactionId) {
        this(transactionId, null);
    }

    public PaymentAcceptRejectOrCancelRequest(Long requestId, String transactionId, String pin) {
        this.requestId = requestId;
        this.transactionId = transactionId;
        this.pin = pin;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
