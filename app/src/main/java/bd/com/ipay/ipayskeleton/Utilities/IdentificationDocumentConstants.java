package bd.com.ipay.ipayskeleton.Utilities;

import java.util.HashMap;
import java.util.Map;

import bd.com.ipay.ipayskeleton.R;

public class IdentificationDocumentConstants {

    public static final String DOCUMENT_TYPE_NATIONAL_ID = "national_id";
    public static final String DOCUMENT_TYPE_VAT_REG_CERT = "vat_reg_certificate";
    public static final String DOCUMENT_TYPE_TRADE_LICENSE = "trade_license";
    public static final String DOCUMENT_TYPE_BUSINESS_TIN = "business_tin";
    public static final String DOCUMENT_TYPE_DRIVING_LICENSE = "driving_license";
    public static final String DOCUMENT_TYPE_TIN = "tin";
    public static final String DOCUMENT_TYPE_BIRTH_CERTIFICATE = "birth_certificate";
    public static final String DOCUMENT_SIDE_FRONT = "front_side";
    public static final String DOCUMENT_SIDE_BACK = "back_side";

    public static String DOCUMENT_TYPE_OTHER = "other";
    public static final String DOCUMENT_TYPE_PASSPORT = "passport";

    public static final String DOCUMENT_VERIFICATION_STATUS_VERIFIED = "VERIFIED";
    public static final String DOCUMENT_VERIFICATION_STATUS_NOT_VERIFIED = "NOT_VERIFIED";

    public static final String[] BUSINESS_DOCUMENT_TYPES = {
            DOCUMENT_TYPE_NATIONAL_ID,
            DOCUMENT_TYPE_BUSINESS_TIN,
            DOCUMENT_TYPE_TRADE_LICENSE,
            DOCUMENT_TYPE_VAT_REG_CERT,
            DOCUMENT_TYPE_DRIVING_LICENSE,
            DOCUMENT_TYPE_PASSPORT,
            DOCUMENT_TYPE_OTHER
    };
    public static final String[] PERSONAL_DOCUMENT_TYPES = {
            DOCUMENT_TYPE_NATIONAL_ID,
            DOCUMENT_TYPE_PASSPORT,
            DOCUMENT_TYPE_DRIVING_LICENSE,
            DOCUMENT_TYPE_BIRTH_CERTIFICATE,
            DOCUMENT_TYPE_TIN,
            DOCUMENT_TYPE_OTHER
    };

    public static final Map<String, Integer> DOCUMENT_ID_TO_EDIT_TEXT_HINT_MAP;
    public static final Map<String, Integer> DOCUMENT_ID_MAX_PAGE_COUNT_MAP;

    static {
        DOCUMENT_ID_TO_EDIT_TEXT_HINT_MAP = new HashMap<>();

        //Common Document
        DOCUMENT_ID_TO_EDIT_TEXT_HINT_MAP.put(DOCUMENT_TYPE_NATIONAL_ID, R.string.national_id);
        DOCUMENT_ID_TO_EDIT_TEXT_HINT_MAP.put(DOCUMENT_TYPE_OTHER, R.string.other_id);
        DOCUMENT_ID_TO_EDIT_TEXT_HINT_MAP.put(DOCUMENT_TYPE_DRIVING_LICENSE, R.string.driving_license_id);
        DOCUMENT_ID_TO_EDIT_TEXT_HINT_MAP.put(DOCUMENT_TYPE_PASSPORT, R.string.passport_id);

        //Personal Account Documents
        DOCUMENT_ID_TO_EDIT_TEXT_HINT_MAP.put(DOCUMENT_TYPE_BIRTH_CERTIFICATE, R.string.birth_certificate_id);
        DOCUMENT_ID_TO_EDIT_TEXT_HINT_MAP.put(DOCUMENT_TYPE_TIN, R.string.personal_tin_id);

        //Business Account Documents
        DOCUMENT_ID_TO_EDIT_TEXT_HINT_MAP.put(DOCUMENT_TYPE_BUSINESS_TIN, R.string.business_tin_id);
        DOCUMENT_ID_TO_EDIT_TEXT_HINT_MAP.put(DOCUMENT_TYPE_TRADE_LICENSE, R.string.trade_license_id);
        DOCUMENT_ID_TO_EDIT_TEXT_HINT_MAP.put(DOCUMENT_TYPE_VAT_REG_CERT, R.string.vat_registration_certificate_id);
        DOCUMENT_ID_TO_EDIT_TEXT_HINT_MAP.put(DOCUMENT_TYPE_TRADE_LICENSE, R.string.trade_license_id);
    }

    static {
        DOCUMENT_ID_MAX_PAGE_COUNT_MAP = new HashMap<>();

        //Common Document
        DOCUMENT_ID_MAX_PAGE_COUNT_MAP.put(DOCUMENT_TYPE_NATIONAL_ID, 2);
        DOCUMENT_ID_MAX_PAGE_COUNT_MAP.put(DOCUMENT_TYPE_OTHER, 2);
        DOCUMENT_ID_MAX_PAGE_COUNT_MAP.put(DOCUMENT_TYPE_DRIVING_LICENSE, 2);
        DOCUMENT_ID_MAX_PAGE_COUNT_MAP.put(DOCUMENT_TYPE_PASSPORT, 2);

        //Personal Account Documents
        DOCUMENT_ID_MAX_PAGE_COUNT_MAP.put(DOCUMENT_TYPE_BIRTH_CERTIFICATE, 1);
        DOCUMENT_ID_MAX_PAGE_COUNT_MAP.put(DOCUMENT_TYPE_TIN, 1);

        //Business Account Documents
        DOCUMENT_ID_MAX_PAGE_COUNT_MAP.put(DOCUMENT_TYPE_BUSINESS_TIN, 1);
        DOCUMENT_ID_MAX_PAGE_COUNT_MAP.put(DOCUMENT_TYPE_TRADE_LICENSE, 1);
        DOCUMENT_ID_MAX_PAGE_COUNT_MAP.put(DOCUMENT_TYPE_VAT_REG_CERT, 1);
        DOCUMENT_ID_MAX_PAGE_COUNT_MAP.put(DOCUMENT_TYPE_TRADE_LICENSE, 1);
    }
}
