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
        if (password.length() < 8) return "Password must contain minimum 8 characters.";
        if (!password.matches(".*[a-zA-Z]+.*")) return "Password must contain a letter.";
        if (!password.matches(".*[0-9]+.*")) return "Password must contain a digit.";
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

    public static boolean isValidName(String name) {
        if (name.matches(".*[0-9]+.*")) return false;
        return true;
    }

    public static boolean isValidNameWithRequiredLength(String name) {
        name = name.replaceAll("\\s+", "");

        if (name.matches(".*[0-9]+.*") || name.length() < Constants.MIN_VALID_NAME_LENGTH)
            return false;
        return true;
    }

    public static String isValidAmount(Context context, BigDecimal amount, BigDecimal minAmount, BigDecimal maxAmount) {
        String errorMessage = null;

        if (minAmount.compareTo(maxAmount) >= 0) {
            errorMessage = context.getResources().getString(R.string.insufficient_balance);
            return errorMessage;
        } else {
            if (amount.compareTo(minAmount) == -1) {
                errorMessage = context.getResources().getString(R.string.please_enter_minimum_amount) + " " + Utilities.formatTaka(minAmount);
                return errorMessage;
            } else if (amount.compareTo(maxAmount) == 1) {
                errorMessage = context.getResources().getString(R.string.please_enter_not_more_than_max_amount) + " " + Utilities.formatTaka(maxAmount);
                return errorMessage;
            }
        }

        return errorMessage;
    }

    public static String isValidOTP(Context context, String otp) {
        String errorMessage = null;

        if (otp.length() == 0)
            errorMessage = context.getString(R.string.error_invalid_otp);
        else if (otp.length() != 6)
            errorMessage = context.getString(R.string.error_invalid_otp_with_required_length);
        return errorMessage;
    }

    public static String isValidDocumentID(Context context, String documentType, String documentID, int[] documentTypeNames) {
        String nID = context.getString(documentTypeNames[0]);
        String passportID = context.getString(documentTypeNames[1]);
        String errorMessage = "";
        if (documentID.length() == 0) {
            if (documentType.equals(nID))
                errorMessage = context.getString(R.string.please_enter_nid);

            else if (documentType.equals(passportID))
                errorMessage = context.getString(R.string.please_enter_passport_id);

            else errorMessage = context.getString(R.string.please_enter_driving_license_id);

        } else {
            if (documentType.equals(nID)) {
                int length = documentID.length();
                if (length >= Constants.MINIMUM_REQUIRED_NID_LENGTH && length <= Constants.MAXIMUM_REQUIRED_NID_LENGTH) {
                    errorMessage = null;
                } else if (length < Constants.MINIMUM_REQUIRED_NID_LENGTH) {
                    errorMessage = context.getString(R.string.invalid_nid_min_length);
                } else {
                    errorMessage = context.getString(R.string.invalid_nid_max_length);
                }
            } else if (documentType.equals(passportID)) {
                String invalidPassportIDInsufficientLength = "[A-Z]{2}[0-9]{0,6}|[A-Z]{1}";
                String validPassPortID = "[A-Z]{2}[0-9]{7}";
                if (documentID.matches(invalidPassportIDInsufficientLength))
                    errorMessage = context.getString(R.string.invalid_passport_ID_insufficient_length);
                else if (documentID.matches(validPassPortID))
                    errorMessage = null;
                else {
                    errorMessage = context.getString(R.string.invalid_passport_ID);
                }
            } else {
                String invalidDrivingLicenseIDInsufficientLength = "[A-Z]{2}[0-9]{0,7}|[A-Z]{1}|[A-Z]{2}[0-9]{7}[A-Z]{1,2}|" +
                        "[A-Z]{2}[0-9]{7}[A-Z]{1}[0-9]{1,4}|[A-Z]{2}[0-9]{7}[A-Z]{2}[0-9]{1,3}";

                String validDrivingLicenseID = "[A-Z]{2}[0-9]{7}[A-Z]{1}[0-9]{5}|[A-Z]{2}[0-9]{7}[A-Z]{2}[0-9]{4}";

                if (documentID.matches(validDrivingLicenseID))
                    errorMessage = null;
                else if (documentID.matches(invalidDrivingLicenseIDInsufficientLength))
                    errorMessage = context.getString(R.string.invalid_driving_license_ID_insufficient_length);
                else errorMessage = context.getString(R.string.invalid_driving_license_ID);
            }
        }
        return errorMessage;
    }
}
