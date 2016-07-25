package bd.com.ipay.ipayskeleton.Model.MMModule.Profile.IntroductionAndInvite;

import bd.com.ipay.ipayskeleton.Model.MMModule.Notification.Notification;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class IntroductionRequestClass implements Notification {
    private long id;
    private String senderMobileNumber;
    private String senderName;
    private long senderAccountId;
    private long verifierAccountId;
    private long date;
    private String status;
    private String profilePictureUrl;

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public long getId() {
        return id;
    }

    public String getSenderMobileNumber() {
        return senderMobileNumber;
    }

    public String getSenderName() {
        return senderName;
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
    public String getTitle() {
        return "Introduction Request from " + getSenderName();
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

    @Override
    public int getNotificationType() {
        return Constants.NOTIFICATION_TYPE_INTROUDCTION_REQUEST;
    }
}
