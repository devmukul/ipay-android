
package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetCustomerInfoResponse implements Serializable
{

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("userType")
    @Expose
    private String userType;
    @SerializedName("allowablePackages")
    @Expose
    private List<AllowablePackage> allowablePackages = null;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public List<AllowablePackage> getAllowablePackages() {
        return allowablePackages;
    }

    public void setAllowablePackages(List<AllowablePackage> allowablePackages) {
        this.allowablePackages = allowablePackages;
    }

}
