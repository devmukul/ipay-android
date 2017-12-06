package bd.com.ipay.ipayskeleton.Model.BusinessContact;

import java.util.List;

public class TrendingBusinessResponse {

    private String message;
    private List<TrendingBusiness> trendingBusinessList;

    public String getMessage() {
        return message;
    }

    public List<TrendingBusiness> getTrendingBusinessList() {
        return trendingBusinessList;
    }
}
