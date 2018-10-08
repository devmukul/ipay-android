package bd.com.ipay.ipayskeleton.BroadcastReceivers;


import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

public class EnableDisableSMSBroadcastReceiver {

	private SMSReaderBroadcastReceiver mSMSReader;

	/**
	 * This method enables the Broadcast receiver registered in the AndroidManifest file.
	 */
	public void enableBroadcastReceiver(Context context, SMSReaderBroadcastReceiver.OnTextMessageReceivedListener listener) {

		IntentFilter intentFilter = new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED");

		mSMSReader = new SMSReaderBroadcastReceiver();
		mSMSReader.setOnTextMessageReceivedListener(listener);
		LocalBroadcastManager.getInstance(context).registerReceiver(mSMSReader, intentFilter);
	}

	/**
	 * This method disables the Broadcast receiver registered in the AndroidManifest file.
	 */
	public void disableBroadcastReceiver(Context context) {
		LocalBroadcastManager.getInstance(context).unregisterReceiver(mSMSReader);
	}
}