
package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Bank;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BankDocument {

    @SerializedName("documentType")
    @Expose
    private String documentType;
    @SerializedName("documentVerificationStatus")
    @Expose
    private String documentVerificationStatus;
    @SerializedName("documentPages")
    @Expose
    private List<DocumentPage> documentPages = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public BankDocument() {
    }

    /**
     * 
     * @param documentType
     * @param documentVerificationStatus
     * @param documentPages
     */
    public BankDocument(String documentType, String documentVerificationStatus, List<DocumentPage> documentPages) {
        super();
        this.documentType = documentType;
        this.documentVerificationStatus = documentVerificationStatus;
        this.documentPages = documentPages;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentVerificationStatus() {
        return documentVerificationStatus;
    }

    public void setDocumentVerificationStatus(String documentVerificationStatus) {
        this.documentVerificationStatus = documentVerificationStatus;
    }

    public List<DocumentPage> getDocumentPages() {
        return documentPages;
    }

    public void setDocumentPages(List<DocumentPage> documentPages) {
        this.documentPages = documentPages;
    }

}
