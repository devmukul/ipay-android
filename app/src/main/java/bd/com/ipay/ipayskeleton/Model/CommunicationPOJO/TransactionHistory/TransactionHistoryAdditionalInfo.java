package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.UserProfilePictureClass;

public class TransactionHistoryAdditionalInfo implements Parcelable {
    private String name;
    private String number;
    private String type;
    private List<UserProfilePictureClass> profilePictures;

    protected TransactionHistoryAdditionalInfo(Parcel in) {
        name = in.readString();
        number = in.readString();
        type = in.readString();
        profilePictures = in.createTypedArrayList(UserProfilePictureClass.CREATOR);
    }

    public String getName() {
        return name;
    }

    public String getMobileNumber() {
        return number;
    }

    public String getUserProfilePic() {
        return profilePictures.get(0).getUrl();
    }

    public List<UserProfilePictureClass> getProfilePictures() {
        return profilePictures;
    }

    public String getType() {
        return type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TransactionHistoryAdditionalInfo> CREATOR = new Creator<TransactionHistoryAdditionalInfo>() {
        @Override
        public TransactionHistoryAdditionalInfo createFromParcel(Parcel in) {
            return new TransactionHistoryAdditionalInfo(in);
        }

        @Override
        public TransactionHistoryAdditionalInfo[] newArray(int size) {
            return new TransactionHistoryAdditionalInfo[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(number);
        dest.writeString(type);
        dest.writeTypedList(this.profilePictures);
    }
}
