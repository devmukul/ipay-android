package bd.com.ipay.ipayskeleton.Model.MMModule.Pay;

import java.util.HashMap;

import bd.com.ipay.ipayskeleton.R;

public class PayPropertyConstants {
    public static final String TOP_UP = "Mobile TopUp";
    public static final String MAKE_PAYMENT = "Make Payment";
    public static final String EDUCATION = "Education Payment";
    public static final String REQUEST_PAYMENT = "Request Payment";
    public static final String PAY_BY_QR_CODE = "Pay by QR Code";

    public static final HashMap<String, Integer> PAY_PROPERTY_NAME_TO_ICON_MAP = new HashMap<>();

    static {
        PAY_PROPERTY_NAME_TO_ICON_MAP.put(TOP_UP, R.drawable.ic_topup);
        PAY_PROPERTY_NAME_TO_ICON_MAP.put(MAKE_PAYMENT, R.drawable.ic_make_payment);
        PAY_PROPERTY_NAME_TO_ICON_MAP.put(EDUCATION, R.drawable.ic_education);
        PAY_PROPERTY_NAME_TO_ICON_MAP.put(REQUEST_PAYMENT, R.drawable.ic_request_payment);
        PAY_PROPERTY_NAME_TO_ICON_MAP.put(PAY_BY_QR_CODE, R.drawable .ic_paybyqrc);
    }
}
