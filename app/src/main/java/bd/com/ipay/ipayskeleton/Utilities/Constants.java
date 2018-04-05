package bd.com.ipay.ipayskeleton.Utilities;

import android.Manifest;

import com.google.android.gms.vision.CameraSource;

import bd.com.ipay.ipayskeleton.BuildConfig;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Employee.GetBusinessInformationResponse;

public class Constants {
    public static final String ApplicationTag = "iPayV2";
    public static final String ApplicationPackage = "bd.com.ipay.android";

    public static final String ERROR = "ERROR";
    public static final String USER_AGENT = "User-Agent";
    public static final String USER_AGENT_MOBILE_ANDROID = "mobile-android";
    public static final String NEW_PASSWORD = "NEW_PASSWORD";
    public static final String TARGET_FRAGMENT = "TARGET_FRAGMENT";
    public static final String SIGN_IN = "SIGN_IN";
    public static final String SIGN_UP = "SIGN_UP";
    public static final String ADD_TRUSTED_PERSON = "ADD_TRUSTED_PERSON";
    public static final String BANK_ACCOUNT = "BANK_ACCOUNT";
    public static final String SIGNED_IN = "SIGNED IN";
    public static final String DOCUMENT_URL = "DOCUMENT_URL";
    public static final String FILE_EXTENSION = "FILE_EXTENSION";
    public static final String TICKET_ID = "TICKET_ID";
    public static final String STRING_TO_ENCODE = "STRING_TO_ENCODE";
    public static final String ACTIVITY_TITLE = "ACTIVITY_TITLE";
    public static final String TITLE = "TITLE";
    public static final String INTENDED_FRAGMENT = "INTENDED_FRAGMENT";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String TIME = "TIME";
    public static final String STATUS = "STATUS";
    public static final String TAG = "TAG";
    public static final String REQUEST = "REQUEST";
    public static final String RECOMMENDATION = "RECOMMENDATION";
    public static final String TRANSACTION_DETAILS = "TRANSACTION_DETAILS";
    public static final String MONEY_REQUEST_ID = "MONEY REQUESTS ID";
    public static final String MONEY_REQUEST_SERVICE_ID = "SERVICE_ID";
    public static final String TRANSACTION_ID = "TRANSACTION_ID";
    public static final String TOP_UP = "TOP_UP";
    public static final String SEND_MONEY = "SEND_MONEY";
    public static final String REQUEST_MONEY = "REQUEST_MONEY";
    public static final String REQUEST_PAYMENT = "REQUEST_PAYMENT";
    public static final String BUSINESS_ROLE_REQUEST = "BUSINESS_ROLE_REQUEST";

    public static final String VERIFIED_USERS_ONLY = "VERIFIED_USERS_ONLY";
    public static final String IPAY_MEMBERS_ONLY = "IPAY_MEMBERS_ONLY";
    public static final String BUSINESS_ACCOUNTS_ONLY = "BUSINESS_ACCOUNTS_ONLY";
    public static final String SHOW_INVITED_ONLY = "SHOW_INVITED_ONLY";
    public static final String SHOW_NON_INVITED_NON_MEMBERS_ONLY = "SHOW_NON_INVITED_NON_MEMBERS_ONLY";
    public static final String SHOW_ALL_MEMBERS = "SHOW_ALL_MEMBERS";

    public static final String SMS_READER_BROADCAST_RECEIVER_PDUS = "pdus";
    public static final String PROFILE_INFO_UPDATE_BROADCAST = "PROFILE_INFO_UPDATE_BROADCAST";
    public static final String PROFILE_PICTURE_UPDATE_BROADCAST = "PROFILE_PICTURE_UPDATE_BROADCAST";
    public static final String COMPLETED_TRANSACTION_HISTORY_UPDATE_BROADCAST = "COMPLETED_TRANSACTION_HISTORY_UPDATE_BROADCAST ";
    public static final String PENDING_TRANSACTION_HISTORY_UPDATE_BROADCAST = "PENDING_TRANSACTION_HISTORY_UPDATE_BROADCAST ";
    public static final String BALANCE_UPDATE_BROADCAST = "BALANCE_UPDATE_BROADCAST";
    public static final String PROFILE_COMPLETION_UPDATE_BROADCAST = "PROFILE_COMPLETION_UPDATE_BROADCAST";
    public static final String NOTIFICATION_UPDATE_BROADCAST = "NOTIFICATION_UPDATE_BROADCAST";

    public static final String TOPUP_HISTORY_UPDATE_BROADCAST = "TOPUP_HISTORY_UPDATE_BROADCAST ";


    public static final String TOKEN = "token";
    public static final String REFRESH_TOKEN = "refresh-token";
    public static final String RESOURCE_TOKEN = "resource-token";
    public static final String OPERATING_ON_ACCOUNT_ID = "onAccountId";

    public static final int CAMERA_REAR = CameraSource.CAMERA_FACING_BACK;
    public static final int CAMERA_FRONT = CameraSource.CAMERA_FACING_FRONT;

    public static final String REQUEST_ID = "REQUEST_ID";
    public static final String QUESTION_ID = "QUESTION_ID";
    public static final String PREVIOUS_QUESTION = "PREVIOUS_QUESTIONS";
    public static final String All_QUESTIONS = "ALL_QUESTIONS";
    public static final String PROFILE_PICTURE = "_PROFILE_PICTURE";
    public static final String MOBILE_NUMBER = "MOBILE_NUMBER";
    public static final String MOBILE_NUMBER_TYPE = "mobile_number_type";
    public static final String DATE_OF_BIRTH = "DATE_OF_BIRTH";
    public static final String GENDER = "GENDER";
    public static final String PRIMARY_EMAIL = "PRIMARY_EMAIL";
    public static final String ACCOUNT_ID = "ACCOUNT_ID";
    public static final String SIGNUP_TIME = "SIGNUP_TIME";
    public static final String BOUNDARY = "iPayBoundary";
    public static final String ANDROID = "Android";
    public static final String IS_IN_CONTACTS = "is-in-contacts";
    public static final String SWITCHED_FROM_BANK_VERIFICATION = "switched_from_bank_validation";
    public static final String IS_STARTED_FROM_PROFILE_COMPLETION = "is_started_from_profile_completion";
    public static final String SWITCHED_FROM_TRANSACTION_HISTORY = "switched_from_transaction_history";
    public static final String MOBILE_ANDROID = "mobile-android-";
    public static final String MULTIPART_FORM_DATA_NAME = "file";
    public static final String OPERATOR_CODE = "operator_code";
    public static final String COUNTRY_CODE = "country_code";
    public static final String EXPAND_PIN = "expand_pin";
    public static final String DEVICE_IS_NOT_TRUSTED = "Device is not trusted";
    public static final String USERNAME_PASSWORD_INCORRECT = "The provided username or password is incorrect";
    public static final String DOCUMENT_ID = "DOCUMENT_ID";
    public static final String DOCUMENT_TYPE_NAME = "DOCUMENT_TYPE_NAME";
    public static final String MESSAGE = "message";

    public static final String NAME = "NAME";
    public static final String ACCEPTED = "ACCEPTED";
    public static final String REJECTED = "REJECTED";
    public static final String CANCELED = "CANCELED";


    public static final String OCCUPATION = "OCCUPATION";
    public static final String OCCUPATION_LIST = "OCCUPATION_LIST";
    public static final String ORGANIZATION_NAME = "ORGANIZATION_NAME";
    public static final String BUSINESS_NAME = "BUSINESS_NAME";

    public static final String BUSINESS_MOBILE_NUMBER = "BUSINESS_MOBILE_NUMBER";
    public static final String BUSINESS_TYPE = "BUSINESS_TYPE";
    public static final String BUSINESS_TYPE_LIST = "BUSINESS_TYPE_LIST";
    public static final String FATHERS_NAME = "FATHERS_NAME";

