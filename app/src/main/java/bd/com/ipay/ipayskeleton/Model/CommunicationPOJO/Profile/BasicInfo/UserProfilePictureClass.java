package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo;

import android.os.Parcel;
import android.os.Parcelable;

public class UserProfilePictureClass implements Parcelable {

    private final String url;
    private final String quality;

    public UserProfilePictureClass(String url, String quality) {
        this.url = url;
        this.quality = quality;
    }

    public UserProfilePictureClass(Parcel in) {
        url = in.readString();
        quality = in.readString();
    }

    public String getUrl() {
        return url;
    }

    public String getQuality() {
        return quality;
    }

    @Override
    public String toString() {
        return "UserProfilePictureClass{" +
                "url='" + url + '\'' +
                ", quality='" + quality + '\'' +
                '}';
    }

    public static final Creator<UserProfilePictureClass> CREATOR = new Creator<UserProfilePictureClass>() {
        @Override
        public UserProfilePictureClass createFromParcel(Parcel in) {
            return new UserProfilePictureClass(in);
        }

        @Override
        public UserProfilePictureClass[] newArray(int size) {
            return new UserProfilePictureClass[size];
        }
    };



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(quality);
    }
}

