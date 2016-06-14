package bd.com.ipay.ipayskeleton.Utilities;

public class Constants {
    public static final String ApplicationTag = "iPay";
    public static final String ApplicationPackage = "bd.com.ipay.android";
    public static final String PICTURE_FOLDER = "/iPay";
    public static final String GENDER_MALE = "M";
    public static final String GENDER_FEMALE = "F";
    public static final String EMAIL = "EMAIL";
    public static final String USERID = "USERID";
    public static final String UUID = "UUID";
    public static final String USERCOUNTRY = "USERCOUNTRY";
    public static final String USER_AGENT = "User-Agent";
    public static final String USER_AGENT_MOBILE_ANDROID = "mobile-android";
    public static final String LOGGEDIN = "LOGGEDIN";
    public static final String FIRST_LAUNCH = "FIRST_LAUNCH";
    public static final String GCM_REGISTRATION_ID_SENT_TO_SERVER = "GCM_REGISTRATION_ID_SENT_TO_SERVER";
    public static final String GCM_REGISTRATION_COMPLETE = "GCM_REGISTRATION_COMPLETE";
    public static final String PASSWORD = "PASSWORD";
    public static final String VERIFICATION_STATUS = "VERIFICATION_STATUS";
    public static final String TARGET_FRAGMENT = "TARGET_FRAGMENT";
    public static final String VERIFIED_USERS_ONLY = "VERIFIED_USERS_ONLY";
    public static final String SIGN_IN = "SIGN_IN";
    public static final String SIGN_UP = "SIGN_UP";
    public static final String DOCUMENT_URL = "DOCUMENT_URL";
    public static final String FILE_EXTENSION = "FILE_EXTENSION";
    public static final String DEVICE_ID = "DEVICE_ID";

    public static final String SMS_READER_BROADCAST_RECEIVER_PDUS = "pdus";

    public static final String TOKEN = "token";
    public static final String REFRESH_TOKEN = "refresh-token";
    public static final String RESOURCE_TOKEN = "resource-token";

    public static final String PROFILE_PICTURE = "PROFILE_PICTURE";
    public static final String PROFILE_INFO_UPDATED = "PROFILE_INFO_UPDATED";
    public static final String MOBILE_NUMBER = "MOBILE_NUMBER";
    public static final String BIRTHDAY = "BIRTHDAY";
    public static final String DATE_OF_BIRTH = "DATE_OF_BIRTH";
    public static final String GENDER = "GENDER";
    public static final String ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public static final String BOUNDARY = "iPayBoundary";
    public static final String ANDROID = "Android";
    public static final String IS_PIN_ADDED = "is-pin-added";
    public static final String MOBILE_ANDROID = "mobile-android-";
    public static final String DUMMY = "DUMMY";
    public static final String THANA = "thana";
    public static final String DISTRICT = "district";
    public static final String MULTIPART_FORM_DATA_NAME = "file";
    public static final String STARTING_DATE_OF_IPAY = "01/01/2016";
    public static final String MOBILE_NUMBER_TYPE = "mobile_number_type";
    public static final String OPERATOR_CODE = "operator_code";
    public static final String COUNTRY_CODE = "country_code";
    public static final String EXPAND_PIN = "expand_pin";
    public static final String TRUSTED_OTP_RECEIVERS = "trusted_otp_receivers";
    public static final String DOCUMENT_ID = "DOCUMENT_ID";
    public static final String DOCUMENT_TYPE_NAME = "DOCUMENT_TYPE_NAME";
    public static final String MESSAGE = "message";

    public static final String NAME = "NAME";
    public static final String OCCUPATION = "OCCUPATION";

    public static final String FATHERS_NAME = "FATHERS_NAME";
    public static final String MOTHERS_NAME = "MOTHERS_NAME";
    public static final String SPOUSES_NAME = "SPOUSES_NAME";

    public static final String FATHERS_MOBILE_NUMBER = "FATHERS_MOBILE_NUMBER";
    public static final String MOTHERS_MOBILE_NUMBER = "MOTHERS_MOBILE_NUMBER";
    public static final String SPOUSES_MOBILE_NUMBER = "SPOUSES_MOBILE_NUMBER";

