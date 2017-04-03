package bd.com.ipay.ipayskeleton.Utilities;

import java.net.URL;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

public class SSLPinning {

    static int REQUIRED_CERTIFICATES = 3;
    static final Set<String> PINS = new HashSet<>(Arrays.asList(
            new String[]{
                    "98b358c0d6d537da39fa5050b1139584774b62536f939aa2aad2a2e6ae30cd06",
                    "4ca704ad0d9f7dea75212451fb80525e72e3c1badbfe79c3865ec8c9eb756581"}));

    public static boolean validatePinning() {
        if (Constants.SERVER_TYPE == Constants.SERVER_TYPE_LIVE) {
            try {
                URL targetURL = new URL(Constants.BASE_URL_WEB);
                HttpsURLConnection targetConnection = (HttpsURLConnection) targetURL.openConnection();
                targetConnection.connect();

                Certificate[] certs = targetConnection.getServerCertificates();
                MessageDigest md = MessageDigest.getInstance("SHA-256");

                for (Certificate cert : certs) {
                    X509Certificate x509Certificate = (X509Certificate) cert;
                    byte[] key = x509Certificate.getPublicKey().getEncoded();
                    md.update(key, 0, key.length);
                    byte[] hashBytes = md.digest();
                    StringBuffer hexHash = new StringBuffer();
                    for (int i = 0; i < hashBytes.length; i++) {
                        int k = 0xFF & hashBytes[i];
                        String tmp = (k < 16) ? "0" : "";
                        tmp += Integer.toHexString(0xFF & hashBytes[i]);
                        hexHash.append(tmp);
                    }
                    if (PINS.contains(hexHash.toString())) {
                        return true;
                    }
                }

                // Bypassing if one of the certificates missing
                if (certs.length < REQUIRED_CERTIFICATES) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return false;
        } else return true;
    }
}

