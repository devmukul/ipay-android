package bd.com.ipay.ipayskeleton.Utilities;

import android.content.Context;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bd.com.ipay.ipayskeleton.R;

/**
 * Validates user inputs (e.g. email, password, date of birth)
 */
public class InputValidator {

    public static String isPasswordValid(String password) {
        // Return empty string if the password is valid
        if (password.length() == 0) return "Enter a password";
        if (password.length() < 8) return "This password is too short";
        if (!password.matches(".*[a-zA-Z]+.*")) return "Password should contain an alphabet";
        if (!password.matches(".*[0-9]+.*")) return "Password should contain a number";
        return "";
    }

    // TODO return meaningful message based on the error
    public static boolean isDateOfBirthValid(String dob) {
        try {
            String[] dobFields = dob.split("/");

            int day = Integer.parseInt(dobFields[0]);
            int month = Integer.parseInt(dobFields[1]);
            int year = Integer.parseInt(dobFields[2]);

            return (day >= 1 && day <= 31 && month >= 1 && month <= 12 && year >= 0 && year <= 9999);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isValidEmail(String email) {
        Pattern emailPattern = Pattern.compile("[a-zA-Z0-9[!#$%&'()*+,/\\-_\\.\"]]+@[a-zA-Z0-9[!#$%&'()*+,/\\-_\"]]+\\.[a-zA-Z0-9[!#$%&'()*+,/\\-_\"\\.]]+");
        Matcher m = emailPattern.matcher(email);
        return m.matches();
    }

    public static String isValidAmount(Context context, BigDecimal amount, BigDecimal min_amount, BigDecimal max_amount) {
        String error_message = null;

        if (amount.compareTo(min_amount) == -1) {
            error_message = context.getResources().getString(R.string.please_enter_minimum_amount) +" "+ min_amount;
            return error_message;
        } else if (amount.compareTo(max_amount) == 1) {
            error_message = context.getResources().getString(R.string.please_enter_not_more_than_max_amount) + " " +max_amount;
            return error_message;
        }
        return error_message;
    }

}
