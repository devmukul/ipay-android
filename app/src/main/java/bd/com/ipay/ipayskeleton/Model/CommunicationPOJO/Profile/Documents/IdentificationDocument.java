package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.Documents;

public class IdentificationDocument {
    private String documentType;
    private String documentIdNumber;
    private String documentVerificationStatus;
    private String documentUrl;
    private String documentName;

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }

    public String getDocumentType() {
        return documentType;
    }

    public String getDocumentIdNumber() {
        return documentIdNumber;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public void setDocumentIdNumber(String documentIdNumber) {
        this.documentIdNumber = documentIdNumber;
    }

    public void setDocumentVerificationStatus(String documentVerificationStatus) {
        this.documentVerificationStatus = documentVerificationStatus;
    }

    public String getDocumentVerificationStatus() {
        return documentVerificationStatus;
    }
}
