package bd.com.ipay.ipayskeleton.BroadcastReceiverClass;


import android.content.Context;
import android.content.IntentFilter;

public class EnableDisableReceiver {

    /**
     * This method enables the Broadcast receiver registered in the AndroidManifest file.
     */

    private TextMessageReader reader;

    public void enableBroadcastReceiver(Context context, TextMessageReader.OnTextMessageReceivedListener listener) {

        IntentFilter intentFilter = new IntentFilter(
                "android.provider.Telephony.SMS_RECEIVED");
        reader = new TextMessageReader();
        reader.setOnTextMessageReceivedListener(listener);
        context.registerReceiver(reader, intentFilter);
    }

    /**
     * This method disables the Broadcast receiver registered in the AndroidManifest file.
     */
    public void disableBroadcastReceiver(Context context) {
        context.unregisterReceiver(reader);
    }
}