    public static final String ADDRESS = "ADDRESS";
    public static final String ADDRESS_TYPE = "ADDRESS_TYPE";

    public static final String AMOUNT = "amount";
    public static final String BANK_NAME = "bank_name";
    public static final String BANK_ACCOUNT_NUMBER = "bank_account_number";
    public static final String BANK_ACCOUNT_ID = "bank_account_id";

    public static final String SERVICE_CHARGE = "service_charge";
    public static final String PHOTO_URI = "photo_uri";
    public static final String VAT = "vat";

    public static final int STARTING_DATE = 01;
    public static final int STARTING_MONTH = 01;
    public static final int STARTING_YEAR = 2016;
    public static final int DEFAULT_USER_CLASS = 1;

    /**
     * All requests and responses to$ server, and token is printed when debug flag is enabled.
     * Besides, for safety measures, all later flags won't work unless DEBUG flag is set.
     */
    public static final boolean DEBUG = true;

    /**
     * If set to true (with DEBUG flag also being set to true),
     * it works like the "Remember me" function. Mobile number and password will be
     * required only for the first time when user tries to login. After that, login request will be
     * automatically sent to the server using the previously used mobile number and default password
     * (qqqqqqq1).
     * <p>
     * *** Set it to false if you are not using the default password ***
     */
    public static final boolean AUTO_LOGIN = false;

    // Server Type 1 -> dev server
    // Server Type 2 -> test server
    // Server Type 3 -> stage server
    // Server Type 4 -> live server
    // Server Type 5 -> local server
    public static final int SERVER_TYPE = 2;

    public static final String BASE_URL_MM;
    public static final String BASE_URL_SM;
    public static final String BASE_URL_FRIEND;
    public static final String BASE_URL_FTP_SERVER;
    public static final String SERVER_NAME;

    static {
        if (SERVER_TYPE == 1) {

            BASE_URL_MM = "http://10.10.10.10:8085/api/v1/";
            BASE_URL_SM = "http://10.10.10.11:8085/api/v1/money/";
            BASE_URL_FTP_SERVER = "http://10.10.10.10";
//            BASE_URL_FRIEND = "http://192.168.1.105:1337/v1/";
            BASE_URL_FRIEND = "http://10.10.10.11:1337/v1/";
            SERVER_NAME = "dev";

        } else if (SERVER_TYPE == 2) {

            BASE_URL_MM = "http://10.15.40.10:8085/api/v1/";
            BASE_URL_SM = "http://10.15.40.11:8085/api/v1/money/";
            BASE_URL_FTP_SERVER = "http://10.15.40.14";
            BASE_URL_FRIEND = "http://10.15.40.14:1337/v1/";
            SERVER_NAME = "test";

        } else if (SERVER_TYPE == 3) {

            BASE_URL_MM = "http://10.10.40.10:8085/api/v1/";
            BASE_URL_SM = "http://10.10.40.11:8085/api/v1/money/";
            BASE_URL_FTP_SERVER = "http://10.10.40.14";
            BASE_URL_FRIEND = "http://10.10.40.14:1337/friend/v1/";
            SERVER_NAME = "stage";

        } else if (SERVER_TYPE == 4) {

            BASE_URL_MM = "https://www.ipay.com.bd/api/v1/";
            BASE_URL_SM = "https://www.ipay.com.bd/api/v1/money/";
            BASE_URL_FTP_SERVER = "https://www.ipay.com.bd";
            BASE_URL_FRIEND = "https://www.ipay.com.bd/friend/v1/";
            SERVER_NAME = "live";

        } else {

            BASE_URL_MM = "http://192.168.1.105:8085/api/v1/";
            BASE_URL_SM = "http://192.168.1.105:8085/api/v1/money/";
            BASE_URL_FTP_SERVER = "http://10.10.10.10";
            BASE_URL_FRIEND = "http://dev.ipay.com.bd/friend/v1/";
            SERVER_NAME = "local";
        }
    }


    // Activity REST
    public static final String URL_USER_ACTIVITY = "/activity";

    // Bank Operation REST
    public static final String URL_ADD_A_BANK = "bank/";
    public static final String URL_GET_BANK = "bank/";
    public static final String URL_REMOVE_A_BANK = "bank/";

