
package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Bank;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DocumentPage {

    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("pageNumber")
    @Expose
    private Long pageNumber;

    /**
     * No args constructor for use in serialization
     * 
     */
    public DocumentPage() {
    }

    /**
     * 
     * @param pageNumber
     * @param url
     */
    public DocumentPage(String url, Long pageNumber) {
        super();
        this.url = url;
        this.pageNumber = pageNumber;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Long pageNumber) {
        this.pageNumber = pageNumber;
    }

}
