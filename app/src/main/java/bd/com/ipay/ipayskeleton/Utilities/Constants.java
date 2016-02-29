package bd.com.ipay.ipayskeleton.Utilities;

public class Constants {
    public static final String ApplicationTag = "iPay";
    public static final String ApplicationPackage = "bd.com.ipay.ipayskeleton";
    public static final String PICTURE_FOLDER = "/iPay";
    public static final String GENDER_MALE = "M";
    public static final String GENDER_FEMALE = "F";
    public static final String TOKEN = "token";
    public static final String NEW_TOKEN = "New-Token";
    public static final String USERNAME = "USERNAME";
    public static final String EMAIL = "EMAIL";
    public static final String FIRST_LAUNCH = "FIRST_LAUNCH";
    public static final String USERID = "USERID";
    public static final String USERCOUNTRY = "USERCOUNTRY";
    public static final String LOGGEDIN = "LOGGEDIN";
    public static final String PASSWORD = "PASSWORD";
    public static final String FIRST_NAME = "FIRST_NAME";
    public static final String BIRTHDAY = "BIRTHDAY";
    public static final String GENDER = "GENDER";
    public static final String LAST_NAME = "LAST_NAME";
    public static final String ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public static final String BOUNDARY = "iPayBoundary";
    public static final String MOBILE_ANDROID = "mobile-android-";
    public static final String DUMMY = "DUMMY";
    public static final String MULTIPART_FORM_DATA_NAME = "file";
    public static final String STARTING_DATE_OF_IPAY = "01/01/2016";
    public static final int STARTING_DATE = 01;
    public static final int STARTING_MONTH = 01;
    public static final int STARTING_YEAR = 2016;

    // For POST Requests
    public static final String BASE_URL_POST_MM = "http://10.15.10.10:8080/mmweb/";
//    public static final String BASE_URL_POST_MM = "http://10.15.10.10:8080/mmweb/";

    // For GET Requests
    public static final String BASE_URL_GET_MM = "10.15.10.10";
    //    public static final String BASE_URL_GET_MM = "10.15.10.10";
    public static final int BASE_URL_GET_MM_PORT = 8080;
    public static final String BASE_URL_GET_MM_PATH = "/mmweb";

    // For Images
//    public static final String BASE_URL_IMAGE_SERVER = "http://10.10.10.10/";
    public static final String BASE_URL_IMAGE_SERVER = "http://10.10.10.10";

    public static final String BASE_URL_SM = "http://10.15.10.10:8080/ism/";
//    public static final String BASE_URL_IMAGE = "http://live.ipay.com.bd:81";

    public static final String URL_SIGN_UP = "signup/activation";
    public static final String URL_SIGN_UP_BUSINESS = "signup/business/activation";
    public static final String URL_OTP_REQUEST = "signup";
    public static final String URL_OTP_REQUEST_BUSINESS = "signup/business";
    public static final String URL_LOGIN = "signin";
    public static final String URL_SEND_OTP_FORGET_PASSWORD = "settings/password/forget";
    public static final String URL_ADD_TRUSTED_DEVICE = "settings/device/add";
    public static final String URL_CONFIRM_OTP_FORGET_PASSWORD = "settings/password/forget/confirmation";
    public static final String URL_SEND_MONEY = "transaction/send";
    public static final String URL_SEND_MONEY_QUERY = "transaction/send/query";
    public static final String URL_REFRESH_BALANCE = "user/balance";
    public static final String URL_GET_USER_INFO = "user/userinfo";
    public static final String URL_GET_NEWS_FEED = "news";
    public static final String URL_TOPUP_REQUEST = "topup/dotopup";
    public static final String URL_EMAIL_VERIFICATION = "settings/email/verification";
    public static final String URL_SET_PIN = "settings/pin/change";
    public static final String URL_CHANGE_PASSWORD = "settings/password/change";
    public static final String URL_GET_PROFILE_INFO_REQUEST = "user/profile/get";
    public static final String URL_SET_PROFILE_INFO_REQUEST = "user/profile/set";
    public static final String URL_LOG_OUT = "signout";
    public static final String URL_USER_ACTIVITY = "report/activities";
    public static final String URL_TRANSACTION_HISTORY = "report/transactions";
    public static final String URL_REQUEST_MONEY = "requestmoney";
    public static final String URL_PENDING_PAYMENT_REQUEST_RECEIVED = "payment/received";
    public static final String URL_PENDING_PAYMENT_REQUEST_SENT = "payment/sent";
    public static final String URL_PENDING_REQUEST_MONEY_TO_ME = "requestmoney/received";
    public static final String URL_PENDING_REQUEST_MONEY_FROM_ME = "requestmoney/sent";
    public static final String URL_REQUEST_MONEY_ACCEPT = "requestmoney/accept";
    public static final String URL_REQUEST_REJECT = "requestmoney/reject";
    public static final String URL_REQUEST_CANCEL = "requestmoney/cancel";
    public static final String URL_PAYMENT_REQUEST_ACCEPT = "payment/accept";
    public static final String URL_PAYMENT_REQUEST_REJECT = "payment/reject";
    public static final String URL_PAYMENT_REQUEST_CANCEL = "payment/cancel";
    public static final String URL_PAYMENT_CREATE_INVOICE = "payment/invoice";
    public static final String URL_SET_PROFILE_PICTURE = "user/profile/profilepicture/set";
    public static final String URL_DOWNLOAD_PROFILE_PICTURE_FRIEND = "user/profile/profilepicture/download/friend";
    public static final String URL_ADD_A_BANK = "bank/add";
    public static final String URL_REMOVE_A_BANK = "bank/remove";
    public static final String URL_DISABLE_A_BANK = "bank/disable";
    public static final String URL_ENABLE_A_BANK = "bank/enable";
    public static final String URL_GET_BANK = "bank/get";
    public static final String URL_ADD_MONEY = "banktransaction/cashin";
    public static final String URL_WITHDRAW_MONEY = "banktransaction/cashout";

