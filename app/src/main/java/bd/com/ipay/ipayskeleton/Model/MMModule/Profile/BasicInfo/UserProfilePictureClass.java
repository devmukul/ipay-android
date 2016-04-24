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

    public void setUrl(String url) {
        this.url = url;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }


}