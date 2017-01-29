package bd.com.ipay.ipayskeleton.FingerPrintAuthentication;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

import bd.com.ipay.ipayskeleton.CustomView.Dialogs.InviteDialog;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class FingerprintAuthenticationDialog extends MaterialDialog.Builder {

    public FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;

    private KeyPairGenerator mKeyPairGenerator;
    private static final String KEY_NAME = "example_key";

    public Cipher mEncryptCipher;
    public Cipher mDecryptCipher;
    private FingerprintManager.CryptoObject cryptoObject;

    private Stage mStage = Stage.FINGERPRINT_ENCRYPT;

    private SharedPreferences mPref;
    private Context mContext;

    private FinishCheckerListener mFinishCheckerListener;
    private FinishDecryptionCheckerListener mFinishDecryptionCheckerListener;

    MaterialDialog.Builder mEncryptionDialog;
    MaterialDialog.Builder mDecryptionDialog;


    public FingerprintAuthenticationDialog(@NonNull Context context, Stage cipherStage) {
        super(context);
        this.mContext = context;

        this.mStage = cipherStage;
        initDialog();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void initDialog() {
        keyguardManager =
                (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        fingerprintManager =
                (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);

        mPref = context.getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
        FingerPrintAuthModule fingerprintAuthModule = new FingerPrintAuthModule(context);

        if (fingerprintAuthModule.checkIfFingerPrintSupported()) {
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
*/
           // generateKey();

            keyStore = providesKeystore();
            keyGenerator = providesKeyGenerator();

            if (mStage == Stage.FINGERPRINT_ENCRYPT) {
                mEncryptionDialog = new MaterialDialog.Builder(context);
                mEncryptionDialog
                        .cancelable(false)
                        .customView(R.layout.fragment_fingerprint, true)
                        .negativeText(R.string.cancel)
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                mFinishCheckerListener.ifFinished();
                            }
                        });

                View dialogView = mEncryptionDialog.build().getCustomView();

                if (initEncryptCipher()) {
                    cryptoObject =
                            new FingerprintManager.CryptoObject(mEncryptCipher);
                    FingerPrintHandler helper = new FingerPrintHandler(context);
                    helper.setOnAuthenticationCallBackListener(new FingerPrintHandler.OnAuthenticationCallBackListener() {
                        @Override
                        public void onAuthenticationCallBack(FingerprintManager.AuthenticationResult result) {
                            tryEncrypt("qqqqqqq1");
                        }
                    });
                    helper.startAuth(fingerprintManager, cryptoObject);
                    mEncryptionDialog.show();
                }
            } else {
                mDecryptionDialog = new MaterialDialog.Builder(context);
                mDecryptionDialog
                        .cancelable(false)
                        .customView(R.layout.fragment_fingerprint, true)
                        .negativeText(R.string.cancel)
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                mFinishDecryptionCheckerListener.ifDecryptionFinished(null);
                            }
                        });

                View dialogView = mDecryptionDialog.build().getCustomView();

                    if (initDecryptCipher()) {
                        cryptoObject =
                                new FingerprintManager.CryptoObject(mDecryptCipher);
                        FingerPrintHandler helper = new FingerPrintHandler(context);
                        helper.setOnAuthenticationCallBackListener(new FingerPrintHandler.OnAuthenticationCallBackListener() {
                            @Override
                            public void onAuthenticationCallBack(FingerprintManager.AuthenticationResult result) {
                                tryDecrypt();
                            }
                        });
                        helper.startAuth(fingerprintManager, cryptoObject);
                        mDecryptionDialog.show();
                    }
            }
        }

       /* if(cipherInit()) {
            mFragment = new FingerPrintAuthenticationDialogFragment();
            mFragment.setStage(FingerPrintAuthenticationDialogFragment.Stage.FINGERPRINT_ENCRYPT);
            Bundle bundle = new Bundle();
            bundle.putString(Constants.PASSWORD, "qqqqqqq1");
            mFragment.setArguments(bundle);
        }*/

    }

    /*@TargetApi(Build.VERSION_CODES.M)
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
    public boolean initEncryptCipher() {
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

    @TargetApi(Build.VERSION_CODES.M)
    public boolean initDecryptCipher() {
        try {
            mDecryptCipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            byte[] iv;
            IvParameterSpec ivParams;
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
            iv = Base64.decode(mPref.getString(Constants.KEY_PASSWORD_IV, ""), Base64.DEFAULT);
            ivParams = new IvParameterSpec(iv);
            mDecryptCipher.init(Cipher.DECRYPT_MODE, key, ivParams);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return false;
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
            mFinishCheckerListener.ifFinished();
            return true;


        } catch (BadPaddingException | IllegalBlockSizeException e) {
            Toast.makeText(context, "Failed to encrypt the data with the generated key. "
                    + "Retry the purchase", Toast.LENGTH_LONG).show();
        } catch (InvalidParameterSpecException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String tryDecrypt() {
        try {
            String encodedData = mPref.getString(Constants.KEY_PASSWORD, "");
            byte[] decodedData = Base64.decode(encodedData, Base64.DEFAULT);
            mFinishDecryptionCheckerListener.ifDecryptionFinished(new String(mDecryptCipher.doFinal(decodedData)));
            return new String(decodedData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }*/

    public void setFinishCheckerListener(FinishCheckerListener finishCheckerListener) {
        mFinishCheckerListener = finishCheckerListener;
    }

    public interface FinishCheckerListener {
        void ifFinished();
    }

    public void setFinishDecryptionCheckerListener(FinishDecryptionCheckerListener finishDecryptionCheckerListener) {
        mFinishDecryptionCheckerListener = finishDecryptionCheckerListener;
    }

    public interface FinishDecryptionCheckerListener {
        void ifDecryptionFinished(String decryptedData);
    }

    public enum Stage {
        FINGERPRINT_ENCRYPT,
        FINGERPRINT_DECRYPT,
    }



 /*****/

 public KeyStore providesKeystore() {
     try {
         return KeyStore.getInstance("AndroidKeyStore");
     } catch (KeyStoreException e) {
         throw new RuntimeException("Failed to get an instance of KeyStore", e);
     }
 }

    public KeyGenerator providesKeyGenerator() {
        try {
            return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get an instance of KeyGenerator", e);
        }
    }

    private boolean initEncryptCipher() {

        mEncryptCipher = getCipher(Cipher.ENCRYPT_MODE);
        if (mEncryptCipher == null) {
            // try again after recreating the keystore
            createKey();
            mEncryptCipher = getCipher(Cipher.ENCRYPT_MODE);
        }
        return (mEncryptCipher != null);

    }

    public boolean initDecryptCipher() {
        mDecryptCipher = getCipher(Cipher.DECRYPT_MODE);
        return (mDecryptCipher != null);
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
            mFinishCheckerListener.ifFinished();
            return true;


        } catch (BadPaddingException | IllegalBlockSizeException e) {
            Toast.makeText(context, "Failed to encrypt the data with the generated key. "
                    + "Retry the purchase", Toast.LENGTH_LONG).show();
        } catch (InvalidParameterSpecException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String tryDecrypt() {
        try {

            byte[] encodedData = Base64.decode(mPref.getString(Constants.KEY_PASSWORD, ""), Base64.DEFAULT);
            byte[] decodedData = mDecryptCipher.doFinal(encodedData);
            mFinishDecryptionCheckerListener.ifDecryptionFinished(new String(decodedData));
            return new String(decodedData);

        } catch (BadPaddingException | IllegalBlockSizeException e) {
            Toast.makeText(context, "Failed to decrypt the data with the generated key. "
                    + "Retry the purchase", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return null;
    }


    private SecretKey getKey() {
        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
            if (key != null) return key;
            return createKey();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return null;
    }

    private SecretKey createKey() {
        try {

            // Set the alias of the entry in Android KeyStore where the key will appear
            // and the constrains (purposes) in the constructor of the Builder
            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_DECRYPT | KeyProperties.PURPOSE_ENCRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    // Require the user to authenticate with a fingerprint to authorize every use
                    // of the key
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            return keyGenerator.generateKey();

        } catch (Exception e) {

        }
        return null;
    }


    public Cipher getCipher(int mode) {
        Cipher cipher;

        try {
            keyStore.load(null);
            byte[] iv;
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            IvParameterSpec ivParams;
            if (mode == Cipher.ENCRYPT_MODE) {
                cipher.init(mode, getKey());

            } else {
                SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
                iv = Base64.decode(mPref.getString(Constants.KEY_PASSWORD_IV, ""), Base64.DEFAULT);
                ivParams = new IvParameterSpec(iv);
                cipher.init(mode, key, ivParams);
            }
            return cipher;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

   /*****/

}


