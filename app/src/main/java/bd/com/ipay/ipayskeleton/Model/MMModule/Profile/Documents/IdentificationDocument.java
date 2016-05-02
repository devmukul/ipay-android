package bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Documents;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class IdentificationDocument {
    public String documentType;
    public String documentIdNumber;
    public String documentVerificationStatus;

    public IdentificationDocument() {

    }

    public IdentificationDocument(String documentType, String documentIdNumber, String documentVerificationStatus) {
        this.documentType = documentType;
        this.documentIdNumber = documentIdNumber;
        this.documentVerificationStatus = documentVerificationStatus;
    }

    public IdentificationDocument(String documentType, String documentIdNumber) {
        this.documentType = documentType;
        this.documentIdNumber = documentIdNumber;
        this.documentVerificationStatus = Constants.ACCOUNT_VERIFICATION_STATUS_NOT_VERIFIED;
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
