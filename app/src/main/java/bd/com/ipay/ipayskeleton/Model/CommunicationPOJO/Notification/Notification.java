package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Notification;

import android.os.Parcelable;

public interface Notification extends Parcelable {
    String getNotificationTitle();

    String getName();

    String getImageUrl();

    long getTime();

    int getNotificationType();
}
