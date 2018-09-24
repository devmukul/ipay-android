package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment;

import com.google.gson.annotations.SerializedName;

public class CancelOrderResponse {
    @SerializedName("message")
    private String message;
    @SerializedName("redirectUrl")
    private String redirectUrl;

    public String getMessage() {
        return message;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }
}
