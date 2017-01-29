package bd.com.ipay.ipayskeleton.Utilities;

public class PasswordManager {
    private static String password = "";

    public static String getPassword() {
        return password;
    }

    public static void invalidatePassword() {
        password = "";
    }

    public static void setPassword(String password) {
        password = password;
    }
}