    // Bank Transaction REST
    public static final String URL_ADD_MONEY = "banktransaction/cashin";
    public static final String URL_WITHDRAW_MONEY = "banktransaction/cashout";

    // Bank Verify Rest
    public static final String URL_SEND_FOR_VERIFICATION_BANK = "bank-verify";
    public static final String URL_BANK_VERIFICATION_WITH_AMOUNT = "bank-verify/check";

    // Trusted device CRUD operations
    public static final String URL_ADD_TRUSTED_DEVICE = "device";
    public static final String URL_GET_TRUSTED_DEVICES = "device";
    public static final String URL_REMOVE_TRUSTED_DEVICE = "device/";

    // Documents Rest
    public static final String URL_GET_DOCUMENTS = "docs/identification/documents";
    public static final String URL_UPLOAD_DOCUMENTS = "docs/identification/documents";
    public static final String URL_GET_DOCUMENT_ACCESS_TOKEN = "user/contentAccessToken";

    // Event Controller
    public static final String URL_EVENT_LIST = "events/user/eventList/";
    public static final String URL_EVENT_CATEGORIES = "categories";

    // Fee Charge REST
    public static final String URL_SERVICE_CHARGE = "feecharge";

    // Introducer REST
    public static final String URL_ASK_FOR_INTRODUCTION = "/introducer/introduceme/";
    public static final String URL_GET_DOWNSTREAM_NOT_APPROVED_INTRODUCTION_REQUESTS = "introducer/downstream/notapproved";
    public static final String URL_GET_DOWNSTREAM_APPROVED_INTRODUCTION_REQUESTS = "introducer/downstream/approved";
    public static final String URL_GET_UPSTREAM_NOT_APPROVED_INTRODUCTION_REQUESTS = "introducer/upstream/notapproved";
    public static final String URL_GET_UPSTREAM_APPROVED_INTRODUCTION_REQUESTS = "introducer/upstream/approved";
    public static final String URL_INTRODUCE_ACTION = "introducer/";

    // Invite Rest
    public static final String URL_GET_INVITE_INFO = "invitation";
    public static final String URL_SEND_INVITE = "invitation/invite/";


    // Mobile Topup Request REST
    public static final String URL_TOPUP_REQUEST = "topup/dotopup";

    // Money Request REST
    public static final String URL_REQUEST_MONEY = "requestmoney";

    // News Feed REST
    public static final String URL_GET_NEWS_FEED = "resource/news";

    // Request Rest
    public static final String URL_GET_NOTIFICATIONS = "requests/received";
    public static final String URL_GET_SENT_REQUESTS = "requests/sent";
    public static final String URL_ACCEPT_NOTIFICATION_REQUEST = "requests/accept";
    public static final String URL_CANCEL_NOTIFICATION_REQUEST = "requests/cancel";
    public static final String URL_REJECT_NOTIFICATION_REQUEST = "requests/reject";

    // Settings REST
    public static final String URL_CHANGE_PASSWORD = "settings/password";
    public static final String URL_SEND_OTP_FORGET_PASSWORD = "settings/password/forget";
    public static final String URL_CONFIRM_OTP_FORGET_PASSWORD = "settings/password/forget/confirmation";

    public static final String URL_GET_PIN_INFO = "settings/pin";
    public static final String URL_SET_PIN = "settings/pin";

    // Signin Rest
    public static final String URL_GET_REFRESH_TOKEN = "signin/refreshToken";
    public static final String URL_LOGIN = "signin";

    // Signout Rest
    public static final String URL_LOG_OUT = "signout";

    // Signup Rest
    public static final String URL_SIGN_UP = "signup/activation";
    public static final String URL_SIGN_UP_BUSINESS = "signup/business/activation";
    public static final String URL_OTP_REQUEST = "signup";
    public static final String URL_CHECK_PROMO_CODE = "signup/checkpromocode";
    public static final String URL_OTP_REQUEST_BUSINESS = "signup/business";

    // SM Payment REST
    public static final String URL_PAYMENT_SEND_INVOICE = "payment/invoice/send";
    public static final String URL_PAYMENT_SAVE_INVOICE = "payment/invoice/save";

