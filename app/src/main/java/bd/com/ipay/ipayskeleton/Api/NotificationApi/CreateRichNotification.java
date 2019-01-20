package bd.com.ipay.ipayskeleton.Api.NotificationApi;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.TransactionDetailsActivity;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory.TransactionHistory;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class CreateRichNotification {
    private static final String CHANNEL_ID_DEFAULT = "ipay_notification_channel";
    private TransactionHistory transactionHistory;
    private Context context;

    public CreateRichNotification(TransactionHistory transactionHistory, Context context) {
        this.transactionHistory = transactionHistory;
        this.context = context;
    }

    private void setBitmap(String url, final RemoteViews remoteView, final int viewId) {
        final int size = context.getResources().getDimensionPixelSize(R.dimen.value48);
        final Bitmap profilePictureBitmap;
        Bitmap newBitmap;
        RoundedBitmapDrawable roundedBitmapDrawable;
        try {
            profilePictureBitmap = Glide.with(context)
                    .load(Constants.BASE_URL_FTP_SERVER + url)
                    .asBitmap()
                    .into(size, size).get();
            newBitmap = getCircleBitmap(profilePictureBitmap);

        } catch (Exception e) {
            return;
        }
        remoteView.setImageViewBitmap(viewId,newBitmap );
    }

    private Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }

    public void setupNotification() {

        PendingIntent pendingIntent = getNotificationPendingIntent();
        RemoteViews transactionNotificationView = new RemoteViews
                (context.getPackageName(), R.layout.list_item_transaction_history_notification);
        transactionNotificationView.setViewVisibility(R.id.button_layout, View.GONE);
        setBitmap(transactionHistory.getOtherParty().getUserProfilePic(),
                transactionNotificationView, R.id.profile_picture);
        fillUpViewsWithNecessaryData(transactionNotificationView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = context.getPackageName();
            CharSequence name = "ipay";
            String description = context.getPackageCodePath();
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setCustomContentView(transactionNotificationView);

            notificationManager.notify(transactionHistory.getTransactionID().hashCode(), mBuilder.build());
        } else {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID_DEFAULT)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setCustomBigContentView(transactionNotificationView);
            notificationManager.notify(transactionHistory.getTransactionID().hashCode(), mBuilder.build());
        }

    }

    public void fillUpViewsWithNecessaryData(RemoteViews transactionNotificationView) {
        final String description = transactionHistory.getShortDescription();
        final String receiver = transactionHistory.getReceiver();
        String responseTime = Utilities.formatDayMonthYear(transactionHistory.getTime());
        final Integer statusCode = transactionHistory.getStatusCode();
        final Double balance = transactionHistory.getAccountBalance();
        final String outletName = transactionHistory.getOutletName();

        if (balance != null) {
            transactionNotificationView.setTextViewText(R.id.amount, Utilities.formatTakaWithComma(balance));
        }

        transactionNotificationView.setTextViewText(R.id.net_amount, String.valueOf(
                Utilities.formatTakaFromString(transactionHistory.getNetAmountFormatted())));

        switch (statusCode) {
            case Constants.TRANSACTION_STATUS_ACCEPTED: {
                transactionNotificationView.setImageViewResource
                        (R.id.status_description_icon, R.drawable.transaction_tick_sign);
                break;
            }
            case Constants.TRANSACTION_STATUS_CANCELLED: {
                transactionNotificationView.setImageViewResource
                        (R.id.status_description_icon, R.drawable.transaction_cross_sign);
                break;
            }
            case Constants.TRANSACTION_STATUS_REJECTED: {

                transactionNotificationView.setImageViewResource
                        (R.id.status_description_icon, R.drawable.transaction_cross_sign);
                break;
            }
            case Constants.TRANSACTION_STATUS_FAILED: {
                transactionNotificationView.setImageViewResource
                        (R.id.status_description_icon, R.drawable.transaction_cross_sign);
                break;
            }
        }

        transactionNotificationView.setTextViewText(R.id.activity_description, description);

        if (receiver != null && !receiver.equals("")) {
            transactionNotificationView.setViewVisibility(R.id.receiver, View.VISIBLE);
            if (!TextUtils.isEmpty(outletName)) {
                transactionNotificationView.setTextViewText(R.id.receiver, receiver + " (" + outletName + ")");
            } else {
                transactionNotificationView.setTextViewText(R.id.receiver, receiver);
            }
        } else transactionNotificationView.setViewVisibility(R.id.receiver, View.GONE);

        if (DateUtils.isToday(transactionHistory.getTime())) {
            responseTime = "Today, " + Utilities.formatTimeOnly(transactionHistory.getTime());
        }
        transactionNotificationView.setTextViewText(R.id.time, responseTime);

        if (transactionHistory.getAdditionalInfo().getType().equalsIgnoreCase(Constants.TRANSACTION_TYPE_USER)) {
            String imageUrl = transactionHistory.getAdditionalInfo().getUserProfilePic();

            transactionNotificationView.setViewVisibility(R.id.other_image, View.INVISIBLE);
            transactionNotificationView.setViewVisibility(R.id.profile_picture, View.VISIBLE);

            setBitmap(Constants.BASE_URL_FTP_SERVER + imageUrl,
                    transactionNotificationView, R.id.profile_picture);
        } else {
            int iconId = transactionHistory.getAdditionalInfo().getImageWithType(context);
            transactionNotificationView.setViewVisibility(R.id.other_image, View.VISIBLE);
            transactionNotificationView.setViewVisibility(R.id.profile_picture, View.INVISIBLE);
            transactionNotificationView.setImageViewResource(R.id.other_image, iconId);
        }
    }

    private PendingIntent getNotificationPendingIntent() {
        Intent intent = new Intent(context, TransactionDetailsActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        String transactionDetailsString = new Gson().toJson(transactionHistory);
        intent.setAction(Intent.ACTION_VIEW);
        intent.putExtra(Constants.TRANSACTION_DETAILS, transactionHistory);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);
        return pendingIntent;
    }
}