    public static final String HTTP_RESPONSE_STATUS_NOT_ACCEPTABLE = "406";
    public static final String HTTP_RESPONSE_STATUS_OK = "200";
    public static final String HTTP_RESPONSE_STATUS_PROCESSING = "102";
    public static final String HTTP_RESPONSE_STATUS_UNAUTHORIZED = "401";
    public static final String HTTP_RESPONSE_STATUS_BAD_REQUEST = "400";
    public static final String HTTP_RESPONSE_STATUS_ACCEPTED = "202";

    public static final int PERSONAL_ACCOUNT_TYPE = 1;
    public static final int BUSINESS_ACCOUNT_TYPE = 2;
    public static final int BANK_ACCOUNT_STATUS_VERIFIED = 1;
    public static final int BANK_ACCOUNT_STATUS_NOT_VERIFIED = 0;
    public static final int BANK_ACCOUNT_STATUS_ACTIVE = 0;
    public static final int BANK_ACCOUNT_STATUS_INACTIVE = 1;
    public static final int BANK_ACCOUNT_STATUS_DELETED = 2;

    public static final String COMMAND_OTP_VERIFICATION = "COMMAND_OTP_VERIFICATION";
    public static final String COMMAND_SIGN_UP = "COMMAND_SIGN_UP";
    public static final String COMMAND_SIGN_UP_BUSINESS = "COMMAND_SIGN_UP_BUSINESS";
    public static final String COMMAND_LOG_IN = "COMMAND_LOG_IN";
    public static final String COMMAND_FORGET_PASSWORD_SEND_OTP = "COMMAND_FORGET_PASSWORD_SEND_OTP";
    public static final String COMMAND_FORGET_PASSWORD_CONFIRM_OTP = "COMMAND_FORGET_PASSWORD_CONFIRM_OTP";
    public static final String COMMAND_LOG_OUT = "COMMAND_LOG_OUT";
    public static final String COMMAND_SEND_MONEY = "COMMAND_SEND_MONEY";
    public static final String COMMAND_SEND_MONEY_QUERY = "COMMAND_SEND_MONEY_QUERY";
    public static final String COMMAND_REFRESH_BALANCE = "COMMAND_REFRESH_BALANCE";
    public static final String COMMAND_GET_USER_ACTIVITIES = "COMMAND_GET_USER_ACTIVITIES";
    public static final String COMMAND_GET_TRANSACTION_HISTORY = "COMMAND_GET_TRANSACTION_HISTORY";
    public static final String COMMAND_ADD_TRUSTED_DEVICE = "COMMAND_ADD_TRUSTED_DEVICE";
    public static final String COMMAND_GET_PENDING_REQUESTS_OTHERS = "COMMAND_GET_PENDING_REQUESTS_OTHERS";
    public static final String COMMAND_GET_PENDING_PAYMENT_REQUESTS_RECEIVED = "COMMAND_GET_PENDING_PAYMENT_REQUESTS_RECEIVED";
    public static final String COMMAND_GET_PENDING_PAYMENT_REQUESTS_SENT = "COMMAND_GET_PENDING_PAYMENT_REQUESTS_SENT";
    public static final String COMMAND_CANCEL_REQUESTS_MONEY = "COMMAND_CANCEL_REQUESTS_MONEY";
    public static final String COMMAND_ACCEPT_REQUESTS_MONEY = "COMMAND_ACCEPT_REQUESTS_MONEY";
    public static final String COMMAND_REJECT_REQUESTS_MONEY = "COMMAND_REJECT_REQUESTS_MONEY";
    public static final String COMMAND_CANCEL_PAYMENT_REQUEST = "COMMAND_CANCEL_PAYMENT_REQUEST";
    public static final String COMMAND_ACCEPT_PAYMENT_REQUEST = "COMMAND_ACCEPT_PAYMENT_REQUEST";
    public static final String COMMAND_REJECT_PAYMENT_REQUEST = "COMMAND_REJECT_PAYMENT_REQUEST";
    public static final String COMMAND_GET_PENDING_REQUESTS_ME = "COMMAND_GET_PENDING_REQUESTS_ME";
    public static final String COMMAND_REQUEST_MONEY = "COMMAND_REQUEST_MONEY";
    public static final String COMMAND_CREATE_INVOICE = "COMMAND_CREATE_INVOICE";
    public static final String COMMAND_SET_PROFILE_PICTURE = "COMMAND_SET_PROFILE_PICTURE";
    public static final String COMMAND_ADD_A_BANK = "COMMAND_ADD_A_BANK";
    public static final String COMMAND_REMOVE_A_BANK = "COMMAND_REMOVE_A_BANK";
    public static final String COMMAND_DISABLE_A_BANK = "COMMAND_DISABLE_A_BANK";
    public static final String COMMAND_ENABLE_A_BANK = "COMMAND_ENABLE_A_BANK";
    public static final String COMMAND_GET_PROFILE_PICTURE_URL = "COMMAND_GET_PROFILE_PICTURE_URL";
    public static final String COMMAND_DOWNLOAD_PROFILE_PICTURE = "COMMAND_DOWNLOAD_PROFILE_PICTURE";
    public static final String COMMAND_TOPUP_REQUEST = "COMMAND_TOPUP_REQUEST";
    public static final String COMMAND_SET_PIN = "COMMAND_SET_PIN";
    public static final String COMMAND_CHANGE_PASSWORD = "COMMAND_CHANGE_PASSWORD";
    public static final String COMMAND_GET_PROFILE_INFO_REQUEST = "COMMAND_GET_PROFILE_INFO_REQUEST";
    public static final String COMMAND_EMAIL_VERIFICATION_REQUEST = "COMMAND_EMAIL_VERIFICATION_REQUEST";
    public static final String COMMAND_SET_PROFILE_INFO_REQUEST = "COMMAND_SET_PROFILE_INFO_REQUEST";
    public static final String COMMAND_GET_BANK_LIST = "COMMAND_GET_BANK_LIST";
    public static final String COMMAND_ADD_MONEY = "COMMAND_ADD_MONEY";
    public static final String COMMAND_GET_USER_INFO = "COMMAND_GET_USER_INFO";
    public static final String COMMAND_GET_NEWS_FEED = "COMMAND_GET_NEWS_FEED";
    public static final String COMMAND_WITHDRAW_MONEY = "COMMAND_WITHDRAW_MONEY";
    public static final String COMMAND_DOWNLOAD_PROFILE_PICTURE_FRIEND = "COMMAND_DOWNLOAD_PROFILE_PICTURE_FRIEND";

