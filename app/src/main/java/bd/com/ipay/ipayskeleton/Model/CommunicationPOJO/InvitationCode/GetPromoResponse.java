
package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.InvitationCode;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetPromoResponse implements Serializable
{

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("invitationCode")
    @Expose
    private String invitationCode;

    public GetPromoResponse() {
    }

    public GetPromoResponse(String message, String invitationCode) {
        super();
        this.message = message;
        this.invitationCode = invitationCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getInvitationCode() {
        return invitationCode;
    }

    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }

    @Override
    public String toString() {
        return "GetPromoResponse{" +
                "message='" + message + '\'' +
                ", invitationCode='" + invitationCode + '\'' +
                '}';
    }
}
