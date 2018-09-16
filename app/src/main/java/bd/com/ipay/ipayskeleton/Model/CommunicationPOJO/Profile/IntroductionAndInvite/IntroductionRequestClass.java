package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.IntroductionAndInvite;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Notification.Notification;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.Address.AddressClass;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class IntroductionRequestClass implements Notification, Parcelable {
    private long id;
    private String senderMobileNumber;
    private String senderName;
    private long senderAccountId;
    private long verifierAccountId;
    private long date;
    private String father;
    private String mother;
    private String status;
    private String profilePictureUrl;
    private List<AddressContainer> addresses;

    protected IntroductionRequestClass(Parcel in) {
        id = in.readLong();
        senderMobileNumber = in.readString();
        senderName = in.readString();
        senderAccountId = in.readLong();
        verifierAccountId = in.readLong();
        date = in.readLong();
        father = in.readString();
        mother = in.readString();
        status = in.readString();
        profilePictureUrl = in.readString();
        addresses = in.createTypedArrayList(AddressContainer.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(senderMobileNumber);
        dest.writeString(senderName);
        dest.writeLong(senderAccountId);
        dest.writeLong(verifierAccountId);
        dest.writeLong(date);
        dest.writeString(father);
        dest.writeString(mother);
        dest.writeString(status);
        dest.writeString(profilePictureUrl);
        dest.writeTypedList(addresses);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<IntroductionRequestClass> CREATOR = new Creator<IntroductionRequestClass>() {
        @Override
        public IntroductionRequestClass createFromParcel(Parcel in) {
            return new IntroductionRequestClass(in);
        }

        @Override
        public IntroductionRequestClass[] newArray(int size) {
            return new IntroductionRequestClass[size];
        }
    };

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public long getId() {
        return id;
    }


    public String getName() {
        return senderName;
    }

    public String getSenderMobileNumber() {
        return senderMobileNumber;
    }


    public long getSenderAccountId() {
        return senderAccountId;
    }

    public long getVerifierAccountId() {
        return verifierAccountId;
    }

    public long getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String getNotificationTitle() {
        return "Introduction Request ";
    }

    @Override
    public String getImageUrl() {
        return getProfilePictureUrl();
    }

    @Override
    public long getTime() {
        return getDate();
    }

    public String getFather() {
        return father;
    }

    public String getMother() {
        return mother;
    }

    public List<AddressContainer> getAddressList() {
        return addresses;
    }

    @Override
    public int getNotificationType() {
        return Constants.NOTIFICATION_TYPE_INTRODUCTION_REQUEST;
    }

    public AddressClass getPresentAddress() {
        for (AddressContainer addressContainer : addresses) {
            if (addressContainer.getAddressType().equals(Constants.ADDRESS_TYPE_PRESENT))
                return addressContainer.getAddress();
        }
        return null;
    }

    public AddressClass getPermanentAddress() {
        for (AddressContainer addressContainer : addresses) {
            if (addressContainer.getAddressType().equals(Constants.ADDRESS_TYPE_PERMANENT))
                return addressContainer.getAddress();
        }
        return null;
    }

    public AddressClass getOfficeAddress() {
        for (AddressContainer addressContainer : addresses) {
            if (addressContainer.getAddressType().equals(Constants.ADDRESS_TYPE_OFFICE))
                return addressContainer.getAddress();
        }
        return null;
    }
}
