package bd.com.ipay.ipayskeleton.Api.NotificationApi;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;

import java.util.Random;

import bd.com.ipay.ipayskeleton.Activities.RichNotificationDetailsActivity;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class CreateOtherTypeRichNotification {
    private String body;
    private String title;
    private String imageUrl;
    private Context context;
    private String deepLink;
    private Bitmap bigPictureBitmap;
    private static final String CHANNEL_ID_DEFAULT = "ipay_notification_channel";

    public CreateOtherTypeRichNotification(Context context, String body, String title, String imageUrl, String deepLink) {
        this.context = context;
        this.body = body;
        this.title = title;
        this.imageUrl = imageUrl;
        this.deepLink = deepLink;
    }

    public void setUpRichNotification() {
        Intent intent = new Intent(context, RichNotificationDetailsActivity.class);
        intent.putExtra(Constants.IMAGE_URL , imageUrl);
        intent.putExtra(Constants.DEEP_LINK, deepLink);
        intent.putExtra(Constants.TITLE,title);
        intent.putExtra(Constants.BODY,body);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, intent, PendingIntent.FLAG_ONE_SHOT);
        initiateNotificationAction(pendingIntent);
    }

    private void initiateNotificationAction(final PendingIntent pendingIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final String CHANNEL_ID = context.getPackageName();
            CharSequence name = "ipay";
            String description = context.getPackageCodePath();
            int importance = NotificationManager.IMPORTANCE_HIGH;
            final NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            try {
                bigPictureBitmap = Glide.with(context)
                        .load(imageUrl)
                        .asBitmap()
                        .into(600, 600).get();
            } catch (Exception e) {

            }
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_ipay_verifiedmember)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setStyle(new NotificationCompat.BigPictureStyle()
                            .bigPicture(bigPictureBitmap));
            notificationManager.notify(new Random().nextInt(), mBuilder.build());


        } else {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID_DEFAULT)
                    .setSmallIcon(R.drawable.ic_ipay_verifiedmember)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setStyle(new NotificationCompat.BigPictureStyle()
                            .bigPicture(bigPictureBitmap));
            notificationManager.notify(new Random().nextInt(), mBuilder.build());
        }
    }
}