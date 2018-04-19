
package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Offer;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
     */
    public Promotion(Long expireDate, String imageUrl, List<Merchant> merchants, String period, String subtitle, List<String> termsConditions, String title) {
        super();
        this.expireDate = expireDate;
        this.imageUrl = imageUrl;
        this.merchants = merchants;
        this.period = period;
        this.subtitle = subtitle;
        this.termsConditions = termsConditions;
        this.title = title;
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
                '}';
    }
}
