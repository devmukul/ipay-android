package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.SendMoney;

import com.google.gson.annotations.SerializedName;

public class IPayTransactionResponse {

    @SerializedName("message")
    private String message;
    @SerializedName("otpValidFor")
    private long otpValidFor;

    public IPayTransactionResponse() {

    }

    public void setMessage(String message) {
        this.message = message;
    }


    public long getOtpValidFor() {
        return otpValidFor;
    }

    public String getMessage() {
        return message;
    }
}
