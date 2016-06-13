package bd.com.ipay.ipayskeleton.BroadcastReceiverClass;


import android.content.Context;
import android.content.IntentFilter;

public class EnableDisableSMSBroadcastReceiver {

    /**
     * This method enables the Broadcast receiver registered in the AndroidManifest file.
     */

    private SMSReaderBraodcastReceiver mSMSReader;

    public void enableBroadcastReceiver(Context context, SMSReaderBraodcastReceiver.OnTextMessageReceivedListener listener) {

        IntentFilter intentFilter = new IntentFilter(
                "android.provider.Telephony.SMS_RECEIVED");
        mSMSReader = new SMSReaderBraodcastReceiver();
        mSMSReader.setOnTextMessageReceivedListener(listener);
        context.registerReceiver(mSMSReader, intentFilter);
    }

    /**
     * This method disables the Broadcast receiver registered in the AndroidManifest file.
     */
    public void disableBroadcastReceiver(Context context) {
        context.unregisterReceiver(mSMSReader);
    }
}