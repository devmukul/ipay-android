package bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Documents;

import android.net.Uri;

public class DocumentPreviewDetails {
    private String documentType;
    private String documentTypeName;
    private String verificationStatus;
    private String documentUrl;

    private Uri selectedDocumentUri = null;
    private String selectedFilePath = "";
    private String documentId = "";
    private boolean mIsViewOpen;

    public DocumentPreviewDetails() {
        this.mIsViewOpen = false;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentTypeName() {
        return documentTypeName;
    }

    public void setDocumentTypeName(String documentTypeName) {
        this.documentTypeName = documentTypeName;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }

    public boolean ismIsViewOpen() {
        return mIsViewOpen;
    }

    public void setmIsViewOpen(boolean mIsViewOpen) {
        this.mIsViewOpen = mIsViewOpen;
    }

    public Uri getSelectedDocumentUri() {
        return selectedDocumentUri;
    }

    public void setSelectedDocumentUri(Uri selectedDocumentUri) {
        this.selectedDocumentUri = selectedDocumentUri;
    }

    public String getSelectedFilePath() {
        return selectedFilePath;
    }

    public void setSelectedFilePath(String selectedFilePath) {
        this.selectedFilePath = selectedFilePath;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public boolean isViewOpen() {
        return mIsViewOpen;
    }

    public void setIsViewOpen(boolean mIsViewOpen) {
        this.mIsViewOpen = mIsViewOpen;
    }
}
