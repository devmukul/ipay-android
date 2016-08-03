package bd.com.ipay.ipayskeleton.Model.MMModule.Pay;

import java.util.HashMap;

import bd.com.ipay.ipayskeleton.R;

public class PayPropertyConstants {
    public static final String TOP_UP = "Mobile TopUp";
    public static final String MAKE_PAYMENT = "Make Payment";
    public static final String EDUCATION = "Education Payment";
    public static final String CREATE_INVOICE = "Create Invoice";

    public static final HashMap<String, Integer> PAY_PROPERTY_NAME_TO_ICON_MAP = new HashMap<>();

    static {
        PAY_PROPERTY_NAME_TO_ICON_MAP.put(TOP_UP, R.drawable.ic_top_up);
        PAY_PROPERTY_NAME_TO_ICON_MAP.put(MAKE_PAYMENT, R.drawable.ic_make_payment);
        PAY_PROPERTY_NAME_TO_ICON_MAP.put(EDUCATION, R.drawable.ic_education);
        PAY_PROPERTY_NAME_TO_ICON_MAP.put(CREATE_INVOICE, R.drawable.ic_make_payment);
    }
}
