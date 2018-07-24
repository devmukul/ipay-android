
package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ProviderCategory implements Serializable
{

    @SerializedName("categoryName")
    @Expose
    private String categoryName;
    @SerializedName("providers")
    @Expose
    private List<Provider> providers = null;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<Provider> getProviders() {
        return providers;
    }

    public void setProviders(List<Provider> providers) {
        this.providers = providers;
    }

}