    // SM Reports REST
    public static final String URL_TRANSACTION_HISTORY = "report/transactions";

    // SM User Rest
    public static final String URL_REFRESH_BALANCE = "user/balance";

    // Static Resource REST
    public static final String URL_RESOURCE = "resource";

    // Transaction REST
    public static final String URL_SEND_MONEY = "transaction/send";

    // Trusted Network REST
    public static final String URL_GET_TRUSTED_PERSONS = "/trustednetwork/trustedpersons/";
    public static final String URL_POST_TRUSTED_PERSONS = "/trustednetwork/trustedpersons/";
    public static final String URL_SET_RECOVERY_PERSON = "/recovery";

    // User Rest
    public static final String URL_GET_USER_INFO = "user/userinfo";
    public static final String URL_GET_PROFILE_INFO_REQUEST = "user/profile";
    ;
    public static final String URL_SET_PROFILE_INFO_REQUEST = "user/profile";

    // User Rest (Profile Completion)
    public static final String URL_GET_PROFILE_COMPLETION_STATUS = "/user/profilecompletion";

    // User Rest (Profile Picture)
    public static final String URL_SET_PROFILE_PICTURE = "user/profile/profilepicture/";

    // User Rest (Address)
    public static final String URL_GET_USER_ADDRESS_REQUEST = "user/profile/address";
    public static final String URL_SET_USER_ADDRESS_REQUEST = "user/profile/address";

    // User Rest (Email)
    public static final String URL_GET_EMAIL = "/user/emails/";
    public static final String URL_POST_EMAIL = "/user/emails/";
    public static final String URL_DELETE_EMAIL = "/user/emails/";
    public static final String URL_MAKE_EMAIL_VERIFIED = "/verification/";
    public static final String URL_MAKE_PRIMARY_EMAIL = "/primary";

    // User Rest (Friends)
    public static final String URL_GET_FRIENDS = "getfriends";
    public static final String URL_ADD_FRIENDS = "addfriends";
    public static final String URL_DELETE_FRIEND = "deletefriend";
    public static final String URL_UPDATE_FRIENDS = "updatefriends";

    public static final String URL_GET_ALL_PARTICIPANTS_LIST = "banktransaction/cashout";  // TODO: change

    public static final int HTTP_RESPONSE_STATUS_NOT_ACCEPTABLE = 406;
    public static final int HTTP_RESPONSE_STATUS_NOT_FOUND = 404;
    public static final int HTTP_RESPONSE_STATUS_OK = 200;
    public static final int HTTP_RESPONSE_STATUS_PROCESSING = 102;
    public static final int HTTP_RESPONSE_STATUS_UNAUTHORIZED = 401;
    public static final int HTTP_RESPONSE_STATUS_BAD_REQUEST = 400;
    public static final int HTTP_RESPONSE_STATUS_ACCEPTED = 202;
    public static final int HTTP_RESPONSE_STATUS_CANCELED = 2;
    public static final int HTTP_RESPONSE_STATUS_REJECTED = 3;
    public static final int HTTP_RESPONSE_STATUS_DRAFT = 4;

    public static final int PERSONAL_ACCOUNT_TYPE = 1;
    public static final int BUSINESS_ACCOUNT_TYPE = 2;
    public static final String BANK_ACCOUNT_STATUS_VERIFIED = "VERIFIED";
    public static final String BANK_ACCOUNT_STATUS_PENDING = "PENDING";
    public static final String BANK_ACCOUNT_STATUS_NOT_VERIFIED = "NOT_VERIFIED";

    public static final int MOBILE_TYPE_PREPAID = 1;
    public static final int MOBILE_TYPE_POSTPAID = 2;

