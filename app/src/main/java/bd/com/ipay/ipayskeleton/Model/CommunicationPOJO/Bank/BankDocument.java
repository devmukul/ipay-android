
package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Bank;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BankDocument implements Parcelable {

	@SerializedName("documentType")
	@Expose
	private String documentType;
	@SerializedName("documentVerificationStatus")
	@Expose
	private String documentVerificationStatus;
	@SerializedName("documentPages")
	@Expose
	private List<DocumentPage> documentPages = null;

	public BankDocument() {
	}

	public BankDocument(String documentType, String documentVerificationStatus, List<DocumentPage> documentPages) {
		super();
		this.documentType = documentType;
		this.documentVerificationStatus = documentVerificationStatus;
		this.documentPages = documentPages;
	}

	protected BankDocument(Parcel in) {
		documentType = in.readString();
		documentVerificationStatus = in.readString();
		documentPages = in.createTypedArrayList(DocumentPage.CREATOR);
	}

	public static final Creator<BankDocument> CREATOR = new Creator<BankDocument>() {
		@Override
		public BankDocument createFromParcel(Parcel in) {
			return new BankDocument(in);
		}

		@Override
		public BankDocument[] newArray(int size) {
			return new BankDocument[size];
		}
	};

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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(documentType);
		dest.writeString(documentVerificationStatus);
		dest.writeTypedList(documentPages);
	}
}
