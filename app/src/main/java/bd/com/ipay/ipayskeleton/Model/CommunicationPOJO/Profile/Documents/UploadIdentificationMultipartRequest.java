package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.Documents;

import android.net.Uri;

/**
 * Created by sajid.shahriar on 10/29/17.
 */

public class UploadIdentificationMultipartRequest {
    private String documentName;
    private String documentId;
    private String documentType;
    private Uri frontSideDocumentUri;
    private Uri backSideDocumentUri;

    public UploadIdentificationMultipartRequest() {
    }

    public UploadIdentificationMultipartRequest(String documentName, String documentId, String documentType, Uri frontSideDocumentUri, Uri backSideDocumentUri) {

        this.documentName = documentName;
        this.documentId = documentId;
        this.documentType = documentType;
        this.frontSideDocumentUri = frontSideDocumentUri;
        this.backSideDocumentUri = backSideDocumentUri;
    }

    public String getDocumentName() {

        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public Uri getFrontSideDocumentUri() {
        return frontSideDocumentUri;
    }

    public void setFrontSideDocumentUri(Uri frontSideDocumentUri) {
        this.frontSideDocumentUri = frontSideDocumentUri;
    }

    public Uri getBackSideDocumentUri() {
        return backSideDocumentUri;
    }

    public void setBackSideDocumentUri(Uri backSideDocumentUri) {
        this.backSideDocumentUri = backSideDocumentUri;
    }

    @Override
    public String toString() {
        return "UploadIdentificationMultipartRequest{" +
                "documentName='" + documentName + '\'' +
                ", documentId='" + documentId + '\'' +
                ", documentType='" + documentType + '\'' +
                ", frontSideDocumentUri=" + frontSideDocumentUri +
                ", backSideDocumentUri=" + backSideDocumentUri +
                '}';
    }
}