    public static final String MOTHERS_NAME = "MOTHERS_NAME";
    public static final String FATHERS_MOBILE = "FATHERS_MOBILE";
    public static final String MOTHERS_MOBILE = "MOTHERS_MOBILE";

    public static final String ADDRESS = "ADDRESS";
    public static final String RELOAD = "RELOAD";
    public static final String FROM_ON_BOARD = "FROM_ON_BOARD";
    public static final String COMMAND_ADD_CARD = "COMMAND_ADD_CARD";
    public static final String VERIFIED = "VERIFIED";
    public static final String DISTRICT = "DISTRICT";
    public static final String COUNTRY = "COUNTRY";
    public static final String FROM_QR_SCAN = "FROM_QR_SCAN";
    public static final String LATITUDE = "LATITUDE";
    public static final String LONGITUDE = "LONGITUDE";
    public static final String COMMAND_CHECK_VERSION = "COMMAND_CHECK_VERSION";
    public static final String THANA = "THANA";
    public static final String ID = "ID";
    public static final String BUSINESS_ACCOUNT_ID = "BUSINESS_ACCOUNT_ID";
    public static String ROLEID = "ROLEID";

    public static final String ADDRESS_TYPE = "ADDRESS_TYPE";
    public static final String PRESENT_ADDRESS = "PRESENT_ADDRESS";
    public static final String REQUEST_TYPE = "request_type";

    public static final String AMOUNT = "amount";
    public static final String SELECTED_BANK_ACCOUNT = "selectedBankAccount";
    public static final String BANK_NAME = "bank_name";
    public static final String BANK_BRANCH = "bank_branch";
    public static final String BANK_ACCOUNT_NUMBER = "bank_account_number";
    public static final String BANK_ACCOUNT_ID = "bank_account_id";
    public static final String BANK_CODE = "bank_code";
    public static final String PHOTO_URI = "photo_uri";
    public static final String VAT = "vat";

    public static final String OFFICE_LAND_LINE_NUMBER_PRIMARY = "+8809638900801";
    public static final String OFFICE_EMAIL = "support@ipay.com.bd";
    public static final String FEEDBACK = "feedback@ipay.com.bd";
    public static final String OFFICE_ADDRESS = "Silver Tower (12th Floor)\n52 Gulshan Avenue, Circle-1\nDhaka-1212\nBangladesh\n";
    public static final double OFFICE_LATITUDE = 23.7810729;
    public static final double OFFICE_LONGITUDE = 90.4169212;
    public static final String HOST_NAME = "www.ipay.com.bd";

    public static final int MIN_AGE_LIMIT = 14;
    public static final int MIN_VALID_NAME_LENGTH = 5;
    public static final int MAX_FILE_ATTACHMENT_LIMIT = 5;
    public static final int MAX_FILE_MB_SIZE = 3;
    public static final int MAX_FILE_BYTE_SIZE = 3145728;
    public static final int MINIMUM_REQUIRED_NID_LENGTH = 10;
    public static final int MAXIMUM_REQUIRED_NID_LENGTH = 17;
    public static final int BUSINESS_TIN_LENGTH = 12;
    public static final int TRADE_LICENSE_ID_LENGTH = 8;
    public static final int VAT_REG_CERT_ID_LENGTH = 11;

    public static final int STARTING_DATE = 01;
    public static final int SERVER_TYPE_LIVE = 4;

    public static final int STARTING_MONTH = 01;
    public static final int STARTING_YEAR = 2016;
    public static final int DEFAULT_USER_CLASS = 1;
    public static final int BUSINESS_ID_DEFAULT = -1;

    public static final int PHOTO_ID_FILE_MAX_SIZE = 5;

    public static final String IS_FINGERPRINT_AUTHENTICATION_ON = "LOGIN_WITH_FINGERPRINT";
    public static final String KEY_NAME = "key_name";
    /**
     * All requests and responses to server, as well as token is printed when debug flag is enabled.
     * Besides, for safety measures, all later flags won't work unless DEBUG flag is set.
     */
    public static final boolean DEBUG = BuildConfig.DebugOnOffStatus;

    /**
     * If set to true (with DEBUG flag also being set to true),
     * it works like the "Remember me" function. Mobile number and password will be
     * required only for the first time when user tries to login. After that, login request will be
     * automatically sent to the server using the previously used mobile number and default password
     * (qqqqqqq1).
     * <p>
     * *** Set it to false if you are not using the default password ***
     */
    public static final boolean AUTO_LOGIN = BuildConfig.AutoLogin;

    // Server Type 1 -> dev server
    // Server Type 2 -> test server
    // Server Type 3 -> stage server
    // Server Type 4 -> live server
    // Server Type 5 -> local server
    public static final int SERVER_TYPE = BuildConfig.ServerType;

    public static final String BASE_URL_MM;
    public static final String BASE_URL_SM;
    public static final String BASE_URL_CARD;
    public static final String BASE_URL_EDU;
    public static final String BASE_URL_CONTACT;
    public static final String BASE_URL_ADMIN;
    public static final String BASE_URL_PUSH_NOTIFICATION;
    public static final String BASE_URL_FTP_SERVER;
    public static final String BASE_URL_WEB;
    public static final String BASE_URL_DATA_COLLECTOR;
    private static final String SERVER_NAME;
    public static final String BASE_URL_OFFER;

    public static final int DEV_SERVER = 1;
    public static final int TEST_SERVER = 2;
    public static final int STAGE_SERVER = 3;
    public static final int LIVE_SERVER = 4;
    public static final int DEMO_SERVER = 5;
    public static final int LOCAL_SERVER = 6;

    public static final String PERSONAL_ACCOUNT = "Personal Account";
    public static final String BUSINESS_ACCOUNT = "Business Account";
    public static final String BUSINESS = "Business";
    public static final String PRIVILEGES = "PRIVILEGES";
    public static final String SERVICE_ID_SET = "SERVICE_ID_SET";
    public static final String NAVIGATION_MENU_SERVICE_ACCESS_SET = "NAVIGATION_MENU_SERVICE_ACCESS_SET";
    public static final String FRAGMENT_SERVICE_ACCESS_SET = "FRAGMENT_SERVICE_ACCESS_SET";
    public static final String MOBILE_NUMBER_REGEX = "^(((\\+)?880)?|(0)?)(1[356789][\\d]{8})$";
    public static final String TWO_FACTOR_AUTH_SETTINGS = "TWO_FACTOR_AUTH_SETTINGS";
    public static final String SELECTED_IDENTIFICATION_DOCUMENT = "SELECTED_IDENTIFICATION_DOCUMENT";
    public static final String SELECTED_CHEQUEBOOK_COVER = "SELECTED_CHEQUEBOOK_COVER";
    public static final String IMAGE_VALIDATOR_REGEX = "([^\\\\s]+(\\\\.(?i)(jpg|png|gif|bmp))$)";

    public static final String ADD_MONEY_TYPE = "ADD_MONEY_TYPE";
    public static final String ADD_MONEY_TYPE_BY_CREDIT_OR_DEBIT_CARD = "ADD_MONEY_TYPE_BY_CREDIT_OR_DEBIT_CARD";
    public static final String ADD_MONEY_TYPE_BY_BANK = "ADD_MONEY_TYPE_BY_BANK";
    public static final String CARD_PAYMENT_URL = "CARD_PAYMENT_URL";
    public static final String ADD_MONEY_BY_CREDIT_OR_DEBIT_CARD_STATUS = "ADD_MONEY_BY_CREDIT_OR_DEBIT_CARD_STATUS";

    public static final String ADD_MONEY_BY_CREDIT_OR_DEBIT_CARD_TITLE = "Credit/Debit Card";
    public static final String ADD_MONEY_BY_BANK_TITLE = "Bank";
    public static final String CARD_TRANSACTION_DATA = "CARD_TRANSACTION_DATA";
    public static final String VALID_IPAY_BD_ADDRESS = "(http://|https://)?(www|dev|test|stage|internal).ipay.com.bd/(.+)";
    public static String Business_role = "BUSINESS_ROLE";


