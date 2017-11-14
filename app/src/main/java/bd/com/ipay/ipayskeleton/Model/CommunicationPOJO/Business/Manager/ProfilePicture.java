
package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Manager;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfilePicture {

    @SerializedName("quality")
    private String quality;
    @SerializedName("url")
    private String url;

    public ProfilePicture() {
    }

    public ProfilePicture(String quality, String url) {
        super();
        this.quality = quality;
        this.url = url;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