    public static final String COMMAND_OTP_VERIFICATION = "COMMAND_OTP_VERIFICATION";
    public static final String COMMAND_CHECK_PROMO_CODE = "COMMAND_CHECK_PROMO_CODE";
    public static final String COMMAND_REFRESH_TOKEN = "COMMAND_REFRESH_TOKEN";
    public static final String COMMAND_SIGN_UP = "COMMAND_SIGN_UP";
    public static final String COMMAND_SIGN_UP_BUSINESS = "COMMAND_SIGN_UP_BUSINESS";
    public static final String COMMAND_LOG_IN = "COMMAND_LOG_IN";
    public static final String COMMAND_ASK_FOR_RECOMMENDATION = "COMMAND_ASK_FOR_RECOMMENDATION";
    public static final String COMMAND_FORGET_PASSWORD_SEND_OTP = "COMMAND_FORGET_PASSWORD_SEND_OTP";
    public static final String COMMAND_FORGET_PASSWORD_CONFIRM_OTP = "COMMAND_FORGET_PASSWORD_CONFIRM_OTP";
    public static final String COMMAND_LOG_OUT = "COMMAND_LOG_OUT";
    public static final String COMMAND_SEND_MONEY = "COMMAND_SEND_MONEY";
    public static final String COMMAND_GET_SERVICE_CHARGE = "COMMAND_GET_SERVICE_CHARGE";
    public static final String COMMAND_SEND_MONEY_QUERY = "COMMAND_SEND_MONEY_QUERY";
    public static final String COMMAND_REFRESH_BALANCE = "COMMAND_REFRESH_BALANCE";
    public static final String COMMAND_GET_USER_ACTIVITIES = "COMMAND_GET_USER_ACTIVITIES";
    public static final String COMMAND_GET_TRANSACTION_HISTORY = "COMMAND_GET_TRANSACTION_HISTORY";
    public static final String COMMAND_GET_NOTIFICATIONS = "COMMAND_GET_NOTIFICATIONS";
    public static final String COMMAND_ADD_TRUSTED_DEVICE = "COMMAND_ADD_TRUSTED_DEVICE";
    public static final String COMMAND_GET_TRUSTED_DEVICES = "COMMAND_GET_TRUSTED_DEVICES";
    public static final String COMMAND_REMOVE_TRUSTED_DEVICE = "COMMAND_REMOVE_TRUSTED_DEVICE";
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
    public static final String COMMAND_GET_ALL_PARTICIPANTS_LIST = "COMMAND_GET_ALL_PARTICIPANTS_LIST";
    public static final String COMMAND_REQUEST_MONEY = "COMMAND_REQUEST_MONEY";
    public static final String COMMAND_SEND_INVOICE = "COMMAND_SEND_INVOICE";
    public static final String COMMAND_SAVE_INVOICE = "COMMAND_SAVE_INVOICE";
    public static final String COMMAND_SET_PROFILE_PICTURE = "COMMAND_SET_PROFILE_PICTURE";
    public static final String COMMAND_ADD_A_BANK = "COMMAND_ADD_A_BANK";
    public static final String COMMAND_SEND_FOR_VERIFICATION_BANK = "COMMAND_SEND_FOR_VERIFICATION_BANK";
    public static final String COMMAND_VERIFICATION_BANK_WITH_AMOUNT = "COMMAND_VERIFICATION_BANK_WITH_AMOUNT";
    public static final String COMMAND_REMOVE_A_BANK = "COMMAND_REMOVE_A_BANK";
    public static final String COMMAND_GET_PROFILE_PICTURE_URL = "COMMAND_GET_PROFILE_PICTURE_URL";
    public static final String COMMAND_DOWNLOAD_PROFILE_PICTURE = "COMMAND_DOWNLOAD_PROFILE_PICTURE";
    public static final String COMMAND_TOPUP_REQUEST = "COMMAND_TOPUP_REQUEST";
    public static final String COMMAND_SET_PIN = "COMMAND_SET_PIN";
    public static final String COMMAND_CHANGE_PASSWORD = "COMMAND_CHANGE_PASSWORD";
    public static final String COMMAND_GET_PROFILE_INFO_REQUEST = "COMMAND_GET_PROFILE_INFO_REQUEST";
    public static final String COMMAND_GET_OCCUPATIONS_REQUEST = "COMMAND_GET_OCCUPATIONS_REQUEST";
    public static final String COMMAND_GET_USER_ADDRESS_REQUEST = "COMMAND_GET_USER_ADDRESS_REQUEST";
    public static final String COMMAND_GET_IDENTIFICATION_DOCUMENTS_REQUEST = "COMMAND_GET_IDENTIFICATION_DOCUMENTS_REQUEST";
    public static final String COMMAND_GET_INTRODUCER_LIST = "COMMAND_GET_INTRODUCER_LIST";
    public static final String COMMAND_GET_INTRODUCED_LIST = "COMMAND_GET_INTRODUCED_LIST";
    public static final String COMMAND_GET_SENT_REQUEST_LIST = "COMMAND_GET_SENT_REQUEST_LIST";
    public static final String COMMAND_EMAIL_VERIFICATION_REQUEST = "COMMAND_EMAIL_VERIFICATION_REQUEST";
    public static final String COMMAND_SET_PROFILE_INFO_REQUEST = "COMMAND_SET_PROFILE_INFO_REQUEST";
    public static final String COMMAND_SET_USER_ADDRESS_REQUEST = "COMMAND_SET_USER_ADDRESS_REQUEST";
    public static final String COMMAND_GET_BANK_LIST = "COMMAND_GET_BANK_LIST";
    public static final String COMMAND_GET_RECOMMENDATION_REQUESTS = "COMMAND_GET_RECOMMENDATION_REQUESTS";
    public static final String COMMAND_ADD_MONEY = "COMMAND_ADD_MONEY";
    public static final String COMMAND_RECOMMEND_ACTION = "COMMAND_RECOMMEND_ACTION";
    public static final String COMMAND_INVITEE_ACTION = "COMMAND_INVITEE_ACTION";
    public static final String COMMAND_GET_USER_INFO = "COMMAND_GET_USER_INFO";
    public static final String COMMAND_GET_NEWS_FEED = "COMMAND_GET_NEWS_FEED";
    public static final String COMMAND_WITHDRAW_MONEY = "COMMAND_WITHDRAW_MONEY";
    public static final String COMMAND_EVENT_CATEGORIES = "COMMAND_EVENT_CATEGORIES";
    public static final String COMMAND_DOWNLOAD_PROFILE_PICTURE_FRIEND = "COMMAND_DOWNLOAD_PROFILE_PICTURE_FRIEND";
    public static final String COMMAND_UPLOAD_DOCUMENT = "COMMAND_UPLOAD_DOCUMENT";
    public static final String COMMAND_GET_DOCUMENT_ACCESS_TOKEN = "COMMAND_GET_DOCUMENT_ACCESS_TOKEN";
    public static final String COMMAND_UPLOAD_NATIONAL_ID = "COMMAND_UPLOAD_NATIONAL_ID";
    public static final String COMMAND_UPLOAD_PASSPORT = "COMMAND_UPLOAD_PASSPORT";
    public static final String COMMAND_UPLOAD_DRIVING_LICENSE = "COMMAND_UPLOAD_DRIVING_LICENSE";
    public static final String COMMAND_UPLOAD_BIRTH_CERTIFICATE = "COMMAND_UPLOAD_BIRTH_CERTIFICATE";
    public static final String COMMAND_UPLOAD_TIN = "COMMAND_UPLOAD_TIN";
    public static final String COMMAND_GET_PIN_INFO = "COMMAND_GET_PIN_INFO";
    public static final String COMMAND_GET_MONEY_REQUESTS = "COMMAND_GET_MONEY_REQUESTS";
    public static final String COMMAND_GET_PROFILE_COMPLETION_STATUS = "COMMAND_GET_PROFILE_COMPLETION_STATUS";
    public static final String COMMAND_GET_FRIENDS = "COMMAND_GET_FRIENDS";
    public static final String COMMAND_ADD_FRIENDS = "COMMAND_ADD_FRIENDS";
    public static final String COMMAND_UPDATE_FRIENDS = "COMMAND_UPDATE_FRIENDS";
    public static final String COMMAND_DELETE_FRIENDS = "COMMAND_DELETE_FRIENDS";


