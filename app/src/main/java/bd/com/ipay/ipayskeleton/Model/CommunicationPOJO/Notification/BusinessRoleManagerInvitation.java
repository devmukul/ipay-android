package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Notification;


import android.os.Parcel;

import java.util.List;

import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.UserProfilePictureClass;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class BusinessRoleManagerInvitation implements Notification {
    private long id;
    private long businessAccountId;
    private String businessName;
    private String roleName;
    private long createdAt;
    private List<UserProfilePictureClass> profilePictures;

    protected BusinessRoleManagerInvitation(Parcel in) {
        id = in.readLong();
        businessAccountId = in.readLong();
        businessName = in.readString();
        roleName = in.readString();
        createdAt = in.readLong();
        profilePictures = in.createTypedArrayList(UserProfilePictureClass.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(businessAccountId);
        dest.writeString(businessName);
        dest.writeString(roleName);
        dest.writeLong(createdAt);
        dest.writeTypedList(profilePictures);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BusinessRoleManagerInvitation> CREATOR = new Creator<BusinessRoleManagerInvitation>() {
        @Override
        public BusinessRoleManagerInvitation createFromParcel(Parcel in) {
            return new BusinessRoleManagerInvitation(in);
        }

        @Override
        public BusinessRoleManagerInvitation[] newArray(int size) {
            return new BusinessRoleManagerInvitation[size];
        }
    };

    @Override
    public String getNotificationTitle() {
        return "<b>" + "<font color='#000000'>" + getBusinessName() + "</b>" + "</font>" + " made you <b><font color='#000000'>" +
                "Admin</b></font> of their business account";
    }

    @Override
    public String getName() {
        return roleName;
    }

    @Override
    public String getImageUrl() {
        return profilePictures.get(0).getUrl();
    }

    public long getId() {
        return id;
    }

    public long getBusinessAccountId() {
        return businessAccountId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public String getRoleName() {
        return roleName;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public List<UserProfilePictureClass> getProflePictures() {
        return profilePictures;
    }

    @Override
    public long getTime() {
        return createdAt;
    }

    @Override
    public int getNotificationType() {
        return Constants.NOTIFICATION_TYPE_PENDING_ROLE_MANAGER_REQUEST;
    }
}

