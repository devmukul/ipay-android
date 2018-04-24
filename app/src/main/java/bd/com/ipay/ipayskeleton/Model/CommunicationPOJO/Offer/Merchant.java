
package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Offer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Merchant {

    @SerializedName("image_url")
    @Expose
    private String imageUrl;
    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("outlets")
    @Expose
    private List<String> outlets = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Merchant() {
    }

    /**
     * 
     * @param imageUrl
     * @param name
     * @param link
     * @param outlets
     */
    public Merchant(String imageUrl, String link, String name, List<String> outlets) {
        super();
        this.imageUrl = imageUrl;
        this.link = link;
        this.name = name;
        this.outlets = outlets;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getOutlets() {
        return outlets;
    }

    public void setOutlets(List<String> outlets) {
        this.outlets = outlets;
    }

}
