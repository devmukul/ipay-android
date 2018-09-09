
package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Merchants;

import java.util.List;

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
