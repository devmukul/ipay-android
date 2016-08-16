package bd.com.ipay.ipayskeleton.Model.MMModule.Business.Owner;

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
    private static final String SEE_ADDRESSES = "SEE_ADDRESSES";
    private static final String SEE_BUSINESS_INFO = "SEE_BUSINESS_INFO";
    private static final String UPDATE_BUSINESS_INFO = "UPDATE_BUSINESS_INFO";
    private static final String MANAGE_EMAILS = "MANAGE_EMAILS";
    private static final String GET_BALANCE = "GetBalance";
    private static final String GET_TRANSACTION = "GetTransaction";
    private static final String SEND_MONEY = "SendMoney";
    private static final String REQUEST_MONEY = "RequestMoney";
    private static final String PAYMENT = "Payment";
    private static final String REQUEST_PAYMENT = "RequestPayment";
    private static final String INVOICE = "Invoice";
    private static final String ADD_MONEY = "AddMoney";
    private static final String WITHDRAW_MONEY = "WithdrawMoney";
    private static final String TOP_UP = "Topup";
    private static final String EDUCATION_MANAGE = "EducationManage";
    private static final String FRIEND_READ = "FRIEND_READ";
    private static final String FRIEND_WRITE = "FRIEND_WRITE";
    private static final String SEE_OWNER_INFO = "SEE_OWNER_INFO";
    private static final String MANAGE_OWNER_INFO = "MANAGE_OWNER_INFO";
    private static final String SEE_DOCS = "SEE_DOCS";
    private static final String SEE_BUSINESS_DOCS = "SEE_BUSINESS_DOCS";
    private static final String UPLOAD_DOCS = "UPLOAD_DOCS";
    private static final String UPLOAD_BUSINESS_DOCS = "UPLOAD_BUSINESS_DOCS";
    private static final String SEE_INTRODUCERS = "SEE_INTRODUCERS";
    private static final String MANAGE_INTRODUCERS = "MANAGE_INTRODUCERS";
    private static final String SEE_INVITATIONS = "SEE_INVITATIONS";
    private static final String MANAGE_INVITATIONS = "MANAGE_INVITATIONS";
    private static final String MANAGE_ADDRESS = "MANAGE_ADDRESS";
    private static final String SEE_PROFILE_COMPLETION = "SEE_PROFILE_COMPLETION";

    public static final List<Privilege> ALL_PRIVILEGES = new ArrayList<>();

    static {
        ALL_PRIVILEGES.add(new Privilege(SEE_OTHER_ACTIVITY));
        ALL_PRIVILEGES.add(new Privilege(SEE_BANK_ACCOUNTS));
        ALL_PRIVILEGES.add(new Privilege(MANAGE_BANK_ACCOUNTS));
        ALL_PRIVILEGES.add(new Privilege(SEE_EMPLOYEE));
        ALL_PRIVILEGES.add(new Privilege(MANAGE_EMPLOYEE));
        ALL_PRIVILEGES.add(new Privilege(SEE_EMAILS));
        ALL_PRIVILEGES.add(new Privilege(SEE_ADDRESSES));
        ALL_PRIVILEGES.add(new Privilege(SEE_BUSINESS_INFO));
        ALL_PRIVILEGES.add(new Privilege(UPDATE_BUSINESS_INFO));
        ALL_PRIVILEGES.add(new Privilege(MANAGE_EMAILS));
        ALL_PRIVILEGES.add(new Privilege(GET_BALANCE));
        ALL_PRIVILEGES.add(new Privilege(GET_TRANSACTION));
        ALL_PRIVILEGES.add(new Privilege(SEND_MONEY));
        ALL_PRIVILEGES.add(new Privilege(REQUEST_MONEY));
        ALL_PRIVILEGES.add(new Privilege(PAYMENT));
        ALL_PRIVILEGES.add(new Privilege(REQUEST_PAYMENT));
        ALL_PRIVILEGES.add(new Privilege(INVOICE));
        ALL_PRIVILEGES.add(new Privilege(ADD_MONEY));
        ALL_PRIVILEGES.add(new Privilege(WITHDRAW_MONEY));
        ALL_PRIVILEGES.add(new Privilege(TOP_UP));
        ALL_PRIVILEGES.add(new Privilege(EDUCATION_MANAGE));
        ALL_PRIVILEGES.add(new Privilege(FRIEND_READ));
        ALL_PRIVILEGES.add(new Privilege(FRIEND_WRITE));
        ALL_PRIVILEGES.add(new Privilege(SEE_OWNER_INFO));
        ALL_PRIVILEGES.add(new Privilege(MANAGE_OWNER_INFO));
        ALL_PRIVILEGES.add(new Privilege(SEE_DOCS));
        ALL_PRIVILEGES.add(new Privilege(SEE_BUSINESS_DOCS));
        ALL_PRIVILEGES.add(new Privilege(UPLOAD_DOCS));
        ALL_PRIVILEGES.add(new Privilege(UPLOAD_BUSINESS_DOCS));
        ALL_PRIVILEGES.add(new Privilege(SEE_INTRODUCERS));
        ALL_PRIVILEGES.add(new Privilege(MANAGE_INTRODUCERS));
        ALL_PRIVILEGES.add(new Privilege(SEE_INVITATIONS));
        ALL_PRIVILEGES.add(new Privilege(MANAGE_INVITATIONS));
        ALL_PRIVILEGES.add(new Privilege(MANAGE_ADDRESS));
        ALL_PRIVILEGES.add(new Privilege(SEE_PROFILE_COMPLETION));
    }

    /**
     * Maps privilege name into a more user friendly format (e.g. "SEE_BANK_ACCOUNTS" to "Send Money").
     */
    public static final Map<String, Integer> PRIVILEGE_NAME_MAP = new HashMap<>();

    static {
        PRIVILEGE_NAME_MAP.put(SEE_OTHER_ACTIVITY, R.string.see_other_activity);
        PRIVILEGE_NAME_MAP.put(SEE_BANK_ACCOUNTS, R.string.see_bank_accounts);
        PRIVILEGE_NAME_MAP.put(MANAGE_BANK_ACCOUNTS, R.string.manage_bank_accounts);
        PRIVILEGE_NAME_MAP.put(SEE_EMPLOYEE, R.string.see_employee);
        PRIVILEGE_NAME_MAP.put(MANAGE_EMPLOYEE, R.string.manage_employee);
        PRIVILEGE_NAME_MAP.put(SEE_EMAILS, R.string.see_emails);
        PRIVILEGE_NAME_MAP.put(SEE_ADDRESSES, R.string.see_addresses);
        PRIVILEGE_NAME_MAP.put(SEE_BUSINESS_INFO, R.string.see_business_info);
        PRIVILEGE_NAME_MAP.put(UPDATE_BUSINESS_INFO, R.string.update_business_info);
        PRIVILEGE_NAME_MAP.put(MANAGE_EMAILS, R.string.manage_emails);
        PRIVILEGE_NAME_MAP.put(GET_BALANCE, R.string.get_balance);
        PRIVILEGE_NAME_MAP.put(GET_TRANSACTION, R.string.get_transactions);
        PRIVILEGE_NAME_MAP.put(SEND_MONEY, R.string.send_money);
        PRIVILEGE_NAME_MAP.put(REQUEST_MONEY, R.string.request_money);
        PRIVILEGE_NAME_MAP.put(PAYMENT, R.string.payment);
        PRIVILEGE_NAME_MAP.put(REQUEST_PAYMENT, R.string.request_payment);
        PRIVILEGE_NAME_MAP.put(INVOICE, R.string.invoice);
        PRIVILEGE_NAME_MAP.put(ADD_MONEY, R.string.add_money);
        PRIVILEGE_NAME_MAP.put(WITHDRAW_MONEY, R.string.withdraw_money);
        PRIVILEGE_NAME_MAP.put(TOP_UP, R.string.mobile_topup);
        PRIVILEGE_NAME_MAP.put(EDUCATION_MANAGE, R.string.education_management);
        PRIVILEGE_NAME_MAP.put(FRIEND_READ, R.string.friend_read);
        PRIVILEGE_NAME_MAP.put(FRIEND_WRITE, R.string.friend_write);
        PRIVILEGE_NAME_MAP.put(SEE_OWNER_INFO, R.string.see_owner_info);
        PRIVILEGE_NAME_MAP.put(MANAGE_OWNER_INFO, R.string.manage_owner_info);
        PRIVILEGE_NAME_MAP.put(SEE_DOCS, R.string.see_docs);
        PRIVILEGE_NAME_MAP.put(SEE_BUSINESS_DOCS, R.string.see_business_docs);
        PRIVILEGE_NAME_MAP.put(UPLOAD_DOCS, R.string.upload_docs);
        PRIVILEGE_NAME_MAP.put(UPLOAD_BUSINESS_DOCS, R.string.upload_business_docs);
        PRIVILEGE_NAME_MAP.put(SEE_INTRODUCERS, R.string.see_introducers);
        PRIVILEGE_NAME_MAP.put(MANAGE_INTRODUCERS, R.string.manage_introducers);
        PRIVILEGE_NAME_MAP.put(SEE_INVITATIONS, R.string.see_invitations);
        PRIVILEGE_NAME_MAP.put(MANAGE_INVITATIONS, R.string.manage_invitations);
        PRIVILEGE_NAME_MAP.put(MANAGE_ADDRESS, R.string.manage_address);
        PRIVILEGE_NAME_MAP.put(SEE_PROFILE_COMPLETION, R.string.see_profile_completion);
    }
}