    static {
        if (SERVER_TYPE == DEV_SERVER) {

            BASE_URL_MM = "http://10.10.10.10:8085/api/v1/";
            BASE_URL_SM = "http://10.10.10.11:8085/api/v1/money/";
            BASE_URL_CARD = "http://10.10.10.11:5665/api/v1/card/";
            BASE_URL_EDU = "http://10.10.10.11:7150/api/v1/em/";
            BASE_URL_FTP_SERVER = "http://10.10.10.10";
            BASE_URL_CONTACT = "http://10.10.10.11:4000/external/v1/";
            BASE_URL_ADMIN = "http://10.10.10.199:8085/api/v1/support/";
            BASE_URL_PUSH_NOTIFICATION = "http://10.10.10.10:7778/api/v1/notification/";
            BASE_URL_WEB = "http://dev.ipay.com.bd";
            BASE_URL_DATA_COLLECTOR = "http://10.10.10.10:8800/data-collector/v1/";
            SERVER_NAME = "dev";
            BASE_URL_OFFER = "http://10.10.10.10:8070/offer_v2/api/v1/";

        } else if (SERVER_TYPE == TEST_SERVER) {

            BASE_URL_MM = "http://10.15.40.10:8085/api/v1/";
            BASE_URL_SM = "http://10.15.40.11:8085/api/v1/money/";
            BASE_URL_CARD = "http://10.15.40.11:2202/api/v1/card/";
            BASE_URL_EDU = "http://10.15.40.11:7150/api/v1/em/";
            BASE_URL_FTP_SERVER = "http://10.15.40.14";
            BASE_URL_CONTACT = "http://10.15.40.14:4000/external/v1/";
            BASE_URL_ADMIN = "http://10.15.40.15:8085/api/v1/support/";
            BASE_URL_PUSH_NOTIFICATION = "http://10.10.10.162:7778/api/v1/notification/";
            BASE_URL_WEB = "http://test.ipay.com.bd";
            BASE_URL_DATA_COLLECTOR = "http://10.15.40.10:8800/data-collector/v1/";
            SERVER_NAME = "test";
            BASE_URL_OFFER = "https://test.ipay.com.bd/offer_v2/api/v1/";

        } else if (SERVER_TYPE == STAGE_SERVER) {

            BASE_URL_MM = "http://10.10.40.10:8085/api/v1/";
            BASE_URL_SM = "http://10.10.40.11:8085/api/v1/money/";
            BASE_URL_CARD = "http://10.10.40.11:2202/api/v1/card/";
            BASE_URL_EDU = "http://10.10.40.11:7150/api/v1/em/";
            BASE_URL_FTP_SERVER = "http://10.10.40.14";
            BASE_URL_CONTACT = "http://10.10.40.14:4000/external/v1/";
            BASE_URL_ADMIN = "http://192.168.1.147:8085/api/v1/support/";
            BASE_URL_PUSH_NOTIFICATION = "http://10.10.10.10:7778/api/v1/notification/";
            BASE_URL_WEB = "http://stage.ipay.com.bd";
            BASE_URL_DATA_COLLECTOR = "http://10.10.40.10:8800/data-collector/v1/";
            SERVER_NAME = "stage";
            BASE_URL_OFFER = "http://stage.ipay.com.bd/offer_v2/api/v1/";

        } else if (SERVER_TYPE == LIVE_SERVER) {

            BASE_URL_MM = "https://www.ipay.com.bd/api/v1/";
            BASE_URL_SM = "https://www.ipay.com.bd/api/v1/money/";
            BASE_URL_CARD = "https://www.ipay.com.bd/api/v1/card/";
            BASE_URL_EDU = "https://www.ipay.com.bd/api/v1/em/";
            BASE_URL_FTP_SERVER = "https://www.ipay.com.bd";
            BASE_URL_CONTACT = "https://www.ipay.com.bd/cm/api/v1/";
            BASE_URL_ADMIN = "https://www.ipay.com.bd/api/v1/support/";
            BASE_URL_PUSH_NOTIFICATION = "https://www.ipay.com.bd/api/v1/notification/";
            BASE_URL_WEB = "https://www.ipay.com.bd";
            BASE_URL_DATA_COLLECTOR = "https://www.ipay.com.bd/data-collector/v1/";
            SERVER_NAME = "live";
            BASE_URL_OFFER = "https://www.ipay.com.bd/offer_v2/api/v1/";

        } else if (SERVER_TYPE == DEMO_SERVER) {

            BASE_URL_MM = "https://demo.ipay.com.bd/api/v1/";
            BASE_URL_SM = "https://demo.ipay.com.bd/api/v1/money/";
            BASE_URL_EDU = "https://demo.ipay.com.bd/api/v1/em/";
            BASE_URL_CARD = "https://demo.ipay.com.bd/api/v1/card/";
            BASE_URL_FTP_SERVER = "https://demo.ipay.com.bd";
            BASE_URL_CONTACT = "https://demo.ipay.com.bd/external/v1/";
            BASE_URL_ADMIN = "https://demo.ipay.com.bd/api/v1/support/";
            BASE_URL_PUSH_NOTIFICATION = "https://demo.ipay.com.bd/api/v1/notification/";
            BASE_URL_DATA_COLLECTOR = "https://demo.ipay.com.bd/data-collector/v1/";
            BASE_URL_WEB = "https://demo.ipay.com.bd";
            SERVER_NAME = "demo";
            BASE_URL_OFFER = "https://demo.ipay.com.bd/offer_v2/api/v1/";

        } else {

            BASE_URL_MM = "http://192.168.1.105:8085/api/v1/";
            BASE_URL_SM = "http://192.168.1.105:8085/api/v1/money/";
            BASE_URL_CARD = "http://192.168.1.215:2202/api/v1/card/";
            BASE_URL_EDU = "http://192.168.1.105:7150/api/v1/em/";
            BASE_URL_FTP_SERVER = "http://10.10.10.10";
            BASE_URL_CONTACT = "https://www.ipay.com.bd/cm/api/v1/";
            BASE_URL_ADMIN = "http://192.168.1.147:8085/api/v1/support/";
            BASE_URL_PUSH_NOTIFICATION = "http://10.10.10.10:7778/api/v1/notification/";
            BASE_URL_WEB = "http://dev.ipay.com.bd";
            BASE_URL_DATA_COLLECTOR = "http://192.168.1.105:8800/data-collector/v1/";
            SERVER_NAME = "local";
            BASE_URL_OFFER = "http://dev.ipay.com.bd/offer_v2/api/v1/";
        }
    }

    public static final String INTERCOM_API_KEY;
    public static final String INTERCOM_ANDROID_SDK_KEY;

    static {
        if (SERVER_TYPE == LIVE_SERVER) {
            INTERCOM_API_KEY = "rvba5ye6";
            INTERCOM_ANDROID_SDK_KEY = "android_sdk-75b180d622ad8a70eb7fd36a84534f9db277f402";
        } else {
            INTERCOM_API_KEY = "zlbinvht";
            INTERCOM_ANDROID_SDK_KEY = "android_sdk-534f3bfed2dbd05497dffe8d9a629a92e6a8583b";
        }
    }

    // Activity REST
    public static final String URL_USER_ACTIVITY = "/activity";

    // Bank Operation REST
    public static final String URL_ADD_A_BANK = "bank/";

    public static final String URL_GET_BANK = "bank/";
    public static final String URL_REMOVE_A_BANK = "bank/";
    public static final String URL_VERIFY_WITH_AMOUNT_A_BANK = "bank/";
    public static final String URL_GET_CARD = "user/card";
    // Bank Transaction REST
    public static final String URL_ADD_MONEY = "banktransaction/cashin/v2";
    public static final String URL_WITHDRAW_MONEY = "banktransaction/cashout/v2";

