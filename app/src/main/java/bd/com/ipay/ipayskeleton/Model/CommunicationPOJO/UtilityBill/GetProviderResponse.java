
package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetProviderResponse implements Serializable
{

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("providerCategories")
    @Expose
    private List<ProviderCategory> providerCategories = null;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ProviderCategory> getProviderCategories() {
        return providerCategories;
    }

    public void setProviderCategories(List<ProviderCategory> providerCategories) {
        this.providerCategories = providerCategories;
    }

}
