
package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Offer;

import java.util.List;

public class Promotion {
    private Long expire_date;
    private String image_url;
    private List<Merchant> merchants = null;
    private String period;
    private String subtitle;
    private List<String> terms_conditions = null;
    private String title;
    private String url;


    public Promotion() {
    }

    public Promotion(Long expire_date, String image_url, List<Merchant> merchants, String period, String subtitle, List<String> terms_conditions, String title, String url) {
        this.expire_date = expire_date;
        this.image_url = image_url;
        this.merchants = merchants;
        this.period = period;
        this.subtitle = subtitle;
        this.terms_conditions = terms_conditions;
        this.title = title;
        this.url = url;
    }

    public Long getExpire_date() {
        return expire_date;
    }

    public void setExpire_date(Long expire_date) {
        this.expire_date = expire_date;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
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

    public List<String> getTerms_conditions() {
        return terms_conditions;
    }

    public void setTerms_conditions(List<String> terms_conditions) {
        this.terms_conditions = terms_conditions;
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
                "expire_date=" + expire_date +
                ", image_url='" + image_url + '\'' +
                ", merchants=" + merchants +
                ", period='" + period + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", terms_conditions=" + terms_conditions +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
