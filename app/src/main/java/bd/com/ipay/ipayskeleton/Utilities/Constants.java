package bd.com.ipay.ipayskeleton.Utilities;

public class Constants {
    public static final String ApplicationTag = "iPay";
    public static final String ApplicationPackage = "bd.com.ipay.ipayskeleton";
    public static final String PICTURE_FOLDER = "/iPay";
    public static final String GENDER_MALE = "M";
    public static final String GENDER_FEMALE = "F";
    public static final String TOKEN = "token";
    public static final String REFRESH_TOKEN = "refresh-token";
    public static final String EMAIL = "EMAIL";
    public static final String FIRST_LAUNCH = "FIRST_LAUNCH";
    public static final String USERID = "USERID";
    public static final String UUID = "UUID";
    public static final String USERCOUNTRY = "USERCOUNTRY";
    public static final String LOGGEDIN = "LOGGEDIN";
    public static final String PASSWORD = "PASSWORD";
    public static final String NAME = "NAME";
    public static final String PROFILE_PICTURE = "PROFILE_PICTURE";
    public static final String MOBILE_NUMBER = "MOBILE_NUMBER";
    public static final String BIRTHDAY = "BIRTHDAY";
    public static final String GENDER = "GENDER";
    public static final String ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public static final String BOUNDARY = "iPayBoundary";
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


    public static final String AMOUNT = "amount";
    public static final String BANK_NAME = "bank_name";
    public static final String BANK_ACCOUNT_NUMBER = "bank_account_number";
    public static final String BANK_ACCOUNT_ID = "bank_account_id";
    public static final String DESCRIPTION = "description";
    public static final String TITLE = "title";
    public static final String RECEIVER = "receiver";
    public static final String SERVICE_CHARGE = "service_charge";
    public static final String PHOTO_URI = "photo_uri";

    public static final int STARTING_DATE = 01;
    public static final int STARTING_MONTH = 01;
    public static final int STARTING_YEAR = 2016;
    public static final int DEFAULT_USER_CLASS = 1;

    // Member Management (MM) Module
    // For POST Requests
    public static final String BASE_URL;

    // For Images
    public static final String BASE_URL_IMAGE_SERVER;

    // Server Type 1 -> dev server
    // Server Type 2 -> staging server
    // Server Type 3 -> live server
    // Server Type 4 -> local server
    public static final int SERVER_TYPE = 1;
    public static final boolean DEBUG = true;
    public static final boolean DEBUG_TOKEN_ENABLED = false;
    public static final int ACCOUNT_ID = 291;

    static {
        if (SERVER_TYPE == 1) {

            BASE_URL = "http://10.10.10.10:8085/api/v1/";
            BASE_URL_IMAGE_SERVER = "https://10.10.10.10";

        } else if (SERVER_TYPE == 2) {

            BASE_URL = "http://stage.ipay.com.bd:8085/api/v1/";
            BASE_URL_IMAGE_SERVER = "https://stage.ipay.com.bd";

        } else if (SERVER_TYPE == 3) {

            BASE_URL = "https://www.ipay.com.bd/api/v1/";
            BASE_URL_IMAGE_SERVER = "https://www.ipay.com.bd";

        } else {

            BASE_URL = "http://192.168.1.105:8085/api/v1/";
            BASE_URL_IMAGE_SERVER = "http://10.10.10.10";
        }
    }

