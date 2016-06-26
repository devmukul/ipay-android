package bd.com.ipay.ipayskeleton.Model.MMModule.Business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bd.com.ipay.ipayskeleton.R;

public class PrivilegeConstants {
    private static final String SEE_OTHER_ACTIVITY = "SEE_OTHER_ACTIVITY";
    private static final String SEE_BANK_ACCOUNTS = "SEE_BANK_ACCOUNTS";
    private static final String MANAGE_BANK_ACCOUNTS = "MANAGE_BANK_ACCOUNTS";
    private static final String SEE_EMPLOYEE = "SEE_EMPLOYEE";
    private static final String MANAGE_EMPLOYEE = "MANAGE_EMPLOYEE";
    private static final String SEE_EMAILS = "SEE_EMAILS";
    private static final String MANAGE_EMAILS = "MANAGE_EMAILS";
    private static final String SEE_ADDRESSES = "SEE_ADDRESSES";
    private static final String SEE_BUSINESS_INFO = "SEE_BUSINESS_INFO";
    private static final String UPDATE_BUSINESS_INFO = "UPDATE_BUSINESS_INFO";

    public static final List<Privilege> ALL_PRIVILEGES = new ArrayList<>();

    static {
        ALL_PRIVILEGES.add(new Privilege(SEE_BANK_ACCOUNTS));
        ALL_PRIVILEGES.add(new Privilege(SEE_EMPLOYEE));
        ALL_PRIVILEGES.add(new Privilege(MANAGE_EMPLOYEE));
        ALL_PRIVILEGES.add(new Privilege(SEE_EMAILS));
        ALL_PRIVILEGES.add(new Privilege(SEE_OTHER_ACTIVITY));
        ALL_PRIVILEGES.add(new Privilege(SEE_ADDRESSES));
        ALL_PRIVILEGES.add(new Privilege(SEE_BUSINESS_INFO));
        ALL_PRIVILEGES.add(new Privilege(UPDATE_BUSINESS_INFO));
        ALL_PRIVILEGES.add(new Privilege(MANAGE_EMAILS));
        ALL_PRIVILEGES.add(new Privilege(MANAGE_BANK_ACCOUNTS));
    }

    /**
     * Maps privilege name into a more user friendly format (e.g. "SEE_BANK_ACCOUNTS" to "Send Money").
     */
    public static final Map<String, Integer> PRIVILEGE_NAME_MAP = new HashMap<>();

    static {
        PRIVILEGE_NAME_MAP.put(SEE_BANK_ACCOUNTS, R.string.see_bank_accounts);
        PRIVILEGE_NAME_MAP.put(SEE_EMPLOYEE, R.string.see_employee);
        PRIVILEGE_NAME_MAP.put(MANAGE_EMPLOYEE, R.string.manage_employee);
        PRIVILEGE_NAME_MAP.put(SEE_EMAILS, R.string.see_emails);
        PRIVILEGE_NAME_MAP.put(SEE_OTHER_ACTIVITY, R.string.see_other_activity);
        PRIVILEGE_NAME_MAP.put(SEE_ADDRESSES, R.string.see_addresses);
        PRIVILEGE_NAME_MAP.put(SEE_BUSINESS_INFO, R.string.see_business_info);
        PRIVILEGE_NAME_MAP.put(UPDATE_BUSINESS_INFO, R.string.update_business_info);
        PRIVILEGE_NAME_MAP.put(MANAGE_EMAILS, R.string.manage_emails);
        PRIVILEGE_NAME_MAP.put(MANAGE_BANK_ACCOUNTS, R.string.manage_bank_accounts);
    }
}
