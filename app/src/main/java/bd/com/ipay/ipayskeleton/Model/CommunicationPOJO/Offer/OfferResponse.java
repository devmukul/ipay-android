
package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Offer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OfferResponse {

    @SerializedName("expire_date")
    @Expose
    private Long expire_date;
    @SerializedName("start_date")
    @Expose
    private Long start_date;
    @SerializedName("image_url")
    @Expose
    private String image_url;
    @SerializedName("subtitle")
    @Expose
    private String subtitle;
    @SerializedName("title")
    @Expose
    private String title;

    public OfferResponse() {
    }

    public OfferResponse(Long expire_date, Long start_date, String image_url, String subtitle, String title) {
        this.expire_date = expire_date;
        this.start_date = start_date;
        this.image_url = image_url;
        this.subtitle = subtitle;
        this.title = title;
    }

    public Long getExpire_date() {
        return expire_date;
    }

    public void setExpire_date(Long expire_date) {
        this.expire_date = expire_date;
    }

    public Long getStart_date() {
        return start_date;
    }

    public void setStart_date(Long start_date) {
        this.start_date = start_date;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "OfferResponse{" +
                "expire_date=" + expire_date +
                ", image_url='" + image_url + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
