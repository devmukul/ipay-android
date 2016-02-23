package bd.com.ipay.ipayskeleton.Model.MMModule.NewsFeed;

public class News {
    public long id;
    public String title;
    public String description;
    public String subDescription;
    public String imageUrl;
    public String imageThumbnailUrl;
    public String newsLink;

    public News() {
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getSubDescription() {
        return subDescription;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getImageThumbnailUrl() {
        return imageThumbnailUrl;
    }

    public String getNewsLink() {
        return newsLink;
    }
}