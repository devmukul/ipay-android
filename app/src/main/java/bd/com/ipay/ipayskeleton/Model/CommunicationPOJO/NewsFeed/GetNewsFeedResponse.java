package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.NewsFeed;

import java.util.List;

class GetNewsFeedResponse {

    private List<News> newsFeed;
    private boolean hasNext;

    public GetNewsFeedResponse() {
    }

    public List<News> getNewsFeed() {
        return newsFeed;
    }

    public boolean isHasNext() {
        return hasNext;
    }
}
