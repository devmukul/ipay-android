package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.NewsFeed;

class News {
    private long id;
    private String title;
    private String description;
    private String subDescription;
    private String imageUrl;
    private String imageThumbnailUrl;
    private String newsLink;

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