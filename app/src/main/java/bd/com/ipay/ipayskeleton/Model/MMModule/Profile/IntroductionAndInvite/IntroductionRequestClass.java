package bd.com.ipay.ipayskeleton.Model.MMModule.Profile.IntroductionAndInvite;

import java.util.List;

import bd.com.ipay.ipayskeleton.Model.MMModule.Notification.Notification;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Address.AddressClass;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class IntroductionRequestClass implements Notification {
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
    public String getDescription() {
        return getSenderMobileNumber();
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

    public static class AddressContainer {
        private String addressType;
        private AddressClass address;

        public String getAddressType() {
            return addressType;
        }

        public AddressClass getAddress() {
            return address;
        }

        @Override
        public String toString() {
            return "AddressContainer{" +
                    "addressType='" + addressType + '\'' +
                    ", address=" + address +
                    '}';
        }
    }

}
