
package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Merchants;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetAllTrendingBusinessResponse
{
    private String message;
    private List<TrendingBusinessList> trendingBusinessList = null;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<TrendingBusinessList> getTrendingBusinessList() {
        return trendingBusinessList;
    }

    public void setTrendingBusinessList(List<TrendingBusinessList> trendingBusinessList) {
        this.trendingBusinessList = trendingBusinessList;
    }

}
