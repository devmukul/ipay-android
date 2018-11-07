package bd.com.ipay.ipayskeleton.SourceOfFund.models;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MetaData implements Parcelable {
    @SerializedName("Sponsor name")
    private String sponsorName;

    @SerializedName("Sponsor mobile number")
    private String sponsorMobileNumber;

    @SerializedName("Sponsor profile pictures")
    private ArrayList<ProfilePicture>sponsorProfilePictures;

    private boolean isSponsoredByOther;

    protected MetaData(Parcel in) {
        sponsorName = in.readString();
        sponsorMobileNumber = in.readString();
        sponsorProfilePictures = in.createTypedArrayList(ProfilePicture.CREATOR);
        isSponsoredByOther = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sponsorName);
        dest.writeString(sponsorMobileNumber);
        dest.writeTypedList(sponsorProfilePictures);
        dest.writeByte((byte) (isSponsoredByOther ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MetaData> CREATOR = new Creator<MetaData>() {
        @Override
        public MetaData createFromParcel(Parcel in) {
            return new MetaData(in);
        }

        @Override
        public MetaData[] newArray(int size) {
            return new MetaData[size];
        }
    };

    public String getSponsorName() {
        return sponsorName;
    }

    public void setSponsorName(String sponsorName) {
        this.sponsorName = sponsorName;
    }

    public String getSponsorMobileNumber() {
        return sponsorMobileNumber;
    }

    public void setSponsorMobileNumber(String sponsorMobileNumber) {
        this.sponsorMobileNumber = sponsorMobileNumber;
    }

    public ArrayList<ProfilePicture> getSponsorProfilePictures() {
        return sponsorProfilePictures;
    }

    public void setSponsorProfilePictures(ArrayList<ProfilePicture> sponsorProfilePictures) {
        this.sponsorProfilePictures = sponsorProfilePictures;
    }

    public boolean isSponsoredByOther() {
        return isSponsoredByOther;
    }

    public void setSponsoredByOther(boolean sponsoredByOther) {
        isSponsoredByOther = sponsoredByOther;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MetaData metaData = (MetaData) o;

        if (isSponsoredByOther != metaData.isSponsoredByOther) return false;
        if (sponsorName != null ? !sponsorName.equals(metaData.sponsorName) : metaData.sponsorName != null)
            return false;
        if (sponsorMobileNumber != null ? !sponsorMobileNumber.equals(metaData.sponsorMobileNumber) : metaData.sponsorMobileNumber != null)
            return false;
        return sponsorProfilePictures != null ? sponsorProfilePictures.equals(metaData.sponsorProfilePictures) : metaData.sponsorProfilePictures == null;
    }

    @Override
    public int hashCode() {
        int result = sponsorName != null ? sponsorName.hashCode() : 0;
        result = 31 * result + (sponsorMobileNumber != null ? sponsorMobileNumber.hashCode() : 0);
        result = 31 * result + (sponsorProfilePictures != null ? sponsorProfilePictures.hashCode() : 0);
        result = 31 * result + (isSponsoredByOther ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MetaData{" +
                "sponsorName='" + sponsorName + '\'' +
                ", sponsorMobileNumber='" + sponsorMobileNumber + '\'' +
                ", sponsorProfilePictures=" + sponsorProfilePictures +
                ", isSponsoredByOther=" + isSponsoredByOther +
                '}';
    }
}