    public static final String URL_SIGN_UP = "signup/activation";
    public static final String URL_SIGN_UP_BUSINESS = "signup/business/activation";
    public static final String URL_OTP_REQUEST = "signup";
    public static final String URL_OTP_REQUEST_BUSINESS = "signup/business";
    public static final String URL_LOGIN = "signin";
    public static final String URL_ASK_FOR_RECOMMENDATION = "kyc/verificationRequest";
    public static final String URL_GET_RECOMMENDATION_REQUESTS = "kyc/verificationRequestList";
    public static final String URL_RECOMMEND_ACTION = "kyc/verifyRequest";
    public static final String URL_SEND_OTP_FORGET_PASSWORD = "settings/password/forget";
    public static final String URL_ADD_TRUSTED_DEVICE = "settings/device/add";
    public static final String URL_GET_TRUSTED_DEVICES = "settings/devices";
    public static final String URL_REMOVE_TRUSTED_DEVICE = "settings/device/remove";
    public static final String URL_CONFIRM_OTP_FORGET_PASSWORD = "settings/password/forget/confirmation";
    public static final String URL_SEND_MONEY = "transaction/send";
    public static final String URL_SEND_MONEY_QUERY = "transaction/send/query";
    public static final String URL_REFRESH_BALANCE = "user/balance";
    public static final String URL_GET_USER_INFO = "user/userinfo";
    public static final String URL_RESOURCE = "resource";
    public static final String URL_GET_NEWS_FEED = "news";
    public static final String URL_TOPUP_REQUEST = "topup/dotopup";
    public static final String URL_EMAIL_VERIFICATION = "settings/email/verification";
    public static final String URL_SET_PIN = "settings/pin/change";
    public static final String URL_CHANGE_PASSWORD = "settings/password/change";
    public static final String URL_GET_PROFILE_INFO_REQUEST = "user/profile";
    public static final String URL_GET_USER_ADDRESS_REQUEST = "user/profile/address";
    public static final String URL_GET_DOCUMENTS = "user/identification/documents";
    public static final String URL_GET_INTRODUCER_LIST = "kyc/introducerList";
    public static final String URL_UPLOAD_DOCUMENTS = "/user/identification/documents";
    public static final String URL_SET_PROFILE_INFO_REQUEST = "user/profile";
    public static final String URL_SET_USER_ADDRESS_REQUEST = "user/profile/address";
    public static final String URL_LOG_OUT = "signout";
    public static final String URL_USER_ACTIVITY = "report/activities";
    public static final String URL_TRANSACTION_HISTORY = "report/transactions";
    public static final String URL_GET_NOTIFICATIONS = "requests/received";
    public static final String URL_GET_SENT_REQUESTS = "requests/sent";
    public static final String URL_ACCEPT_NOTIFICATION_REQUEST = "requests/accept";
    public static final String URL_REJECT_NOTIFICATION_REQUEST = "requests/cancel";
    public static final String URL_REQUEST_MONEY = "requestmoney";
    public static final String URL_PAYMENT_CREATE_INVOICE = "payment/invoice";
    public static final String URL_SET_PROFILE_PICTURE = "user/profile/profilepicture/set";
    public static final String URL_ADD_A_BANK = "bank/add";
    public static final String URL_SEND_FOR_VERIFICATION_BANK = "bank-verify";
    public static final String URL_BANK_VERIFICATION_WITH_AMOUNT = "bank-verify/check";
    public static final String URL_REMOVE_A_BANK = "bank/remove";
    public static final String URL_DISABLE_A_BANK = "bank/disable";
    public static final String URL_ENABLE_A_BANK = "bank/enable";
    public static final String URL_GET_BANK = "bank/get";
    public static final String URL_ADD_MONEY = "banktransaction/cashin";
    public static final String URL_WITHDRAW_MONEY = "banktransaction/cashout";
    public static final String URL_EVENT_LIST = "events/user/eventList/";
    public static final String URL_EVENT_CATEGORIES = "categories";
    public static final String URL_GET_ALL_PARTICIPANTS_LIST = "banktransaction/cashout";  // TODO: change
    public static final String URL_GET_INVITE_INFO = "settings/invitations";
    public static final String URL_SEND_INVITE = "settings/invitations";
    public static final String URL_GET_REFRESH_TOKEN = "signin/refreshToken";
    public static final String URL_GET_FIREBASE_TOKEN = "friend/firebasetoken";
    public static final String URL_UPDATE_FIREBASE_FRIEND_LIST = "friend/updatefriendlist";
    public static final String URL_SERVICE_CHARGE = "feecharge";
    public static final String URL_GET_PIN_INFO = "settings/pin";

