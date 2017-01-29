/*
package bd.com.ipay.ipayskeleton.FingerPrintAuthentication;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class FingerprintAuthFragment extends Fragment {

    public FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private static final String KEY_NAME = "example_key";
    public Cipher mEncryptCipher;
    private FingerprintManager.CryptoObject cryptoObject;

    private SharedPreferences mPref;

    private FingerPrintAuthenticationDialogFragment mFragment;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_fingerprint, container, false);

        keyguardManager =
                (KeyguardManager) getActivity().getSystemService(Context.KEYGUARD_SERVICE);
        fingerprintManager =
                (FingerprintManager) getActivity().getSystemService(Context.FINGERPRINT_SERVICE);

        mPref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
        FingerPrintAuthModule fingerprintAuthModule = new FingerPrintAuthModule(getActivity());

        if(fingerprintAuthModule.checkIfFingerPrintSupported()) {
       */
/* if (!keyguardManager.isKeyguardSecure()) {

            Toast.makeText(getActivity(),
                    "Lock screen security not enabled in Settings",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.USE_FINGERPRINT) !=
                PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(),
                    "Fingerprint authentication permission not enabled",
                    Toast.LENGTH_LONG).show();
        }

        if (!fingerprintManager.hasEnrolledFingerprints()) {

            // This happens when no fingerprints are registered.
            Toast.makeText(getActivity(),
                    "Register at least one fingerprint in Settings",
                    Toast.LENGTH_LONG).show();
        }

        if (!fingerprintManager.hasEnrolledFingerprints()) {

            // This happens when no fingerprints are registered.
            Toast.makeText(this,
                    "Register at least one fingerprint in Settings",
                    Toast.LENGTH_LONG).show();
            return;
        }
*//*

            generateKey();

            if (cipherInit()) {
                cryptoObject =
                        new FingerprintManager.CryptoObject(mEncryptCipher);
                FingerPrintHandler helper = new FingerPrintHandler(getActivity());
                helper.setOnAuthenticationCallBackListener(new FingerPrintHandler.OnAuthenticationCallBackListener() {
                    @Override
                    public void onAuthenticationCallBack(FingerprintManager.AuthenticationResult result) {
                        tryEncrypt("qqqqqqq1");
                    }
                });
                helper.startAuth(fingerprintManager, cryptoObject);
            }
        }

       */
/* if(cipherInit()) {
            mFragment = new FingerPrintAuthenticationDialogFragment();
            mFragment.setStage(FingerPrintAuthenticationDialogFragment.Stage.FINGERPRINT_ENCRYPT);
            Bundle bundle = new Bundle();
            bundle.putString(Constants.PASSWORD, "qqqqqqq1");
            mFragment.setArguments(bundle);
        }*//*


        return v;

    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,
                    "AndroidKeyStore");
        } catch (NoSuchAlgorithmException |
                NoSuchProviderException e) {
            throw new RuntimeException(
                    "Failed to get KeyGenerator instance", e);
        }

        try {
            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException |
                InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean cipherInit() {
        try {
            mEncryptCipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            mEncryptCipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    public boolean tryEncrypt(String secert) {
        try {

            byte[] encrypted = mEncryptCipher.doFinal(secert.getBytes());

            IvParameterSpec ivParams = mEncryptCipher.getParameters().getParameterSpec(IvParameterSpec.class);
            String iv = Base64.encodeToString(ivParams.getIV(), Base64.DEFAULT);

            SharedPreferences.Editor editor = mPref.edit();
            editor.putString(Constants.KEY_PASSWORD, Base64.encodeToString(encrypted, Base64.DEFAULT));
            editor.putString(Constants.KEY_PASSWORD_IV, iv);
            editor.commit();
            Toast.makeText(getActivity(),
                    "Successful",
                    Toast.LENGTH_LONG).show();
            return true;


        } catch (BadPaddingException | IllegalBlockSizeException e) {
            Toast.makeText(getActivity(), "Failed to encrypt the data with the generated key. "
                    + "Retry the purchase", Toast.LENGTH_LONG).show();
        } catch (InvalidParameterSpecException e) {
            e.printStackTrace();
        }
        return false;
    }
}
*/
