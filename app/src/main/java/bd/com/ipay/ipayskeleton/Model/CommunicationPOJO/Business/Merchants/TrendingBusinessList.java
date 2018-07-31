
package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Merchants;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrendingBusinessList
{


    private List<BusinessList> businessList = null;
    private String businessType;

    public List<BusinessList> getBusinessList() {
        return businessList;
    }

    public void setBusinessList(List<BusinessList> businessList) {
        this.businessList = businessList;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

}
