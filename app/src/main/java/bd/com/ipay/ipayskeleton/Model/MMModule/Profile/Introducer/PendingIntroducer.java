package bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Introducer;

import bd.com.ipay.ipayskeleton.Model.MMModule.Notification.Notification;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class PendingIntroducer implements Notification {
    private int id;
    private String name;
    private String mobileNumber;
    private String profilePictureUrl;
    private long requestTime;

    public PendingIntroducer() {

    }

    public int getId() {
        return id;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    @Override
    public String getNotificationTitle() {
        String customDescription = "";

        customDescription = "Request to introduce you ";

        return customDescription;
    }

    @Override
    public String getDescription() {
        String customDescription = "";

        customDescription = "Request to introduce you ";

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
