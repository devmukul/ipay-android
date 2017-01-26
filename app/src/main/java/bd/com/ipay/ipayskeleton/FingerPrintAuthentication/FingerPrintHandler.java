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
        this.update("Fingerprint Authentication help\n" + helpString, false);
    }


    @Override
    public void onAuthenticationFailed() {
    }


    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        onAuthenticationCallBackListener.onAuthenticationCallBack(result);
    }


    public void update(String e, Boolean success){
       /* TextView textView = (TextView) ((Activity)context).findViewById(R.id.);
        textView.setText(e);*//*
        if(success){
            textView.setTextColor(ContextCompat.getColor(context,R.color.colorPrimaryDark));
        }*/
    }

    public void setOnAuthenticationCallBackListener(OnAuthenticationCallBackListener onAuthenticationCallBackListener) {
        this.onAuthenticationCallBackListener = onAuthenticationCallBackListener;
    }

    public interface OnAuthenticationCallBackListener {
        void onAuthenticationCallBack(FingerprintManager.AuthenticationResult result);
    }

}
