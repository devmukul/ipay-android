package bd.com.ipay.ipayskeleton.FingerPrintAuthentication;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;
import android.widget.Toast;

import bd.com.ipay.ipayskeleton.R;

public class FingerPrintHandler extends FingerprintManager.AuthenticationCallback {

    private Context context;
    private OnAuthenticationCallBackListener onAuthenticationCallBackListener;

    // Constructor
    public FingerPrintHandler(Context mContext) {
        context = mContext;
    }

    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
        CancellationSignal cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
    }

    @Override
    public void onAuthenticationFailed() {
        Toast.makeText(context, R.string.fingerprint_not_recognized, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        Toast.makeText(context, R.string.fingerprint_recognized, Toast.LENGTH_LONG).show();
        onAuthenticationCallBackListener.onAuthenticationCallBack(result);
    }

    public void setOnAuthenticationCallBackListener(OnAuthenticationCallBackListener onAuthenticationCallBackListener) {
        this.onAuthenticationCallBackListener = onAuthenticationCallBackListener;
    }

    public interface OnAuthenticationCallBackListener {
        void onAuthenticationCallBack(FingerprintManager.AuthenticationResult result);
    }
}