    // Resource
    public static final String COMMAND_GET_AVAILABLE_BANK_LIST = "COMMAND_GET_AVAILABLE_BANK_LIST";
    public static final String COMMAND_GET_BUSINESS_TYPE_LIST = "COMMAND_GET_BUSINESS_TYPE_LIST";
    public static final String COMMAND_GET_THANA_LIST = "COMMAND_GET_THANA_LIST";
    public static final String COMMAND_GET_DISTRICT_LIST = "COMMAND_GET_DISTRICT_LIST";
    public static final String COMMAND_GET_BANK_BRANCH_LIST = "COMMAND_GET_BANK_BRANCH_LIST";

    // Invite
    public static final String COMMAND_GET_INVITE_INFO = "COMMAND_GET_INVITE_INFO";
    public static final String COMMAND_SEND_INVITE = "COMMAND_SEND_INVITE";

    // Email
    public static final String COMMAND_GET_EMAILS = "COMMAND_GET_EMAILS";
    public static final String COMMAND_ADD_NEW_EMAIL = "COMMAND_ADD_NEW_EMAIL";
    public static final String COMMAND_EMAIL_VERIFICATION = "COMMAND_EMAIL_VERIFICATION";
    public static final String COMMAND_EMAIL_MAKE_PRIMARY = "COMMAND_EMAIL_MAKE_PRIMARY";
    public static final String COMMAND_DELETE_EMAIL = "COMMAND_DELETE_EMAIL";

