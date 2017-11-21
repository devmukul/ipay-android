
package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Manager;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfilePicture {

    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("quality")
    @Expose
    private String quality;

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
