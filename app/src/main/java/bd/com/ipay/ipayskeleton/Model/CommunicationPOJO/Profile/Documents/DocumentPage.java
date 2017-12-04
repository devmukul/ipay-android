package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.Documents;

import android.os.Parcel;
import android.os.Parcelable;

public class DocumentPage implements Parcelable {
    private String url;
    private int pageNumber;

    public DocumentPage() {
    }

    public DocumentPage(String url, int pageNumber) {

        this.url = url;
        this.pageNumber = pageNumber;
    }

    protected DocumentPage(Parcel in) {
        url = in.readString();
        pageNumber = in.readInt();
    }

    public static final Creator<DocumentPage> CREATOR = new Creator<DocumentPage>() {
        @Override
        public DocumentPage createFromParcel(Parcel in) {
            return new DocumentPage(in);
        }

        @Override
        public DocumentPage[] newArray(int size) {
            return new DocumentPage[size];
        }
    };

    public String getUrl() {

        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    @Override
    public String toString() {
        return "DocumentPage{" +
                "url='" + url + '\'' +
                ", pageNumber=" + pageNumber +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeInt(pageNumber);
    }
}