    public static final String HTTP_RESPONSE_STATUS_NOT_ACCEPTABLE = "406";
    public static final String HTTP_RESPONSE_STATUS_NOT_FOUND = "404";
    public static final String HTTP_RESPONSE_STATUS_OK = "200";
    public static final String HTTP_RESPONSE_STATUS_PROCESSING = "102";
    public static final String HTTP_RESPONSE_STATUS_UNAUTHORIZED = "401";
    public static final String HTTP_RESPONSE_STATUS_BAD_REQUEST = "400";
    public static final String HTTP_RESPONSE_STATUS_ACCEPTED = "202";

    public static final int PERSONAL_ACCOUNT_TYPE = 1;
    public static final int BUSINESS_ACCOUNT_TYPE = 2;
    public static final String BANK_ACCOUNT_STATUS_VERIFIED = "VERIFIED";
    public static final String BANK_ACCOUNT_STATUS_PENDING = "PENDING";
    public static final String BANK_ACCOUNT_STATUS_NOT_VERIFIED = "NOT_VERIFIED";
    public static final int BANK_ACCOUNT_STATUS_ACTIVE = 0;
    public static final int BANK_ACCOUNT_STATUS_INACTIVE = 1;
    public static final int BANK_ACCOUNT_STATUS_DELETED = 2;

    public static final int MOBILE_TYPE_PREPAID = 1;
    public static final int MOBILE_TYPE_POSTPAID = 2;

    public static final String COMMAND_OTP_VERIFICATION = "COMMAND_OTP_VERIFICATION";
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
    public static final String COMMAND_CREATE_INVOICE = "COMMAND_CREATE_INVOICE";
    public static final String COMMAND_SET_PROFILE_PICTURE = "COMMAND_SET_PROFILE_PICTURE";
    public static final String COMMAND_ADD_A_BANK = "COMMAND_ADD_A_BANK";
    public static final String COMMAND_SEND_FOR_VERIFICATION_BANK = "COMMAND_SEND_FOR_VERIFICATION_BANK";
    public static final String COMMAND_VERIFICATION_BANK_WITH_AMOUNT = "COMMAND_VERIFICATION_BANK_WITH_AMOUNT";
    public static final String COMMAND_REMOVE_A_BANK = "COMMAND_REMOVE_A_BANK";
    public static final String COMMAND_DISABLE_A_BANK = "COMMAND_DISABLE_A_BANK";
    public static final String COMMAND_ENABLE_A_BANK = "COMMAND_ENABLE_A_BANK";
    public static final String COMMAND_GET_PROFILE_PICTURE_URL = "COMMAND_GET_PROFILE_PICTURE_URL";
    public static final String COMMAND_DOWNLOAD_PROFILE_PICTURE = "COMMAND_DOWNLOAD_PROFILE_PICTURE";
    public static final String COMMAND_TOPUP_REQUEST = "COMMAND_TOPUP_REQUEST";
    public static final String COMMAND_SET_PIN = "COMMAND_SET_PIN";
    public static final String COMMAND_CHANGE_PASSWORD = "COMMAND_CHANGE_PASSWORD";
    public static final String COMMAND_GET_PROFILE_INFO_REQUEST = "COMMAND_GET_PROFILE_INFO_REQUEST";
    public static final String COMMAND_GET_USER_ADDRESS_REQUEST = "COMMAND_GET_USER_ADDRESS_REQUEST";
    public static final String COMMAND_GET_IDENTIFICATION_DOCUMENTS_REQUEST = "COMMAND_GET_IDENTIFICATION_DOCUMENTS_REQUEST";
    public static final String COMMAND_GET_INTRODUCER_LIST = "COMMAND_GET_INTRODUCER_LIST";
    public static final String COMMAND_EMAIL_VERIFICATION_REQUEST = "COMMAND_EMAIL_VERIFICATION_REQUEST";
    public static final String COMMAND_SET_PROFILE_INFO_REQUEST = "COMMAND_SET_PROFILE_INFO_REQUEST";
    public static final String COMMAND_SET_USER_ADDRESS_REQUEST = "COMMAND_SET_USER_ADDRESS_REQUEST";
    public static final String COMMAND_GET_BANK_LIST = "COMMAND_GET_BANK_LIST";
    public static final String COMMAND_GET_RECOMMENDATION_REQUESTS = "COMMAND_GET_RECOMMENDATION_REQUESTS";
    public static final String COMMAND_ADD_MONEY = "COMMAND_ADD_MONEY";
    public static final String COMMAND_RECOMMEND_ACTION = "COMMAND_RECOMMEND_ACTION";
    public static final String COMMAND_GET_USER_INFO = "COMMAND_GET_USER_INFO";
    public static final String COMMAND_GET_FIREBASE_TOKEN = "COMMAND_GET_FIREBASE_TOKEN";
    public static final String COMMAND_GET_NEWS_FEED = "COMMAND_GET_NEWS_FEED";
    public static final String COMMAND_WITHDRAW_MONEY = "COMMAND_WITHDRAW_MONEY";
    public static final String COMMAND_EVENT_CATEGORIES = "COMMAND_EVENT_CATEGORIES";
    public static final String COMMAND_DOWNLOAD_PROFILE_PICTURE_FRIEND = "COMMAND_DOWNLOAD_PROFILE_PICTURE_FRIEND";
    public static final String COMMAND_UPLOAD_NATIONAL_ID = "COMMAND_UPLOAD_NATIONAL_ID";
    public static final String COMMAND_UPLOAD_PASSPORT = "COMMAND_UPLOAD_PASSPORT";
    public static final String COMMAND_UPLOAD_DRIVING_LICENSE = "COMMAND_UPLOAD_DRIVING_LICENSE";
    public static final String COMMAND_UPLOAD_BIRTH_CERTIFICATE = "COMMAND_UPLOAD_BIRTH_CERTIFICATE";
    public static final String COMMAND_UPLOAD_TIN = "COMMAND_UPLOAD_TIN";
    public static final String COMMAND_GET_PIN_INFO = "COMMAND_GET_PIN_INFO";