    // Bank Verify Rest
    public static final String URL_BANK_VERIFICATION_WITH_AMOUNT = "verify";

    // Check book covor upload Rest
    public static final String URL_CHECKBOOK_COVOR_UPLOAD = "bank/document";

    // Card Transaction REST
    public static final String URL_ADD_MONEY_CREDIT_OR_DEBIT_CARD = "add-money";

    // Trusted device CRUD operations
    public static final String URL_ADD_TRUSTED_DEVICE = "device";

    public static final String URL_GET_TRUSTED_DEVICES = "device";
    public static final String URL_REMOVE_TRUSTED_DEVICE = "device/";
    // Documents Rest
    public static final String URL_GET_DOCUMENTS_v2 = "docs/identification/documents/v2";
    public static final String URL_GET_BUSINESS_DOCUMENTS = "docs/identification/documents/business";
    public static final String URL_GET_BUSINESS_DOCUMENTS_v2 = "docs/identification/documents/business/v2";

    public static final String URL_UPLOAD_DOCUMENTS = "docs/identification/documents";
    public static final String URL_UPLOAD_DOCUMENTS_V2 = "docs/identification/documents/v2";
    public static final String URL_UPLOAD_BUSINESS_DOCUMENTS = "docs/identification/documents/business";
    public static final String URL_UPLOAD_BUSINESS_DOCUMENTS_V2 = "docs/identification/documents/business/v2";

    // Fee Charge REST
    public static final String URL_SERVICE_CHARGE = "feecharge";

    //business rule Rest
    public static final String URL_BUSINESS_RULE = "business-rule";
    public static final String URL_SWITCH_ACCOUNT = "business-manager/businesses/";

    // Introducer REST
    public static final String URL_ASK_FOR_INTRODUCTION = "/introducer/introduceme/";

    public static final String URL_GET_DOWNSTREAM_NOT_APPROVED_INTRODUCTION_REQUESTS = "introducer/downstream/notapproved";
    public static final String URL_GET_DOWNSTREAM_APPROVED_INTRODUCTION_REQUESTS = "introducer/downstream/approved";
    public static final String URL_GET_DETAILS_OF_INVITED_BUSINESS_ROLE = "business-manager-invitation/received/";

    public static final String URL_GET_UPSTREAM_NOT_APPROVED_INTRODUCTION_REQUESTS = "introducer/upstream/notapproved";
    public static final String URL_GET_UPSTREAM_APPROVED_INTRODUCTION_REQUESTS = "introducer/upstream/approved";
    public static final String URL_INTRODUCE_ACTION = "introducer/";
    public static final String URL_GET_PENDING_INTRODUCER = "introducer/pending";
    public static final String URL_PENDING_INTRODUCER_ACTION = "introducer/pending";

    // Invite Rest
    public static final String URL_GET_INVITE_INFO = "invitation";
    public static final String URL_SEND_INVITE = "invitation/invite/";


    // Mobile Topup Request REST
    public static final String URL_TOPUP_REQUEST = "topup/dotopup/v2";

    // Money Request REST
    public static final String URL_REQUEST_MONEY = "requestmoney";

    // News Feed REST
    public static final String URL_GET_NEWS_FEED = "resource/news";

    // Request Rest

    public static final String URL_GET_All_NOTIFICATIONS = "requests/received/all-list";
    public static final String URL_GET_NOTIFICATIONS = "requests/received";
    public static final String URL_GET_SENT_REQUESTS = "requests/sent";
    public static final String URL_ACCEPT_NOTIFICATION_REQUEST = "requests/accept/v2";
    public static final String URL_CANCEL_NOTIFICATION_REQUEST = "requests/cancel";
    public static final String URL_REJECT_NOTIFICATION_REQUEST = "requests/reject";

    // Settings REST
    public static final String URL_CHANGE_PASSWORD = "settings/password/v2";
    public static final String URL_GET_SECURITY_ALL_QUESTIONS = "settings/security/allquestions/";
    public static final String URL_GET_SECURITY_QUESTIONS = "settings/security/questions";
    public static final String URL_SET_SECURITY_ANSWERS = "settings/security/answers";
    public static final String URL_FORGET_PASSWORD = "/forgot-password";
    public static final String URL_TWO_FACTOR_AUTH_SETTINGS = "settings/2fa/preference";
    public static final String URL_GET_PIN_INFO = "settings/pin";
    public static final String URL_SET_PIN = "settings/pin/v2";

    // Sign in Rest
    public static final String URL_GET_REFRESH_TOKEN = "signin/refreshToken";
    public static final String URL_LOGIN = "signin/v2";

    // Sign out Rest
    public static final String URL_LOG_OUT = "signout";
    public static final String URL_LOG_OUT_from_all_device = "signout/formAllDevice";

    // Sign up Rest
    public static final String URL_SIGN_UP = "signup/activation/v2";
    public static final String URL_SIGN_UP_BUSINESS = "signup/business/activation/v2";
    public static final String URL_OTP_REQUEST = "signup/v2";
    public static final String URL_CHECK_IF_USER_EXISTS = "signup/check/";
    public static final String URL_OTP_REQUEST_BUSINESS = "signup/business/v2";
    public static final String URL_GET_ACCESS_CONTROL_LIST = "acl/services";

    // SM Payment REST
    public static final String URL_SEND_PAYMENT_REQUEST = "payment/invoice/send";
    public static final String URL_GET_SINGLE_REQUEST_PAYMENT = "payment/invoice/get/";


    public static final String URL_PAYMENT = "payment/v2";

    // SM Reports REST
    public static final String URL_TRANSACTION_HISTORY = "report/transactions";
    public static final String URL_TRANSACTION_HISTORY_SINGLE = "/ta/single-transaction";
    public static final String URL_TRANSACTION_HISTORY_COMPLETED = "ta/transaction-history";
    public static final String URL_TRANSACTION_HISTORY_PENDING = "ta/pending-transactions";

    // SM User Rest
    public static final String URL_REFRESH_BALANCE = "user/balance";

    // Static Resource REST
    public static final String URL_RESOURCE = "resource";

    // Transaction REST
    public static final String URL_SEND_MONEY = "transaction/send/v2";

    // Trusted Network REST
    public static final String URL_GET_TRUSTED_PERSONS = "trustednetwork/trustedpersons/";
    public static final String URL_POST_TRUSTED_PERSONS = "trustednetwork/trustedpersons/";
    public static final String URL_REMOVE_TRUSTED_PERSON = "trustednetwork/trustedpersons";

    // User Rest
    public static final String URL_GET_USER_INFO = "user/userinfo";
    public static final String URL_GET_PARENT_INFO_REQUEST = "user/parent";
    public static final String URL_SET_PARENT_INFO_REQUEST = "user/parent";
    public static final String URL_GET_PROFILE_INFO_REQUEST = "user/profile/v1";
    public static final String URL_SET_PROFILE_INFO_REQUEST = "user/profile";

    // User Rest (Profile Completion)
    public static final String URL_GET_PROFILE_COMPLETION_STATUS = "user/profilecompletion";

    // API version check Rest
    public static final String URL_GET_MIN_API_VERSION_REQUIRED = "app/min-supported-versions";

    // User Rest (Profile Picture)
    public static final String URL_SET_PROFILE_PICTURE = "user/profile/profilepicture/";

    // User Rest (Business Contact Profile Picture)
    public static final String URL_SET_BUSINESS_CONTACT_PROFILE_PICTURE = "user/profile/business-owner/picture";

    // User Rest (Address)
    public static final String URL_GET_USER_ADDRESS_REQUEST = "user/profile/address";
    public static final String URL_SET_USER_ADDRESS_REQUEST = "user/profile/address";

