package bd.com.ipay.ipayskeleton.FingerPrintAuthentication;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyProperties;
import android.support.v4.app.ActivityCompat;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.KeyGenerator;
import javax.net.ssl.KeyManager;

public class FingerPrintAuthModule {

    private Context mContext;
    private KeyguardManager mKeyguardManager;
    private FingerprintManager mFingerprintManager;

    public FingerPrintAuthModule(Context context) {
        this.mContext = context;
        initFingerPrintManagager();
    }

    public void initFingerPrintManagager() {
        // Initializing both Android Keyguard Manager and Fingerprint Manager
        mKeyguardManager = (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);
        mFingerprintManager = (FingerprintManager) mContext.getSystemService(Context.FINGERPRINT_SERVICE);
    }

    public boolean checkIfFingerPrintSupported() {
        // Check whether the device has a Fingerprint sensor.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return false;
        if (!mFingerprintManager.isHardwareDetected()) {
            return false;
        } else {
            // Checks whether fingerprint permission is set on manifest
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                return false;
            } else {
                // Check whether at least one fingerprint is registered
                if (!mFingerprintManager.hasEnrolledFingerprints()) {
                    return false;
                } else {
                    // Checks whether lock screen security is enabled or not
                    if (!mKeyguardManager.isKeyguardSecure()) {
                        return false;
                    } else {
                        return true;
                    }
                }
            }
        }
    }

    public KeyguardManager getKeyguardManager() {
        return mKeyguardManager;
    }

    public FingerprintManager getFingerprintManager() {
        return mFingerprintManager;
    }

    public KeyStore getKeystore() {
        try {
            return KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            throw new RuntimeException("Failed to get an instance of KeyStore", e);
        }
    }

    public KeyGenerator getKeyGenerator() {
        try {
            return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get an instance of KeyGenerator", e);
        }
    }

}