    // Trusted Network
    public static final String COMMAND_GET_TRUSTED_PERSONS = "COMMAND_GET_TRUSTED_PERSONS";
    public static final String COMMAND_ADD_TRUSTED_PERSON = "COMMAND_ADD_TRUSTED_PERSON";
    public static final String COMMAND_SET_ACCOUNT_RECOVERY_PERSON = "COMMAND_SET_ACCOUNT_RECOVERY_PERSON";

    public static final String COUNTRY_CODE_BANGLADESH = "+880";
    public static final int ACTIVITY_LOG_COUNT = 10;

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

    public static final String EMAIL_VERIFICATION_STATUS_VERIFIED = "VERIFIED";
    public static final String EMAIL_VERIFICATION_STATUS_NOT_VERIFIED = "NOT_VERIFIED";
    public static final String EMAIL_VERIFICATION_STATUS_VERIFICATION_IN_PROGRESS = "IN_PROGRESS";

    public static final int EVENT_STATUS_ACTIVE = 1;
    public static final int EVENT_STATUS_INACTIVE = 2;

    public static final String INTRODUCTION_REQUEST_STATUS_PENDING = "PENDING";
    public static final String INTRODUCTION_REQUEST_STATUS_REJECTED = "REJECTED";
    public static final String INTRODUCTION_REQUEST_STATUS_APPROVED = "APPROVED";
    public static final String INTRODUCTION_REQUEST_STATUS_SPAM = "MARKED_SPAM";

    public static final String INTRODUCTION_REQUEST_ACTION_APPROVE = "approve";
    public static final String INTRODUCTION_REQUEST_ACTION_REJECT = "reject";
    public static final String INTRODUCTION_REQUEST_ACTION_MARK_AS_SPAM = "mark-spam";

    public static final int EVENT_PARTICIPANT_TYPE_ANYONE = 0;
    public static final int EVENT_PARTICIPANT_TYPE_FROM_LIST = 3;

    public static final int TRANSACTION_HISTORY_OPENING_BALANCE = 1001;
    public static final int TRANSACTION_HISTORY_SEND_MONEY = 1;
    public static final int TRANSACTION_HISTORY_REQUEST_MONEY = 6001;
    public static final int TRANSACTION_HISTORY_ADD_MONEY = 3001;
    public static final int TRANSACTION_HISTORY_WITHDRAW_MONEY = 3002;
    public static final int TRANSACTION_HISTORY_TOP_UP = 2001;
    public static final int TRANSACTION_HISTORY_PAYMENT = 6002;
    public static final int TRANSACTION_HISTORY_EDUCATION = 8001;

    public static final int SERVICE_ID_REQUEST_MONEY = 6001;
    public static final int SERVICE_ID_REQUEST_INVOICE = 6003;
    public static final int SERVICE_ID_RECOMMENDATION_REQUEST = 1002;
    public static final int SERVICE_ID_OPENING_BALANCE = 1001;
    public static final int SERVICE_ID_SEND_MONEY = 1;
    public static final int SERVICE_ID_ADD_MONEY = 3001;
    public static final int SERVICE_ID_WITHDRAW_MONEY = 3002;
    public static final int SERVICE_ID_TOP_UP = 2001;
    public static final int SERVICE_ID_MAKE_PAYMENT = 6002;
    public static final int SERVICE_ID_EDUCATION = 8001;

