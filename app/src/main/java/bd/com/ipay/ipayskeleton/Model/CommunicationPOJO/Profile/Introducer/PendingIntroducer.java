package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.Introducer;

import android.os.Parcel;

import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Notification.Notification;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class PendingIntroducer implements Notification {
    private int id;
    private String name;
    private String mobileNumber;
    private String profilePictureUrl;
    private long requestTime;

    public PendingIntroducer() {

    }

    protected PendingIntroducer(Parcel in) {
        id = in.readInt();
        name = in.readString();
        mobileNumber = in.readString();
        profilePictureUrl = in.readString();
        requestTime = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(mobileNumber);
        dest.writeString(profilePictureUrl);
        dest.writeLong(requestTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PendingIntroducer> CREATOR = new Creator<PendingIntroducer>() {
        @Override
        public PendingIntroducer createFromParcel(Parcel in) {
            return new PendingIntroducer(in);
        }

        @Override
        public PendingIntroducer[] newArray(int size) {
            return new PendingIntroducer[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    @Override
    public String getNotificationTitle() {
        String customDescription = "Request to introduce you ";

        return customDescription;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getImageUrl() {
        return profilePictureUrl;
    }

    @Override
    public long getTime() {
        return requestTime;
    }

    @Override
    public int getNotificationType() {
        return Constants.NOTIFICATION_TYPE_PENDING_INTRODUCER_REQUEST;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public long getRequestTime() {
        return requestTime;
    }


}
