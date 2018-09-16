package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Notification;

import android.os.Parcel;
import android.os.Parcelable;

public class UserProfile implements Parcelable {

    private String userName;
    private String userMobileNumber;
    private String userProfilePicture;

    public UserProfile() {
    }

    protected UserProfile(Parcel in) {
        userName = in.readString();
        userMobileNumber = in.readString();
        userProfilePicture = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        dest.writeString(userMobileNumber);
        dest.writeString(userProfilePicture);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserProfile> CREATOR = new Creator<UserProfile>() {
        @Override
        public UserProfile createFromParcel(Parcel in) {
            return new UserProfile(in);
        }

        @Override
        public UserProfile[] newArray(int size) {
            return new UserProfile[size];
        }
    };

    public String getUserName() {
        return userName;
    }

    public String getUserMobileNumber() {
        return userMobileNumber;
    }

    public String getUserProfilePicture() {
        return userProfilePicture;
    }
}
