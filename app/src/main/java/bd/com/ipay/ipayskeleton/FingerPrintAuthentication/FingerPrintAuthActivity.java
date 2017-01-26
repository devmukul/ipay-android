package bd.com.ipay.ipayskeleton.FingerPrintAuthentication;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;
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

public class FingerprintAuthActivity extends Activity {

    private static final String TAG = FingerprintAuthActivity.class.getSimpleName();

    private static final String DIALOG_FRAGMENT_TAG = "myFragment";

    private boolean StageEncrypt = true;

    private SharedPreferences mPref;

    private FingerPrintAuthenticationDialogFragment mFragment;

    public Cipher mEncryptCipher;
    public Cipher mDecryptCipher;

    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;
    public KeyguardManager mKeyguardManager;
    public FingerprintManager mFingerprintManager;

    /**
     * Alias for our key in the Android Key Store
     */
    private static final String KEY_NAME = "com.softllc.password.key";
    private static final String KEY_PASSWORD = "EncryptedPassword";
    private static final String KEY_PASSWORD_IV = "EncryptedPasswordIV";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_activity_log);

        FingerprintAuthModule fingerprintAuthModule = new FingerprintAuthModule(this);
       /* mKeyStore = fingerprintAuthModule.getKeystore();
        mKeyGenerator = fingerprintAuthModule.getKeyGenerator();*/
        mKeyguardManager = fingerprintAuthModule.getKeyguardManager();
        mFingerprintManager = fingerprintAuthModule.getFingerprintManager();

        if(fingerprintAuthModule.checkIfFingerPrintSupported()) {
            if (StageEncrypt) {
                generateKey();
                if (cipherInit()) {
                    /*mFragment.setEncryptCipher(mEncryptCipher);

                    // Case 2 :  If Logged in normally and finger password is not encrypted then show finger print dialog and encrypt with it
                    // Case 2 : If password is not encrypted then encrypt it
                    mFragment.setStage(FingerPrintAuthenticationDialogFragment.Stage.FINGERPRINT_ENCRYPT);
                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_PASSWORD, "qqqqqqq1");
                    mFragment.setArguments(bundle);
                    mFragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);*/
                }
            }
        }

       /* if (!StageEncrypt)
            if (initEncryptCipher()) {
                mFragment.setEncryptCipher(mEncryptCipher);

                // Case 1 :  If Finger print pressed and password encrypted then decrypt it
                if (mPref.getString(KEY_PASSWORD, "") != "") {
                    if (initDecryptCipher()) {

                        // Show the fingerprint dialog to unlock the password
                        mFragment.setDecryptCipher(mDecryptCipher);

                        mFragment.setStage(FingerPrintAuthenticationDialogFragment.Stage.FINGERPRINT_DECRYPT);
                        mFragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
                        return;
                    }
                }
            }*/

               /* // Case 2 : If password is not encrypted then encrypt it
                mFragment.setStage(FingerprintAuthenticationDialogFragment.Stage.PASSWORD);
                Bundle bundle = new Bundle();
                bundle.putString(KEY_PASSWORD,password.getText().toString());
                mFragment.setArguments(bundle);
                mFragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);*/

        /*logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.encrypted_message).setVisibility(View.GONE);

                if (initEncryptCipher()) {
                    mFragment.setEncryptCipher(mEncryptCipher);

                    // Case 2 :  If Logged in normally and finger password is not encrypted then show finger print dialog and encrypt with it
                    if (mSharedPreferences.getString(KEY_PASSWORD, "") == "") {
                        // Case 2 : If password is not encrypted then encrypt it
                        mFragment.setStage(FingerprintAuthenticationDialogFragment.Stage.FINGERPRINT_ENCRYPT);
                        Bundle bundle = new Bundle();
                        bundle.putString(KEY_PASSWORD,password.getText().toString());
                        mFragment.setArguments(bundle);
                        mFragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
                        return;
                    }
                }

            }
        });*/

    }

    /*private boolean initEncryptCipher() {
            generateKey();

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

    public void onPurchased(String password, boolean bDecrypt, boolean bEncrypt) {
        showConfirmation(password + (bDecrypt ? " (fingerprint decrypt)" : "") + (bEncrypt ? " (fingerprint encrypt)" : ""));
    }
*/
    // Show confirmation, - show the plain text password
    private void showConfirmation(String password) {

        /*if (password != null) {
           *//* TextView v = (TextView) findViewById(R.id.encrypted_message);*//*
            v.setVisibility(View.VISIBLE);
            v.setText(password);
        }*/
    }

   /* *//**
     * Tries to encrypt some data with the generated key in {@link #createKey} which is
     * only works if the user has just authenticated via fingerprint.
     *//*
    public boolean tryEncrypt(String secert) {
        try {

            byte[] encrypted = mEncryptCipher.doFinal(secert.getBytes());

            IvParameterSpec ivParams = mEncryptCipher.getParameters().getParameterSpec(IvParameterSpec.class);
            String iv = Base64.encodeToString(ivParams.getIV(), Base64.DEFAULT);

            SharedPreferences.Editor editor = mPref.edit();
            editor.putString(KEY_PASSWORD, Base64.encodeToString(encrypted, Base64.DEFAULT));
            editor.putString(KEY_PASSWORD_IV, iv);
            editor.commit();
            return true;


        } catch (BadPaddingException | IllegalBlockSizeException e) {
            Toast.makeText(this, "Failed to encrypt the data with the generated key. "
                    + "Retry the purchase", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Failed to encrypt the data with the generated key." + e.getMessage());
        } catch (InvalidParameterSpecException e) {
            e.printStackTrace();
        }
        return false;
    }

    *//**
     * Tries to decrypt some data with the generated key in {@link #createKey} which is
     * only works if the user has just authenticated via fingerprint.
     *//*
    public String tryDecrypt() {
        try {

            byte[] encodedData = Base64.decode(mPref.getString(KEY_PASSWORD, ""), Base64.DEFAULT);
            byte[] decodedData = mDecryptCipher.doFinal(encodedData);
            return new String(decodedData);

        } catch (BadPaddingException | IllegalBlockSizeException e) {
            Toast.makeText(this, "Failed to decrypt the data with the generated key. "
                    + "Retry the purchase", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Failed to decrypt the data with the generated key." + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

        @TargetApi(Build.VERSION_CODES.M)
    private void generateKey()
    {
        try {
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mKeyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get KeyGenerator instance", e);
        }

    }
    private SecretKey getKey() {
        try {
            mKeyStore.load(null);
            SecretKey key = (SecretKey) mKeyStore.getKey(KEY_NAME, null);
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
            mKeyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME,
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
                SecretKey key = (SecretKey) mKeyStore.getKey(KEY_NAME, null);
                iv = Base64.decode(mPref.getString(KEY_PASSWORD_IV, ""), Base64.DEFAULT);
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
    }*/

    protected void generateKey() {
        try {
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            mKeyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get KeyGenerator instance", e);
        }


        try {
            mKeyStore.load(null);
            mKeyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            mKeyGenerator.generateKey();
        } catch (NoSuchAlgorithmException |
                InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean cipherInit() {
        try {
            mEncryptCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }


        try {
            mKeyStore.load(null);
            SecretKey key = (SecretKey) mKeyStore.getKey(KEY_NAME,
                    null);
            mEncryptCipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    public boolean tryEncrypt(String secert) {
        try {

            byte[] encrypted = mEncryptCipher.doFinal(secert.getBytes());

            IvParameterSpec ivParams = mEncryptCipher.getParameters().getParameterSpec(IvParameterSpec.class);
            String iv = Base64.encodeToString(ivParams.getIV(), Base64.DEFAULT);

            SharedPreferences.Editor editor = mPref.edit();
            editor.putString(KEY_PASSWORD, Base64.encodeToString(encrypted, Base64.DEFAULT));
            editor.putString(KEY_PASSWORD_IV, iv);
            editor.commit();
            return true;


        } catch (BadPaddingException | IllegalBlockSizeException e) {
            Toast.makeText(this, "Failed to encrypt the data with the generated key. "
                    + "Retry the purchase", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Failed to encrypt the data with the generated key." + e.getMessage());
        } catch (InvalidParameterSpecException e) {
            e.printStackTrace();
        }
        return false;
    }
}