    // FireBase
    public static final String PATH_TO_IPAY_USERS = "https://ipay-com-test.firebaseio.com/data/iPay-users/";
    public static final String PATH_TO_USER_CONTACTS = "https://ipay-com-test.firebaseio.com/data/users-contacts/";
    public static final String IPAY_USERS_PRIMARY_KEY_MOBILE_NUMBER = "mobileNumber/";
    public static final String IPAY_USER_CONTACTS_PRIMARY_KEY_MOBILE_NUMBER = "mobileNumber/";

    public static final int TRANSACTION_TYPE_TOP_UP_REQUEST = 4097;
    public static final int TRANSACTION_TYPE_TOP_UP_RESPONSE = 4098;
    public static final int TRANSACTION_TYPE_TOP_UP_ROLLBACK_REQUEST = 4099;
    public static final int TRANSACTION_TYPE_TOP_UP_ROLLBACK_RESPONSE = 4100;
    public static final int TRANSACTION_TYPE_SEND_MONEY_REQUEST = 0x3101;
    public static final int TRANSACTION_TYPE_SEND_MONEY_RESPONSE = 0x3102;
    public static final int TRANSACTION_TYPE_CASH_IN_REQUEST = 0x3103;
    public static final int TRANSACTION_TYPE_CASH_OUT_REQUEST = 0x3105;

    public static final int TRANSACTION_TYPE_DEBIT = 1;
    public static final int TRANSACTION_TYPE_CREDIT = -1;

    public static final int ACTIVITY_TYPE_CHANGE_PROFILE = 0;
    public static final int ACTIVITY_TYPE_MONEY_IN = 1;
    public static final int ACTIVITY_TYPE_MONEY_OUT = 2;
    public static final int ACTIVITY_TYPE_VERIFICATION = 3;
    public static final int ACTIVITY_TYPE_SYSTEM_EVENT = 4;
    public static final int ACTIVITY_TYPE_CHANGE_SECURITY = 5;

    public static final int EMAIL_VERIFICATION_STATUS_VERIFIED = 2;
    public static final int EMAIL_VERIFICATION_STATUS_NOT_VERIFIED = 0;
    public static final int EMAIL_VERIFICATION_STATUS_VERIFICATION_IN_PROGRESS = 1;
}
