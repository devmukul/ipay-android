package bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo;

public class UserProfilePictureClass {

    private String url;
    private String quality;

    public UserProfilePictureClass(String url, String quality) {
        this.url = url;
        this.quality = quality;
    }

    public String getUrl() {
        return url;
    }

    public String getQuality() {
        return quality;
    }

    @Override
    public String toString() {
        return "UserProfilePictureClass{" +
                "url='" + url + '\'' +
                ", quality='" + quality + '\'' +
                '}';
    }
}