    // User Rest (Email)
    public static final String URL_GET_EMAIL = "user/emails/";
    public static final String URL_POST_EMAIL = "user/emails/";
    public static final String URL_DELETE_EMAIL = "user/emails/";
    public static final String URL_MAKE_PRIMARY_EMAIL = "/primary";

    // User Rest (Contact)
    public static final String URL_GET_CONTACTS = "contacts";
    public static final String URL_ADD_CONTACTS = "contacts";
    public static final String URL_UPDATE_CONTACTS = "contacts";
    public static final String URL_DELETE_CONTACTS = "contacts";

    // Business Information (Owner)
    public static final String URL_GET_BUSINESS_INFORMATION = "user/profile/business";
    public static final String URL_SET_BUSINESS_INFORMATION = "user/profile/business";

    // Business Information (Employee)
    public static final String URL_GET_BUSINESS_LIST_ALL = "business/list/all";

    public static final String URL_GET_BUSINESS_LIST_TRENDING = "trending";

    // Business Information (Owner)
    public static final String URL_CREATE_EMPLOYEE = "business-manager-invitation";
    public static final String URL_UPDATE_EMPLOYEE = "business/user";

    public static final String URL_GET_PENDING_EMPLOYEE_LIST = "business-manager-invitation/sent";
    public static final String URL_GET_EMPLOYEE_LIST = "business-manager";
    public static final String URL_GET_EMPLOYEE_DETAILS = "business/user/";
    public static final String URL_REMOVE_AN_EMPLOYEE_FIRST_PART = "business-manager/";
    public static final String URL_REMOVE_PENDING_EMPLOYEE = "/business-manager-invitation";
    public static final String URL_GET_BUSINESS_ROLES_DETAILS = "business-role/";
    public static final String URL_GET_ROLE_MANAGER_REQUESTS = "business-manager-invitation/received";

    // Education
    public static final String URL_GET_ALL_INSTITUTIONS_LIST = "institute/all";
    public static final String URL_GET_ALL_SESSIONS_LIST = "session";
    public static final String URL_GET_ENABLED_PAYABLES_LIST = "payableitem";
    public static final String URL_GET_STUDENT_INFO_BY_STUDENT_ID = "students";
    public static final String URL_MAKE_PAYMENT_EDUCATION = "receipt";

    // Ticket
    public static final String URL_CREATE_TICKET = "ticket/create";
    public static final String URL_GET_TICKETS = "ticket/list";
    public static final String URL_GET_TICKET_DETAILS = "ticket/by/id";
    public static final String URL_GET_TICKET_CATEGORIES = "ticket/categories";
    public static final String URL_ADD_COMMENT = "ticket/add/comment";
    public static final String URL_UPLOAD_TICKET_ATTACHMENT = "ticket/comment/attachment";
    public static final String URL_ADD_COMMENT_WITH_ATTACHMENT = "ticket/add/comment/attachments";

    // FCM notification
    public static final String URL_REFRESH_FIREBASE_TOKEN = "firebase/login/";

    // FCM notification
    public static final String URL_PROMO_ACTIVE = "promo/activate";

    // User Data collector
    public static final String URL_ENDPOINT_LOCATION_COLLECTOR = "location";

    public static final int HTTP_RESPONSE_STATUS_NOT_ACCEPTABLE = 406;
    public static final int HTTP_RESPONSE_STATUS_NOT_EXPIRED = 452;
    public static final int HTTP_RESPONSE_STATUS_NOT_FOUND = 404;
    public static final int HTTP_RESPONSE_STATUS_PAYMENT_REQUIRED = 402;
    public static final int HTTP_RESPONSE_STATUS_OK = 200;
    public static final int HTTP_RESPONSE_STATUS_PROCESSING = 102;
    public static final int HTTP_RESPONSE_STATUS_UNAUTHORIZED = 401;
    public static final int HTTP_RESPONSE_STATUS_BAD_REQUEST = 400;
    public static final int HTTP_RESPONSE_STATUS_ACCEPTED = 428;
    public static final int HTTP_RESPONSE_STATUS_INTERNAL_ERROR = 500;
    public static final int HTTP_RESPONSE_STATUS_BLOCKED = 403;

    public static final int PERSONAL_ACCOUNT_TYPE = 1;
    public static final int BUSINESS_ACCOUNT_TYPE = 2;
    public static final String BANK_ACCOUNT_STATUS_VERIFIED = "VERIFIED";
    public static final String BANK_ACCOUNT_STATUS_PENDING = "PENDING";
    public static final String BANK_ACCOUNT_STATUS_NOT_VERIFIED = "NOT_VERIFIED";
    public static final String BANK_ACCOUNT_STATUS_BLOCKED = "BLOCKED";

    public static final int MOBILE_TYPE_PREPAID = 1;
    public static final int MOBILE_TYPE_POSTPAID = 2;

    public static final long DEFAULT_TOKEN_TIME = 60000;   // By default token time is one minute
    public static final long DEFAULT_TOKEN_OVERLAP_TIME = 15000;   // By default token time is one minute
    public static final long MIN_REQUIRED_REFRESH_TOKEN_TIME = 24 * 60 * 60 * 1000;   // Refresh token should be called at least after 24 hours

