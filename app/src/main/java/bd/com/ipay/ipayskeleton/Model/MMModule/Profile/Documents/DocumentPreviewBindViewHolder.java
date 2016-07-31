package bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Documents;

import android.net.Uri;

import java.net.URI;

public class DocumentPreviewBindViewHolder {
    private Uri mSelectedDocumentUri=null;
    private String mSelectedfilePath="";
    private String mDocumentId="";
    private boolean mIsViewOpen;

    public DocumentPreviewBindViewHolder() {
        this.mIsViewOpen=false;
    }

    public Uri getmSelectedDocumentUri() {
        return mSelectedDocumentUri;
    }

    public void setmSelectedDocumentUri(Uri mSelectedDocumentUri) {
        this.mSelectedDocumentUri = mSelectedDocumentUri;
    }

    public String getmSelectedfilePath() {
        return mSelectedfilePath;
    }

    public void setmSelectedfilePath(String mSelectedfilePath) {
        this.mSelectedfilePath = mSelectedfilePath;
    }

    public String getmDocumentId() {
        return mDocumentId;
    }

    public void setmDocumentId(String mDocumentId) {
        this.mDocumentId = mDocumentId;
    }

    public boolean isViewOpen() {
        return mIsViewOpen;
    }

    public void setIsViewOpen(boolean mIsViewOpen) {
        this.mIsViewOpen = mIsViewOpen;
    }
}
