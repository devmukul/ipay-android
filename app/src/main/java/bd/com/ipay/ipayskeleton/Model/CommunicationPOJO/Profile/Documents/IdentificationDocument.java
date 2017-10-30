package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.Documents;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class IdentificationDocument implements Parcelable {
    private String documentType;
    private String documentTypeTitle;
    private String documentIdNumber;
    private String documentVerificationStatus;
    private List<DocumentPage> documentPages;
    private String documentName;

    public IdentificationDocument() {

    }

    public IdentificationDocument(String documentType, String documentTypeTitle, String documentIdNumber, String documentVerificationStatus, List<DocumentPage> documentPages, String documentName) {

        this.documentType = documentType;
        this.documentTypeTitle = documentTypeTitle;
        this.documentIdNumber = documentIdNumber;
        this.documentVerificationStatus = documentVerificationStatus;
        this.documentPages = documentPages;
        this.documentName = documentName;
    }

    protected IdentificationDocument(Parcel in) {
        documentType = in.readString();
        documentTypeTitle = in.readString();
        documentIdNumber = in.readString();
        documentVerificationStatus = in.readString();
        documentPages = in.createTypedArrayList(DocumentPage.CREATOR);
        documentName = in.readString();
    }

    public static final Creator<IdentificationDocument> CREATOR = new Creator<IdentificationDocument>() {
        @Override
        public IdentificationDocument createFromParcel(Parcel in) {
            return new IdentificationDocument(in);
        }

        @Override
        public IdentificationDocument[] newArray(int size) {
            return new IdentificationDocument[size];
        }
    };

    public String getDocumentType() {

        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentTypeTitle() {
        return documentTypeTitle;
    }

    public void setDocumentTypeTitle(String documentTypeTitle) {
        this.documentTypeTitle = documentTypeTitle;
    }

    public String getDocumentIdNumber() {
        return documentIdNumber;
    }

    public void setDocumentIdNumber(String documentIdNumber) {
        this.documentIdNumber = documentIdNumber;
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

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    @Override
    public String toString() {
        return "IdentificationDocument{" +
                "documentType='" + documentType + '\'' +
                ", documentTypeTitle='" + documentTypeTitle + '\'' +
                ", documentIdNumber='" + documentIdNumber + '\'' +
                ", documentVerificationStatus='" + documentVerificationStatus + '\'' +
                ", documentPages=" + documentPages +
                ", documentName='" + documentName + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(documentType);
        dest.writeString(documentTypeTitle);
        dest.writeString(documentIdNumber);
        dest.writeString(documentVerificationStatus);
        dest.writeTypedList(documentPages);
        dest.writeString(documentName);
    }
}