    public static final String COMMAND_OTP_VERIFICATION = "COMMAND_OTP_VERIFICATION";
    public static final String COMMAND_RESEND_OTP = "COMMAND_RESEND_OTP";
    public static final String COMMAND_CHECK_IF_USER_EXISTS = "COMMAND_CHECK_IF_USER_EXISTS";
    public static final String COMMAND_REFRESH_TOKEN = "COMMAND_REFRESH_TOKEN";
    public static final String COMMAND_REFRESH_FIREBASE_TOKEN = "COMMAND_REFRESH_FIREBASE_TOKEN";
    public static final String COMMAND_SIGN_UP = "COMMAND_SIGN_UP";
    public static final String COMMAND_SIGN_UP_BUSINESS = "COMMAND_SIGN_UP_BUSINESS";
    public static final String COMMAND_LOG_IN = "COMMAND_LOG_IN";
    public static final String COMMAND_ASK_FOR_RECOMMENDATION = "COMMAND_ASK_FOR_RECOMMENDATION";
    public static final String COMMAND_LOG_OUT = "COMMAND_LOG_OUT";
    public static final String COMMAND_SEND_MONEY = "COMMAND_SEND_MONEY";
    public static final String COMMAND_PAYMENT = "COMMAND_PAYMENT";
    public static final String COMMAND_SWITCH_ACCOUNT = "COMMAND_SWITCH_ACCOUNT";
    public static final String COMMAND_GET_SERVICE_CHARGE = "COMMAND_GET_SERVICE_CHARGE";
    public static final String COMMAND_REFRESH_BALANCE = "COMMAND_REFRESH_BALANCE";
    public static final String COMMAND_GET_USER_ACTIVITIES = "COMMAND_GET_USER_ACTIVITIES";
    public static final String COMMAND_GET_TRANSACTION_HISTORY = "COMMAND_GET_TRANSACTION_HISTORY";
    public static final String COMMAND_GET_PENDING_TRANSACTION_HISTORY = "COMMAND_GET_PENDING_TRANSACTION_HISTORY";
    public static final String COMMAND_GET_MONEY_AND_PAYMENT_REQUESTS = "COMMAND_GET_MONEY_AND_PAYMENT_REQUESTS";
    public static final String COMMAND_ADD_TRUSTED_DEVICE = "COMMAND_ADD_TRUSTED_DEVICE";
    public static final String COMMAND_GET_TRUSTED_DEVICES = "COMMAND_GET_TRUSTED_DEVICES";
    public static final String COMMAND_REMOVE_TRUSTED_DEVICE = "COMMAND_REMOVE_TRUSTED_DEVICE";
    public static final String COMMAND_GET_PENDING_PAYMENT_REQUESTS_SENT = "COMMAND_GET_PENDING_PAYMENT_REQUESTS_SENT";
    public static final String COMMAND_CANCEL_REQUESTS_MONEY = "COMMAND_CANCEL_REQUESTS_MONEY";
    public static final String COMMAND_ACCEPT_REQUESTS_MONEY = "COMMAND_ACCEPT_REQUESTS_MONEY";
    public static final String COMMAND_REJECT_REQUESTS_MONEY = "COMMAND_REJECT_REQUESTS_MONEY";
    public static final String COMMAND_GET_MANAGED_BUSINESS_ACCOUNTS = "COMMAND_GET_MANAGED_BUSINESS_ACCOUNTS";
    public static final String COMMAND_CANCEL_PAYMENT_REQUEST = "COMMAND_CANCEL_PAYMENT_REQUEST";
    public static final String COMMAND_ACCEPT_PAYMENT_REQUEST = "COMMAND_ACCEPT_PAYMENT_REQUEST";
    public static final String COMMAND_REJECT_PAYMENT_REQUEST = "COMMAND_REJECT_PAYMENT_REQUEST";
    public static final String COMMAND_GET_PENDING_REQUESTS_ME = "COMMAND_GET_PENDING_REQUESTS_ME";
    public static final String COMMAND_REQUEST_MONEY = "COMMAND_REQUEST_MONEY";
    public static final String COMMAND_SEND_PAYMENT_REQUEST = "COMMAND_SEND_PAYMENT_REQUEST";
    public static final String COMMAND_GET_SINGLE_REQUEST_PAYMENT = "COMMAND_GET_SINGLE_REQUEST_PAYMENT";
    public static final String COMMAND_SET_PROFILE_PICTURE = "COMMAND_SET_PROFILE_PICTURE";
    public static final String COMMAND_SET_BUSINESS_CONTACT_PROFILE_PICTURE = "COMMAND_SET_BUSINESS_CONTACT_PROFILE_PICTURE";
    public static final String COMMAND_ADD_A_BANK = "COMMAND_ADD_A_BANK";
    public static final String COMMAND_VERIFICATION_BANK_WITH_AMOUNT = "COMMAND_VERIFICATION_BANK_WITH_AMOUNT";
    public static final String COMMAND_REMOVE_A_BANK = "COMMAND_REMOVE_A_BANK";
    public static final String COMMAND_TOPUP_REQUEST = "COMMAND_TOPUP_REQUEST";
    public static final String COMMAND_SET_PIN = "COMMAND_SET_PIN";
    public static final String COMMAND_ADD_PROMO = "COMMAND_ADD_PROMO";
    public static final String COMMAND_CHANGE_PASSWORD = "COMMAND_CHANGE_PASSWORD";
    public static final String COMMAND_CHANGE_PASSWORD_VALIDATION = "COMMAND_CHANGE_PASSWORD_VALIDATION";
    public static final String COMMAND_GET_PROFILE_INFO_REQUEST = "COMMAND_GET_PROFILE_INFO_REQUEST";
    public static final String COMMAND_GET_PARENT_INFO_REQUEST = "COMMAND_GET_PARENT_INFO_REQUEST";
    public static final String COMMAND_GET_OCCUPATIONS_REQUEST = "COMMAND_GET_OCCUPATIONS_REQUEST";
    public static final String COMMAND_GET_USER_ADDRESS_REQUEST = "COMMAND_GET_USER_ADDRESS_REQUEST";
    public static final String COMMAND_GET_IDENTIFICATION_DOCUMENTS_REQUEST = "COMMAND_GET_IDENTIFICATION_DOCUMENTS_REQUEST";
    public static final String COMMAND_GET_IDENTIFICATION_BUSINESS_DOCUMENTS_REQUEST = "COMMAND_GET_IDENTIFICATION_BUSINESS_DOCUMENTS_REQUEST";
    public static final String COMMAND_GET_INTRODUCER_LIST = "COMMAND_GET_INTRODUCER_LIST";
    public static final String COMMAND_GET_INTRODUCED_LIST = "COMMAND_GET_INTRODUCED_LIST";
    public static final String COMMAND_GET_DETAILS_OF_INVITED_BUSINESS_ROLE = "COMMAND_GET_DETAILS_OF_INVITED_BUSINESS_ROLE";
    public static final String COMMAND_GET_PENDING_INTRODUCER_LIST = "COMMAND_GET_PENDING_INTRODUCER_LIST";
    public static final String COMMAND_GET_SENT_REQUEST_LIST = "COMMAND_GET_SENT_REQUEST_LIST";
    public static final String COMMAND_SET_PROFILE_INFO_REQUEST = "COMMAND_SET_PROFILE_INFO_REQUEST";
    public static final String COMMAND_SET_PARENT_INFO_REQUEST = "COMMAND_SET_PARENT_INFO_REQUEST";
    public static final String COMMAND_SET_USER_ADDRESS_REQUEST = "COMMAND_SET_USER_ADDRESS_REQUEST";
    public static final String COMMAND_GET_BANK_LIST = "COMMAND_GET_BANK_LIST";
    public static final String COMMAND_GET_RECOMMENDATION_REQUESTS = "COMMAND_GET_RECOMMENDATION_REQUESTS";
    public static final String COMMAND_ADD_MONEY = "COMMAND_ADD_MONEY";
    public static final String COMMAND_INTRODUCE_ACTION = "COMMAND_INTRODUCE_ACTION";
    public static final String COMMAND_GET_USER_INFO = "COMMAND_GET_USER_INFO";
    public static final String COMMAND_WITHDRAW_MONEY = "COMMAND_WITHDRAW_MONEY";
    public static final String COMMAND_UPLOAD_DOCUMENT = "COMMAND_UPLOAD_DOCUMENT";
    public static final String COMMAND_GET_DOCUMENT_ACCESS_TOKEN = "COMMAND_GET_DOCUMENT_ACCESS_TOKEN";
    public static final String COMMAND_GET_PIN_INFO = "COMMAND_GET_PIN_INFO";
    public static final String COMMAND_GET_MONEY_REQUESTS = "COMMAND_GET_MONEY_REQUESTS";
    public static final String COMMAND_GET_PROFILE_COMPLETION_STATUS = "COMMAND_GET_PROFILE_COMPLETION_STATUS";
    public static final String COMMAND_GET_CONTACTS = "COMMAND_GET_CONTACTS";
    public static final String COMMAND_ADD_CONTACTS = "COMMAND_ADD_CONTACTS";
    public static final String COMMAND_DELETE_CONTACTS = "COMMAND_DELETE_CONTACTS";
    public static final String COMMAND_UPDATE_CONTACTS = "COMMAND_UPDATE_CONTACTS";
    public static final String COMMAND_GET_BUSINESS_RULE = "COMMAND_GET_BUSINESS_RULE";
    public static final String COMMAND_UPDATE_BUSINESS_ROLE_INVITATION = "COMMAND_UPDATE_BUSINESS_ROLE_INVITATION";
    public static final String COMMAND_GET_ALL_BUSINESS_LIST = "COMMAND_GET_ALL_BUSINESS_LIST";
    public static final String COMMAND_GET_TRENDING_BUSINESS_LIST = "COMMAND_GET_TRENDING_BUSINESS_LIST";
    public static final String COMMAND_GET_ALL_SECURITY_QUESTIONS = "COMMAND_GET_ALL_SECURITY_QUESTIONS";
    public static final String COMMAND_GET_SELECTED_SECURITY_QUESTIONS = "COMMAND_GET_SELECTED_SECURITY_QUESTIONS";
    public static final String COMMAND_SET_SECURITY_ANSWERS = "COMMAND_SET_SECURITY_ANSWERS";
    public static final String COMMAND_UPDATE_SECURITY_ANSWERS = "COMMAND_UPDATE_SECURITY_ANSWERS";
    public static final String COMMAND_GET_ACCESS_CONTROL_LIST = "COMMAND_GET_ACCESS_CONTROL_LIST";

