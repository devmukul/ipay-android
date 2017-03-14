package bd.com.ipay.ipayskeleton.Utilities.FingerPrintAuthenticationManager;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import bd.com.ipay.ipayskeleton.R;

@TargetApi(Build.VERSION_CODES.M)
public class FingerPrintHandler extends FingerprintManager.AuthenticationCallback {

    private Context mContext;
    private CancellationSignal mCancellationSignal;

    private OnAuthenticationCallBackListener onAuthenticationCallBackListener;

    // Constructor
    public FingerPrintHandler(Context mContext) {
        this.mContext = mContext;
    }

    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
        mCancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, mCancellationSignal, 0, this, null);
    }

    public void stopAuth() {
        if (mCancellationSignal != null)
            mCancellationSignal.cancel();
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
    }

    @Override
    public void onAuthenticationFailed() {
        Toast.makeText(mContext, R.string.fingerprint_not_recognized, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        Toast.makeText(mContext, R.string.fingerprint_recognized, Toast.LENGTH_SHORT).show();
        onAuthenticationCallBackListener.onAuthenticationCallBack(result);
    }

    public void setOnAuthenticationCallBackListener(OnAuthenticationCallBackListener onAuthenticationCallBackListener) {
        this.onAuthenticationCallBackListener = onAuthenticationCallBackListener;
    }

    public interface OnAuthenticationCallBackListener {
        void onAuthenticationCallBack(FingerprintManager.AuthenticationResult result);
    }
}