    public static final String RESULT = "Result";
    public static final String POST_REQUEST = "POST_RESULT: ";
    public static final String GET_REQUEST = "GET_RESULT: ";
    public static final String GET_URL = "GET_URL: ";
    public static final String DELETE_URL = "DELETE_URL: ";
    public static final String DELETE_REQUEST = "GET_RESULT: ";

    public static final String PARSED_TOKEN = "Parsed Token: ";
    public static final String SUCCESS = "SUCCESS";
    public static final String FAIL = "FAIL";
    public static final String iPay_USER = "iPay_USER";

    public static final String DOCUMENT_TYPE_NATIONAL_ID = "national_id";
    public static final String DOCUMENT_TYPE_PASSPORT = "passport";
    public static final String DOCUMENT_TYPE_DRIVING_LICENSE = "driving_license";
    public static final String DOCUMENT_TYPE_BIRTH_CERTIFICATE = "birth_certificate";
    public static final String DOCUMENT_TYPE_TIN = "tin";

    public static final String DOCUMENT_ID_NUMBER = "documentIdNumber";
    public static final String DOCUMENT_TYPE = "documentType";

    public static final String ACCOUNT_VERIFICATION_STATUS_VERIFIED = "VERIFIED";
    public static final String ACCOUNT_VERIFICATION_STATUS_NOT_VERIFIED = "NOT_VERIFIED";

    public static final String ADDRESS_TYPE_PRESENT = "PRESENT";
    public static final String ADDRESS_TYPE_PERMANENT = "PERMANENT";
    public static final String ADDRESS_TYPE_OFFICE = "OFFICE";

    public static final String PUSH_NOTIFICATION_TAG_PROFILE_PICTURE = "PROFILE_PICTURE";
    public static final String PUSH_NOTIFICATION_TAG_PROFILE_INFO_UPDATE = "PROFILE_INFO";
    public static final String PUSH_NOTIFICATION_TAG_IDENTIFICATION_DOCUMENT_UPDATE = "IDENTIFICATION_DOCUMENT";
    public static final String PUSH_NOTIFICATION_TAG_EMAIL_UPDATE = "EMAIL";
    public static final String PUSH_NOTIFICATION_TAG_BANK_UPDATE = "BANK";
    public static final String PUSH_NOTIFICATION_TAG_DEVICE_UPDATE = "DEVICE";
    public static final String PUSH_NOTIFICATION_TAG_TRUSTED_PERSON_UPDATE = "TRUSTED_PERSON";
    public static final String PUSH_NOTIFICATION_TAG_SEND_MONEY = "SEND_MONEY";
    public static final String PUSH_NOTIFICATION_TAG_REQUEST_MONEY = "REQUEST_MONEY";
    public static final String PUSH_NOTIFICATION_TAG_TRANSACTION_HISTORY = "TRANSACTION_HISTORY";
    public static final String PUSH_NOTIFICATION_TOKEN = "token";
    public static final String PUSH_NOTIFICATION_TAG = "tag";
    public static final String PUSH_NOTIFICATION_EVENT = "EVENT";
    public static final String PUSH_NOTIFICATION_BODY = "notification";

    public static final String TOTAL = "total";
    public static final String INVOICE_RECEIVER_TAG = "receiver";
    public static final String INVOICE_ITEM_NAME_TAG = "item_name";
    public static final String INVOICE_DESCRIPTION_TAG = "description";
    public static final String INVOICE_QUANTITY_TAG = "quantity";
    public static final String INVOICE_RATE_TAG = "rate";
    public static final String INVOICE_TITLE_TAG = "title";


    public static final int INVOICE_STATUS_ACCEPTED = 200;
    public static final int INVOICE_STATUS_PROCESSING = 102;
    public static final int INVOICE_STATUS_CANCELED = 2;
    public static final int INVOICE_STATUS_REJECTED = 3;
    public static final int INVOICE_STATUS_DRAFT = 4;

    public static final int TRANSACTION_STATUS_ACCEPTED = 200;
    public static final int TRANSACTION_STATUS_PROCESSING = 102;
}
