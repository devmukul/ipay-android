package bd.com.ipay.ipayskeleton.Model.MMModule.NewsFeed;

import java.util.List;

public class GetNewsFeedResponse {

    public List<News> newsFeed;
    public boolean hasNext;

    public GetNewsFeedResponse() {
    }

    public List<News> getNewsFeed() {
        return newsFeed;
    }

    public boolean isHasNext() {
        return hasNext;
    }
}
