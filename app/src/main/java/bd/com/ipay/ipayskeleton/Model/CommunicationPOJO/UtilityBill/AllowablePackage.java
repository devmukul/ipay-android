
package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AllowablePackage implements Serializable
{

    @SerializedName("packageName")
    @Expose
    private String packageName;
    @SerializedName("amount")
    @Expose
    private Long amount;
    @SerializedName("validity")
    @Expose
    private String validity;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

}
