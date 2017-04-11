package bd.com.ipay.ipayskeleton.Utilities.FingerPrintAuthenticationManager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.util.Base64;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
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

import bd.com.ipay.ipayskeleton.Activities.SignupOrLoginActivity;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class FingerprintAuthenticationDialog extends MaterialDialog.Builder {

    public FingerprintManager mFingerprintManager;
    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;

    private FingerprintManager.CryptoObject mCryptoObject;

    public Cipher mEncryptCipher;
    public Cipher mDecryptCipher;

    private FinishEncryptionCheckerListener mFinishEncryptionCheckerListener;
    private FinishDecryptionCheckerListener mFinishDecryptionCheckerListener;

    private MaterialDialog.Builder mEncryptionDialogBuilder;
    private MaterialDialog.Builder mDecryptionDialogBuilder;
    private MaterialDialog mEncryptionDialog;
    private MaterialDialog mDecryptionDialog;

    private FingerPrintAuthenticationManager mFingerprintAuthenticationManager;
    private FingerPrintHandler mFingerPrintHandler;

    private Stage mStage = Stage.FINGERPRINT_ENCRYPT;
    private SharedPreferences mPref;
    boolean isFingerPrintAuthOn;

    public FingerprintAuthenticationDialog(@NonNull Context context, Stage cipherStage) {
        super(context);

        this.mStage = cipherStage;

        initDialog();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void initDialog() {
        initFingerPrintAuthenticationManager();

        mPref = context.getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
        isFingerPrintAuthOn = ProfileInfoCacheManager.getFingerprintAuthenticationStatus(false);

        if (mFingerprintAuthenticationManager.ifFingerprintAuthenticationSupported()) {
            if (mStage == Stage.FINGERPRINT_ENCRYPT) {
                setEncryptionStage();
            } else if (mStage == Stage.FINGERPRINT_DECRYPT) {
                setDecryptionStage();
            }
        }
    }

    private void initFingerPrintAuthenticationManager() {
        mFingerprintAuthenticationManager = new FingerPrintAuthenticationManager(context);

        if (mFingerprintAuthenticationManager.ifFingerprintAuthenticationSupported()) {
            mFingerprintManager = mFingerprintAuthenticationManager.getFingerprintManager();
            mKeyStore = mFingerprintAuthenticationManager.getKeystore();
            mKeyGenerator = mFingerprintAuthenticationManager.getKeyGenerator();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void setEncryptionStage() {
        mEncryptionDialogBuilder = new MaterialDialog.Builder(context);
        mEncryptionDialogBuilder
                .canceledOnTouchOutside(false)
                .customView(R.layout.dialog_fingerprint_encryption, true)
                .negativeText(R.string.cancel)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        cancelFingerprintAuthentication();
                    }
                });

        mEncryptionDialog = mEncryptionDialogBuilder.build();
        mEncryptionDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancelFingerprintAuthentication();
            }
        });

        if (initEncryptCipher()) {
            mCryptoObject = new FingerprintManager.CryptoObject(mEncryptCipher);
            mFingerPrintHandler = new FingerPrintHandler(context);
            mFingerPrintHandler.setOnAuthenticationCallBackListener(new FingerPrintHandler.OnAuthenticationCallBackListener() {
                @Override
                public void onAuthenticationCallBack(FingerprintManager.AuthenticationResult result) {

                    tryEncrypt(SignupOrLoginActivity.mPassword);
                }
            });
            mFingerPrintHandler.startAuth(mFingerprintManager, mCryptoObject);
            mEncryptionDialog.show();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void setDecryptionStage() {
        mDecryptionDialogBuilder = new MaterialDialog.Builder(context);
        mDecryptionDialogBuilder
                .canceledOnTouchOutside(false)
                .customView(R.layout.dialog_fingerprint_decryption, true)
                .negativeText(R.string.use_password)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        cancelFingerprintAuthentication();
                    }
                });

        mDecryptionDialog = mDecryptionDialogBuilder.build();
        mDecryptionDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancelFingerprintAuthentication();
            }
        });

        if (initDecryptCipher()) {
            mCryptoObject = new FingerprintManager.CryptoObject(mDecryptCipher);
            mFingerPrintHandler = new FingerPrintHandler(context);
            mFingerPrintHandler.setOnAuthenticationCallBackListener(new FingerPrintHandler.OnAuthenticationCallBackListener() {
                @Override
                public void onAuthenticationCallBack(FingerprintManager.AuthenticationResult result) {
                    tryDecrypt();
                }
            });
            mFingerPrintHandler.startAuth(mFingerprintManager, mCryptoObject);
            mDecryptionDialog.show();
        }
    }

    private boolean initEncryptCipher() {
        mEncryptCipher = getCipher(Cipher.ENCRYPT_MODE);
        if (mEncryptCipher == null) {
            // Try again after recreating the keystore
            createKey();
            mEncryptCipher = getCipher(Cipher.ENCRYPT_MODE);
        }
        return (mEncryptCipher != null);
    }

    public boolean initDecryptCipher() {
        mDecryptCipher = getCipher(Cipher.DECRYPT_MODE);
        return (mDecryptCipher != null);
    }

    private SecretKey getKey() {
        try {
            mKeyStore.load(null);
            SecretKey key = (SecretKey) mKeyStore.getKey(Constants.KEY_NAME, null);
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

    @TargetApi(Build.VERSION_CODES.M)
    private SecretKey createKey() {
        try {

            // Set the alias of the entry in Android KeyStore where the key will appear
            // and the constrains (purposes) in the constructor of the Builder
            mKeyGenerator.init(new KeyGenParameterSpec.Builder(Constants.KEY_NAME,
                    KeyProperties.PURPOSE_DECRYPT | KeyProperties.PURPOSE_ENCRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    // Require the user to authenticate with a fingerprint to authorize every use
                    // of the key
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            return mKeyGenerator.generateKey();

        } catch (Exception e) {

        }
        return null;
    }

    public Cipher getCipher(int mode) {
        Cipher cipher;


        try {
            mKeyStore.load(null);
            byte[] iv;
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            IvParameterSpec ivParams;
            if (mode == Cipher.ENCRYPT_MODE) {
                cipher.init(mode, getKey());

            } else {
                SecretKey key = (SecretKey) mKeyStore.getKey(Constants.KEY_NAME, null);
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
        if (isFingerPrintAuthOn) {
            removeFingerprintAuthentication();
        }
        return null;
    }

    private void removeFingerprintAuthentication() {
        ProfileInfoCacheManager.clearEncryptedPassword();
        showSettingsChangedForFingerPrintDialog();
    }

    private void showSettingsChangedForFingerPrintDialog() {
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(context);
        dialog
                .content(R.string.settings_changed_for_touch_id)
                .cancelable(false)
                .positiveText(R.string.ok)
                .show();
    }

    public void tryEncrypt(String password) {
        try {
            byte[] encrypted = mEncryptCipher.doFinal(password.getBytes());

            IvParameterSpec ivParams = mEncryptCipher.getParameters().getParameterSpec(IvParameterSpec.class);
            String iv = Base64.encodeToString(ivParams.getIV(), Base64.DEFAULT);

            SharedPreferences.Editor editor = mPref.edit();
            editor.putString(Constants.KEY_PASSWORD, Base64.encodeToString(encrypted, Base64.DEFAULT));
            editor.putString(Constants.KEY_PASSWORD_IV, iv);
            editor.commit();
            mEncryptionDialog.dismiss();
            mFinishEncryptionCheckerListener.ifEncryptionFinished();

        } catch (BadPaddingException | IllegalBlockSizeException e) {
        } catch (InvalidParameterSpecException e) {
            e.printStackTrace();
        }
    }

    public void tryDecrypt() {
        try {
            byte[] encodedData = Base64.decode(mPref.getString(Constants.KEY_PASSWORD, ""), Base64.DEFAULT);
            byte[] decodedData = mDecryptCipher.doFinal(encodedData);
            mDecryptionDialog.dismiss();
            mFinishDecryptionCheckerListener.ifDecryptionFinished(new String(decodedData));

        } catch (BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    public void stopFingerprintAuthenticationListener() {
        if (mFingerPrintHandler != null)
            mFingerPrintHandler.stopAuth();

        if (mStage == Stage.FINGERPRINT_ENCRYPT)
            mEncryptionDialog.dismiss();
        else mDecryptionDialog.dismiss();
    }

    public void cancelFingerprintAuthentication() {
        if (mFingerPrintHandler != null)
            mFingerPrintHandler.stopAuth();

        if (mStage == Stage.FINGERPRINT_ENCRYPT) {
            ProfileInfoCacheManager.clearEncryptedPassword();
            mFinishEncryptionCheckerListener.ifEncryptionFinished();
        } else
            mFinishDecryptionCheckerListener.ifDecryptionFinished(null);
    }

    public void setFinishEncryptionCheckerListener(FinishEncryptionCheckerListener finishEncryptionCheckerListener) {
        mFinishEncryptionCheckerListener = finishEncryptionCheckerListener;
    }

    public interface FinishEncryptionCheckerListener {
        void ifEncryptionFinished();
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
}