    // Ticket
    public static final String COMMAND_CREATE_TICKET = "COMMAND_CREATE_TICKET";
    public static final String COMMAND_GET_TICKETS = "COMMAND_GET_TICKETS";
    public static final String COMMAND_GET_TICKET_DETAILS = "COMMAND_GET_TICKET_DETAILS";
    public static final String COMMAND_GET_TICKET_CATEGORIES = "COMMAND_GET_TICKET_CATEGORIES";
    public static final String COMMAND_ADD_COMMENT = "COMMAND_ADD_COMMENT";
    public static final String COMMAND_ADD_ATTACHMENT = "COMMAND_ADD_ATTACHMENT";

    // Resource
    public static final String COMMAND_GET_AVAILABLE_BANK_LIST = "COMMAND_GET_AVAILABLE_BANK_LIST";
    public static final String COMMAND_GET_BUSINESS_TYPE_LIST = "COMMAND_GET_BUSINESS_TYPE_LIST";
    public static final String COMMAND_GET_THANA_LIST = "COMMAND_GET_THANA_LIST";
    public static final String COMMAND_GET_DISTRICT_LIST = "COMMAND_GET_DISTRICT_LIST";
    public static final String COMMAND_GET_BANK_BRANCH_LIST = "COMMAND_GET_BANK_BRANCH_LIST";
    public static final String COMMAND_GET_RELATIONSHIP_LIST = "COMMAND_GET_RELATIONSHIP_LIST";

    // Invite
    public static final String COMMAND_GET_INVITE_INFO = "COMMAND_GET_INVITE_INFO";
    public static final String COMMAND_SEND_INVITE = "COMMAND_SEND_INVITE";

    // Email
    public static final String COMMAND_GET_EMAILS = "COMMAND_GET_EMAILS";
    public static final String COMMAND_ADD_NEW_EMAIL = "COMMAND_ADD_NEW_EMAIL";
    public static final String COMMAND_EMAIL_MAKE_PRIMARY = "COMMAND_EMAIL_MAKE_PRIMARY";
    public static final String COMMAND_DELETE_EMAIL = "COMMAND_DELETE_EMAIL";

    // Trusted Network
    public static final String COMMAND_GET_TRUSTED_PERSONS = "COMMAND_GET_TRUSTED_PERSONS";
    public static final String COMMAND_ADD_TRUSTED_PERSON = "COMMAND_ADD_TRUSTED_PERSON";
    public static final String COMMAND_REMOVE_TRUSTED_PERSON = "COMMAND_REMOVE_TRUSTED_PERSON";

    // Business Information (Owner)
    public static final String COMMAND_GET_BUSINESS_INFORMATION = "COMMAND_GET_BUSINESS_INFORMATION";
    public static final String COMMAND_SET_BUSINESS_INFORMATION = "COMMAND_SET_BUSINESS_INFORMATION";
    public static final String COMMAND_GET_DETAILS_OF_BUSINESS_ROLE = "COMMAND_GET_DETAILS_OF_BUSINESS_ROLE";
    public static final String COMMAND_GET_ROLE_MAANGER_REQUESTS = "COMMAND_GET_ROLE_MAANGER_REQUESTS";
    public static final String COMMAND_LEAVE_ACCOUNT = "COMMAND_LEAVE_ACCOUNT";

    public static final String COMMAND_CREATE_EMPLOYEE = "COMMAND_CREATE_EMPLOYEE";
    public static final String COMMAND_UPDATE_EMPLOYEE = "COMMAND_UPDATE_EMPLOYEE";
    public static final String COMMAND_GET_PENDING_EMPLOYEE_LIST = "COMMAND_GET_PENDING_EMPLOYEE_LIST";
    public static final String COMMAND_GET_ACCEPTED_EMPLOYEE_LIST = "COMMAND_GET_ACCEPTED_EMPLOYEE_LIST";
    public static final String COMMAND_REMOVE_AN_EMPLOYEE = "COMMAND_REMOVE_AN_EMPLOYEE";
    public static final String COMMAND_GET_EMPLOYEE_DETAILS = "COMMAND_GET_EMPLOYEE_DETAILS";
    public static final String COMMAND_GET_ALL_ROLES = "COMMAND_GET_ALL_ROLES";

    // Education
    public static final String COMMAND_GET_INSTITUTION_LIST = "COMMAND_GET_INSTITUTION_LIST";
    public static final String COMMAND_GET_SESSION_LIST = "COMMAND_GET_SESSION_LIST";
    public static final String COMMAND_GET_ENABLED_PAYABLES_LIST = "COMMAND_GET_ENABLED_PAYABLES_LIST";
    public static final String COMMAND_GET_STUDENT_INFO_BY_STUDENT_ID = "COMMAND_GET_STUDENT_INFO_BY_STUDENT_ID";
    public static final String COMMAND_MAKE_PAYMENT_EDUCATION = "COMMAND_MAKE_PAYMENT_EDUCATION";

    //Two FA
    public static final String COMMAND_GET_TWO_FACTOR_AUTH_SETTINGS = "COMMAND_GET_TWO_FACTOR_AUTH_SETTINGS";
    public static final String COMMAND_PUT_TWO_FACTOR_AUTH_SETTINGS = "COMMAND_PUT_TWO_FACTOR_AUTH_SETTINGS";

    //Data Collector
    public static final String COMMAND_POST_USER_LOCATION = "COMMAND_POST_USER_LOCATION";

    public static final int ACTIVITY_LOG_COUNT = 10;

    public static final int ACTIVITY_TYPE_CHANGE_PROFILE = 0;
    public static final int ACTIVITY_TYPE_MONEY_IN = 1;
    public static final int ACTIVITY_TYPE_MONEY_OUT = 2;
    public static final int ACTIVITY_TYPE_VERIFICATION = 3;
    public static final int ACTIVITY_TYPE_SYSTEM_EVENT = 4;
    public static final int ACTIVITY_TYPE_CHANGE_SECURITY = 5;

    public static final String EMAIL_VERIFICATION_STATUS_VERIFIED = "VERIFIED";
    public static final String EMAIL_VERIFICATION_STATUS_VERIFICATION_IN_PROGRESS = "IN_PROGRESS";

    public static final String ADD_BANK = "ADD_BANK";
    public static final String VERIFY_BANK = "VERIFY_BANK";

    public static final String INTRODUCTION_REQUEST_STATUS_PENDING = "PENDING";
    public static final String INTRODUCTION_REQUEST_STATUS_APPROVED = "APPROVED";
    public static final String INTRODUCTION_REQUEST_STATUS_SPAM = "MARKED_SPAM";

    public static final String INTRODUCTION_REQUEST_ACTION_APPROVE = "approve";
    public static final String INTRODUCTION_REQUEST_ACTION_REJECT = "reject";
    public static final String INTRODUCTION_REQUEST_ACTION_MARK_AS_SPAM = "mark-spam";

    public static final int TRANSACTION_HISTORY_OPENING_BALANCE = 1001;
    public static final int TRANSACTION_HISTORY_SEND_MONEY = 1;
    public static final int TRANSACTION_HISTORY_REQUEST_MONEY = 6001;
    public static final int TRANSACTION_HISTORY_ADD_MONEY_BY_BANK = 3001;
    public static final int TRANSACTION_HISTORY_ADD_MONEY_BY_CREDIT_OR_DEBIT_CARD = 3011;
    public static final int TRANSACTION_HISTORY_ADD_MONEY_REVERT = 963001;
    public static final int TRANSACTION_HISTORY_WITHDRAW_MONEY = 3002;
    public static final int TRANSACTION_HISTORY_TOP_UP = 2001;
    public static final int TRANSACTION_HISTORY_MAKE_PAYMENT = 6002;
    public static final int TRANSACTION_HISTORY_EDUCATION = 8001;
    public static final int TRANSACTION_HISTORY_TOP_UP_ROLLBACK = 2002;
    public static final int TRANSACTION_HISTORY_WITHDRAW_MONEY_ROLL_BACK = 3003;
    public static final int TRANSACTION_HISTORY_REQUEST_PAYMENT = 6005;
    public static final int TRANSACTION_HISTORY_WITHDRAW_MONEY_REVERT = 3502;
    public static final int TRANSACTION_HISTORY_OFFER = 1100;
    public static final int TRANSACTION_HISTORY_INTERNAL_BALANCE_TRANSFER = 7001;

