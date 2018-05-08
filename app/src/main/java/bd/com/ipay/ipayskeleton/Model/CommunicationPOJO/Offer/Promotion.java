
package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Offer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Promotion {

    @SerializedName("expire_date")
    @Expose
    private Long expireDate;
    @SerializedName("image_url")
    @Expose
    private String imageUrl;
    @SerializedName("merchants")
    @Expose
    private List<Merchant> merchants = null;
    @SerializedName("period")
    @Expose
    private String period;
    @SerializedName("subtitle")
    @Expose
    private String subtitle;
    @SerializedName("terms_conditions")
    @Expose
    private List<String> termsConditions = null;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("url")
    @Expose
    private String url;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Promotion() {
    }

    /**
     *
     * @param merchants
     * @param title
     * @param imageUrl
     * @param expireDate
     * @param subtitle
     * @param termsConditions
     * @param period
     * @param url
     */

    public Promotion(Long expireDate, String imageUrl, List<Merchant> merchants, String period, String subtitle, List<String> termsConditions, String title, String url) {
        this.expireDate = expireDate;
        this.imageUrl = imageUrl;
        this.merchants = merchants;
        this.period = period;
        this.subtitle = subtitle;
        this.termsConditions = termsConditions;
        this.title = title;
        this.url = url;
    }


    public Long getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Long expireDate) {
        this.expireDate = expireDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<Merchant> getMerchants() {
        return merchants;
    }

    public void setMerchants(List<Merchant> merchants) {
        this.merchants = merchants;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public List<String> getTermsConditions() {
        return termsConditions;
    }

    public void setTermsConditions(List<String> termsConditions) {
        this.termsConditions = termsConditions;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Promotion{" +
                "expireDate=" + expireDate +
                ", imageUrl='" + imageUrl + '\'' +
                ", merchants=" + merchants +
                ", period='" + period + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", termsConditions=" + termsConditions +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