    // Resource
    public static final String COMMAND_GET_AVAILABLE_BANK_LIST = "COMMAND_GET_AVAILABLE_BANK_LIST";
    public static final String COMMAND_GET_BUSINESS_TYPE_LIST = "COMMAND_GET_BUSINESS_TYPE_LIST";
    public static final String COMMAND_GET_THANA_LIST = "COMMAND_GET_THANA_LIST";
    public static final String COMMAND_GET_DISTRICT_LIST = "COMMAND_GET_DISTRICT_LIST";
    public static final String COMMAND_GET_BANK_BRANCH_LIST = "COMMAND_GET_BANK_BRANCH_LIST";

    // Invite
    public static final String COMMAND_GET_INVITE_INFO = "COMMAND_GET_INVITE_INFO";
    public static final String COMMAND_SEND_INVITE = "COMMAND_SEND_INVITE";

    // FireBase
    public static final String PATH_TO_FIREBASE_DATABASE;

    static {
        if (SERVER_TYPE == 2)
            PATH_TO_FIREBASE_DATABASE = "https://ipaybdstage.firebaseio.com/";
        else if (SERVER_TYPE == 3)
            PATH_TO_FIREBASE_DATABASE = "https://ipaybd.firebaseio.com/";
        else
            PATH_TO_FIREBASE_DATABASE = "https://testingipay.firebaseio.com/";
    }

    public static final String FIREBASE_CONTACT_LIST = "ContactList";
    public static final String FIREBASE_DIRTY = "dirty";
    public static final String FIREBASE_SYNCED = "synced";

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

    public static final int EVENT_STATUS_ACTIVE = 1;
    public static final int EVENT_STATUS_INACTIVE = 2;

    public static final String RECOMMENDATION_STATUS_PENDING = "PENDING";
    public static final String RECOMMENDATION_STATUS_REJECTED = "REJECTED";
    public static final String RECOMMENDATION_STATUS_APPROVED = "APPROVED";
    public static final String RECOMMENDATION_STATUS_SPAM = "MARKED_SPAM";

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

    public static final String VERIFICATION_STATUS_VERIFIED = "VERIFIED";
    public static final String VERIFICATION_STATUS_NOT_VERIFIED = "NOT_VERIFIED";

}