    public static final int SERVICE_ID_REQUEST_MONEY = 6001;
    public static final int SERVICE_ID_REQUEST_INVOICE = 6003;
    public static final int SERVICE_ID_SEND_MONEY = 1;
    public static final int SERVICE_ID_ADD_MONEY_BY_BANK = 3001;
    public static final int SERVICE_ID_ADD_MONEY_BY_CREDIT_OR_DEBIT_CARD = 3011;
    public static final int SERVICE_ID_WITHDRAW_MONEY = 3002;
    public static final int SERVICE_ID_TOP_UP = 2001;
    public static final int SERVICE_ID_MAKE_PAYMENT = 6002;
    public static final int SERVICE_ID_REQUEST_PAYMENT = 6005;
    public static final int SERVICE_ID_BATCH_NOTIFICATION = 9003;

    public static final String RESULT = "Result";
    public static final String GET_REQUEST = "GET_RESULT: ";
    public static final String GET_URL = "GET_URL: ";
    public static final String DELETE_URL = "DELETE_URL: ";

    public static final String PARSED_TOKEN = "Parsed Token: ";

    public static final String DOCUMENT_ID_NUMBER = "documentIdNumber";
    public static final String DOCUMENT_TYPE = "documentType";
    public static final String DOCUMENT_NAME = "documentName";
    public static final String BANK_ID = "bankId";

    public static final String ACCOUNT_VERIFICATION_STATUS_VERIFIED = "VERIFIED";
    public static final String ACCOUNT_VERIFICATION_STATUS_NOT_VERIFIED = "NOT_VERIFIED";

    public static final String ADDRESS_TYPE_PRESENT = "PRESENT";
    public static final String ADDRESS_TYPE_PERMANENT = "PERMANENT";
    public static final String ADDRESS_TYPE_OFFICE = "OFFICE";

    public static final String TOTAL = "total";
    public static final String ROLENAME = "role";
    public static final String RECEIVER_MOBILE_NUMBER = "receiver";
    public static final String INVOICE_ITEM_NAME_TAG = "item_name";
    public static final String DESCRIPTION_TAG = "description";
    public static final String REFERENCE_NUMBER = "reference number";
    public static final String AMOUNT_TAG = "amount";

    public static final int INVOICE_STATUS_ACCEPTED = 200;
    public static final int INVOICE_STATUS_PROCESSING = 102;
    public static final int INVOICE_STATUS_CANCELED = 2;
    public static final int INVOICE_STATUS_REJECTED = 3;
    public static final int INVOICE_STATUS_DRAFT = 4;

    public static final int MONEY_REQUEST_STATUS_ACCEPTED = 200;
    public static final int MONEY_REQUEST_STATUS_PROCESSING = 102;
    public static final int PAYMENT_REQUEST_STATUS_ALL = -1;

    public static final int TRANSACTION_STATUS_ACCEPTED = 200;
    public static final int TRANSACTION_STATUS_PROCESSING = 102;
    public static final int TRANSACTION_STATUS_CANCELLED = 2;
    public static final int TRANSACTION_STATUS_REJECTED = 3;
    public static final int TRANSACTION_STATUS_FAILED = 444;

    public static final String IMAGE_QUALITY_LOW = "low";
    public static final String IMAGE_QUALITY_MEDIUM = "medium";
    public static final String IMAGE_QUALITY_HIGH = "high";

    public static final int NOTIFICATION_TYPE_REQUEST_MONEY = 1;
    public static final int NOTIFICATION_TYPE_MAKE_PAYMENT = 2;
    public static final int NOTIFICATION_TYPE_INTRODUCTION_REQUEST = 4;
    public static final int NOTIFICATION_TYPE_PENDING_INTRODUCER_REQUEST = 5;
    public static final int NOTIFICATION_TYPE_PENDING_ROLE_MANAGER_REQUEST = 6;

    public static final String ACTION_TYPE_VERIFY = "Verify";
    public static final String ACTION_TYPE_REMOVE = "Remove";
    public static final String ACTION_TYPE_MAKE_PRIMARY = "Make Primary";
    public static final String ACTION_TYPE_TAKE_PICTURE = "Take a picture";
    public static final String ACTION_TYPE_SELECT_FROM_GALLERY = "Select from gallery";

    public static final String SERVICE_ACTION_REQUEST_PAYMENT = "Request Payment";
    public static final String SERVICE_ACTION_MAKE_PAYMENT = "Make Payment";
    public static final String SERVICE_ACTION_TOP_UP = "Mobile TopUp";
    public static final String SERVICE_ACTION_PAY_BY_QR_CODE = "Pay by QR Code";

    public static final String TICKET_STATUS_NEW = "new";
    public static final String TICKET_STATUS_OPEN = "open";
    public static final String TICKET_STATUS_PENDING = "pending";
    public static final String TICKET_STATUS_ON_HOLD = "hold";
    public static final String TICKET_STATUS_SOLVED = "solved";
    public static final String TICKET_STATUS_CLOSED = "closed";

    public static final String TICKET_COMMENT_ID = "commentId";

    public static final int REQUEST_TYPE_RECEIVED_REQUEST = 1;
    public static final int REQUEST_TYPE_SENT_REQUEST = 2;

    public static final int TYPE_PROFILE_PICTURE = 1;
    public static final int TYPE_BUSINESS_LOGO = 2;

    public static final int INVALID_ACCOUNT_ID = -1;

    // API Version Checker
    public static boolean IS_API_VERSION_CHECKED = false;
    public static boolean ACCOUNT_DEFAULT = false;
    public static boolean ACCOUNT_SWITCHED = true;
    public static boolean HAS_COME_FROM_BACKGROUND_TO_FOREGROUND = false;

    // Format
    public static final String DATE_FORMAT = "%02d/%02d/%4d";

    public static final String[] LOCATION_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION};

    public static final String PROFILE_PHOTO_PATH = "profile_photo_path";

    public static final String METHOD_POST = "POST";
    public static final String METHOD_PUT = "PUT";

    public static final String TRANSACTION_TYPE_CREDIT = "Cr";
    public static final String TRANSACTION_TYPE_DEBIT = "Dr";
    public static final String TRANSACTION_TYPE_USER = "USER";
    public static final String TRANSACTION_TYPE_INTERNAL = "INTERNAL";
    public static final String TRANSACTION_TYPE_CARD = "CARD";
    public static final String TRANSACTION_TYPE_BANK = "BANK";

    public static final String VISA_CARD_STARTS_WITH_REGEX = "4(.*)";
    public static final String AMEX_CARD_STARTS_WITH_REGEX = "3[47](.*)";
    public static final String MASTER_CARD_STARTS_WITH_REGEX = "5[1-5](.*)";

    public static final String ON_ACCOUNT_ID_DEFAULT = null;
    public static final long ACCOUNT_ID_DEFAULT = -1;
    public static final GetBusinessInformationResponse ACCOUNT_INFO_DEFAULT = null;

    public static final String QUALITY_HIGH = "High";

    public static final int LOCATION_REQUIRED_TRUE = 1;

    public static final int RC_BARCODE_CAPTURE = 9001;

}
