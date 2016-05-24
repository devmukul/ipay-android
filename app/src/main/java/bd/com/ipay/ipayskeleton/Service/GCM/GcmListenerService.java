package bd.com.ipay.ipayskeleton.Service.GCM;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import bd.com.ipay.ipayskeleton.Activities.SignupOrLoginActivity;
import bd.com.ipay.ipayskeleton.Api.DownloadImageFromUrlAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo.GetUserInfoRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo.GetUserInfoResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo.UserProfilePictureClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Documents.GetIdentificationDocumentResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Email.GetEmailResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.PushNotificationStatusHolder;

public class GcmListenerService extends com.google.android.gms.gcm.GcmListenerService implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetProfileInfoTask = null;
    private GetUserInfoResponse mGetUserInfoResponse;

    private HttpRequestGetAsyncTask mGetIdentificationDocumentsTask = null;
    private GetIdentificationDocumentResponse mIdentificationDocumentResponse = null;

    private HttpRequestGetAsyncTask mGetEmailsTask = null;
    private GetEmailResponse mGetEmailResponse;

    private String tag;
    private PushNotificationStatusHolder mPushNotificationStatusHolder;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.i("Push Found", "From: " + from + ", data: " + data);

        SharedPreferences pref = getSharedPreferences(Constants.ApplicationTag, MODE_PRIVATE);
        boolean isLoggedIn = pref.getBoolean(Constants.LOGGEDIN, false);

        tag = data.getString(Constants.PUSH_NOTIFICATION_EVENT);

        mPushNotificationStatusHolder = new PushNotificationStatusHolder(this);

        if (isForeground() && isLoggedIn) {
            switch (tag) {
                case Constants.PUSH_NOTIFICATION_TAG_PROFILE_PICTURE:
                    getProfileInfo();
                    break;
                case Constants.PUSH_NOTIFICATION_TAG_IDENTIFICATION_DOCUMENT_UPDATE:
                    getIdentificationDocuments();
                    break;
                case Constants.PUSH_NOTIFICATION_TAG_EMAIL_UPDATE:
                    getEmails();
                    break;

            }
        } else {
            mPushNotificationStatusHolder.setUpdateNeeded(tag, true);
        }


    }

    private void createNotification(String title, String message) {
        int notificationID = new Random().nextInt();
        Intent intent = new Intent(this, SignupOrLoginActivity.class);


        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationID, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.icon_ipay)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationID, notificationBuilder.build());
    }

    private boolean isForeground() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        return componentInfo.getPackageName().equals(Constants.ApplicationPackage);
    }

    private void getProfileInfo() {
        if (mGetProfileInfoTask != null) {
            return;
        }

        SharedPreferences pref = getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
        GetUserInfoRequestBuilder mGetUserInfoRequestBuilder = new GetUserInfoRequestBuilder(pref.getString(Constants.USERID, ""));

        String mUri = mGetUserInfoRequestBuilder.getGeneratedUri();
        mGetProfileInfoTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_USER_INFO,
                mUri, this);
        mGetProfileInfoTask.mHttpResponseListener = this;
        mGetProfileInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getIdentificationDocuments() {
        if (mGetIdentificationDocumentsTask != null) {
            return;
        }

        mGetIdentificationDocumentsTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_IDENTIFICATION_DOCUMENTS_REQUEST,
                Constants.BASE_URL_MM + Constants.URL_GET_DOCUMENTS, this, this);
        mGetIdentificationDocumentsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getEmails() {
        if (mGetEmailsTask != null) {
            return;
        }

        mGetEmailsTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_EMAILS,
                Constants.BASE_URL_MM + Constants.URL_GET_EMAIL, this, this);
        mGetEmailsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        if (result == null) {
            mGetProfileInfoTask = null;
            return;
        }

        DataHelper dataHelper = DataHelper.getInstance(this);
        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_GET_USER_INFO)) {

            try {
                mGetUserInfoResponse = gson.fromJson(result.getJsonString(), GetUserInfoResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                    List<UserProfilePictureClass> profilePictures = mGetUserInfoResponse.getProfilePictures();

                    String imageUrl = "";
                    if (profilePictures.size() > 0) {
                        for (Iterator<UserProfilePictureClass> it = profilePictures.iterator(); it.hasNext(); ) {
                            UserProfilePictureClass userProfilePictureClass = it.next();
                            imageUrl = userProfilePictureClass.getUrl();
                            break;
                        }
                    }

                    SharedPreferences pref = getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
                    pref.edit().putString(Constants.VERIFICATION_STATUS, mGetUserInfoResponse.getAccountStatus()).apply();
                    String mUserID = pref.getString(Constants.USERID, "");

                    // Download the profile picture and store it in local storage
                    new DownloadImageFromUrlAsyncTask(imageUrl, mUserID)
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    mPushNotificationStatusHolder.setUpdateNeeded(Constants.PUSH_NOTIFICATION_TAG_PROFILE_PICTURE, false);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            mGetProfileInfoTask = null;

        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_IDENTIFICATION_DOCUMENTS_REQUEST)) {

            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                dataHelper.updatePushEvents(Constants.COMMAND_GET_IDENTIFICATION_DOCUMENTS_REQUEST, result.getJsonString());
                mPushNotificationStatusHolder.setUpdateNeeded(Constants.PUSH_NOTIFICATION_TAG_IDENTIFICATION_DOCUMENT_UPDATE, false);
            }

        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_EMAILS)) {

            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                dataHelper.updatePushEvents(Constants.PUSH_NOTIFICATION_TAG_EMAIL_UPDATE, result.getJsonString());
                mPushNotificationStatusHolder.setUpdateNeeded(Constants.PUSH_NOTIFICATION_TAG_EMAIL_UPDATE, false);
            }
        }

        dataHelper.closeDbOpenHelper();
    }
}