package bd.com.ipay.ipayskeleton.Model.MMModule.Profile;

public class IdentificationDocument {
    public String documentType;
    public String documentIdNumber;

    public IdentificationDocument() {

    }

    public IdentificationDocument(String documentType, String documentIdNumber) {
        this.documentType = documentType;
        this.documentIdNumber = documentIdNumber;
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
}
