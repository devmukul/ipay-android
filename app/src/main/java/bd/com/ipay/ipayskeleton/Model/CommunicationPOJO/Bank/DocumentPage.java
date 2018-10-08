
package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Bank;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DocumentPage implements Parcelable {

	@SerializedName("url")
	@Expose
	private String url;
	@SerializedName("pageNumber")
	@Expose
	private Long pageNumber;

	public DocumentPage() {
	}

	public DocumentPage(String url, Long pageNumber) {
		super();
		this.url = url;
		this.pageNumber = pageNumber;
	}

	protected DocumentPage(Parcel in) {
		url = in.readString();
		if (in.readByte() == 0) {
			pageNumber = null;
		} else {
			pageNumber = in.readLong();
		}
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

	public Long getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(Long pageNumber) {
		this.pageNumber = pageNumber;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(url);
		if (pageNumber == null) {
			dest.writeByte((byte) 0);
		} else {
			dest.writeByte((byte) 1);
			dest.writeLong(pageNumber);
		}
	}
}
