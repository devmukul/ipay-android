package bd.com.ipay.ipayskeleton.Utilities;

import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

public class EncryptionManager {

    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;

    private Cipher mEncryptCipher;
    private Cipher mDecryptCipher;

}
