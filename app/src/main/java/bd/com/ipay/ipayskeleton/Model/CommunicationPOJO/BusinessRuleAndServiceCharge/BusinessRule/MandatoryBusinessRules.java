package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule;

import java.math.BigDecimal;

import bd.com.ipay.ipayskeleton.Utilities.BusinessRuleConstants;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class MandatoryBusinessRules {

    private BigDecimal MAX_AMOUNT_PER_PAYMENT;
    private BigDecimal MIN_AMOUNT_PER_PAYMENT;
    private boolean PIN_REQUIRED;
    private boolean VERIFICATION_REQUIRED;
    private boolean LOCATION_REQUIRED;
    private String tag;

    public MandatoryBusinessRules(String tag) {
        this.tag = tag;
    }

    public void setDefaultRules() {
        switch (tag) {
            case Constants.SEND_MONEY:
                MAX_AMOUNT_PER_PAYMENT = new BigDecimal(BusinessRuleConstants.DEFAULT_VALUE_SEND_MONEY_MAX_AMOUNT_PER_PAYMENT);
                MIN_AMOUNT_PER_PAYMENT = new BigDecimal(BusinessRuleConstants.DEFAULT_VALUE_SEND_MONEY_MIN_AMOUNT_PER_PAYMENT);
                PIN_REQUIRED = BusinessRuleConstants.DEFAULT_VALUE_SEND_MONEY_PIN_REQUIRED;
                VERIFICATION_REQUIRED = BusinessRuleConstants.DEFAULT_VALUE_SEND_MONEY_VERIFICATION_REQUIRED;
                LOCATION_REQUIRED = false;
                break;
            case Constants.REQUEST_MONEY:
                MAX_AMOUNT_PER_PAYMENT = new BigDecimal(BusinessRuleConstants.DEFAULT_VALUE_REQUEST_MONEY_MAX_AMOUNT_PER_PAYMENT);
                MIN_AMOUNT_PER_PAYMENT = new BigDecimal(BusinessRuleConstants.DEFAULT_VALUE_REQUEST_MONEY_MIN_AMOUNT_PER_PAYMENT);
                PIN_REQUIRED = BusinessRuleConstants.DEFAULT_VALUE_REQUEST_MONEY_PIN_REQUIRED;
                VERIFICATION_REQUIRED = BusinessRuleConstants.DEFAULT_VALUE_REQUEST_MONEY_VERIFICATION_REQUIRED;
                LOCATION_REQUIRED = false;
                break;
            case Constants.ADD_MONEY_BY_BANK:
                MAX_AMOUNT_PER_PAYMENT = new BigDecimal(BusinessRuleConstants.DEFAULT_VALUE_ADD_MONEY_BY_BANK_MAX_AMOUNT_PER_PAYMENT);
                MIN_AMOUNT_PER_PAYMENT = new BigDecimal(BusinessRuleConstants.DEFAULT_VALUE_ADD_MONEY_BY_BANK_MIN_AMOUNT_PER_PAYMENT);
                PIN_REQUIRED = BusinessRuleConstants.DEFAULT_VALUE_ADD_MONEY_BY_BANK_PIN_REQUIRED;
                VERIFICATION_REQUIRED = BusinessRuleConstants.DEFAULT_VALUE_ADD_MONEY_BY_BANK_VERIFICATION_REQUIRED;
                LOCATION_REQUIRED = false;
                break;
            case Constants.ADD_MONEY_BY_CARD:
                MAX_AMOUNT_PER_PAYMENT = new BigDecimal(BusinessRuleConstants.DEFAULT_VALUE_ADD_MONEY_BY_CARD_MAX_AMOUNT_PER_PAYMENT);
                MIN_AMOUNT_PER_PAYMENT = new BigDecimal(BusinessRuleConstants.DEFAULT_VALUE_ADD_MONEY_BY_CARD_MIN_AMOUNT_PER_PAYMENT);
                PIN_REQUIRED = BusinessRuleConstants.DEFAULT_VALUE_ADD_MONEY_BY_CARD_PIN_REQUIRED;
                VERIFICATION_REQUIRED = BusinessRuleConstants.DEFAULT_VALUE_ADD_MONEY_BY_CARD_VERIFICATION_REQUIRED;
                LOCATION_REQUIRED = false;
                break;
            case Constants.WITHDRAW_MONEY:
                MAX_AMOUNT_PER_PAYMENT = new BigDecimal(BusinessRuleConstants.DEFAULT_VALUE_WITHDRAW_MONEY_MAX_AMOUNT_PER_PAYMENT);
                MIN_AMOUNT_PER_PAYMENT = new BigDecimal(BusinessRuleConstants.DEFAULT_VALUE_WITHDRAW_MONEY_MIN_AMOUNT_PER_PAYMENT);
                PIN_REQUIRED = BusinessRuleConstants.DEFAULT_VALUE_WITHDRAW_MONEY_PIN_REQUIRED;
                VERIFICATION_REQUIRED = BusinessRuleConstants.DEFAULT_VALUE_WITHDRAW_MONEY_VERIFICATION_REQUIRED;
                LOCATION_REQUIRED = false;
                break;
            case Constants.MAKE_PAYMENT:
                MAX_AMOUNT_PER_PAYMENT = new BigDecimal(BusinessRuleConstants.DEFAULT_VALUE_MAKE_PAYMENT_MAX_AMOUNT_PER_PAYMENT);
                MIN_AMOUNT_PER_PAYMENT = new BigDecimal(BusinessRuleConstants.DEFAULT_VALUE_MAKE_PAYMENT_MIN_AMOUNT_PER_PAYMENT);
                PIN_REQUIRED = BusinessRuleConstants.DEFAULT_VALUE_MAKE_PAYMENT_PIN_REQUIRED;
                VERIFICATION_REQUIRED = BusinessRuleConstants.DEFAULT_VALUE_MAKE_PAYMENT_VERIFICATION_REQUIRED;
                LOCATION_REQUIRED = false;
                break;
            case Constants.REQUEST_PAYMENT:
                MAX_AMOUNT_PER_PAYMENT = new BigDecimal(BusinessRuleConstants.DEFAULT_VALUE_REQUEST_PAYMENT_MAX_AMOUNT_PER_PAYMENT);
                MIN_AMOUNT_PER_PAYMENT = new BigDecimal(BusinessRuleConstants.DEFAULT_VALUE_REQUEST_PAYMENT_MIN_AMOUNT_PER_PAYMENT);
                PIN_REQUIRED = BusinessRuleConstants.DEFAULT_VALUE_REQUEST_PAYMENT_PIN_REQUIRED;
                VERIFICATION_REQUIRED = BusinessRuleConstants.DEFAULT_VALUE_REQUEST_PAYMENT_VERIFICATION_REQUIRED;
                LOCATION_REQUIRED = false;
                break;
            case Constants.TOP_UP:
                MAX_AMOUNT_PER_PAYMENT = new BigDecimal(BusinessRuleConstants.DEFAULT_VALUE_TOP_UP_MAX_AMOUNT_PER_PAYMENT);
                MIN_AMOUNT_PER_PAYMENT = new BigDecimal(BusinessRuleConstants.DEFAULT_VALUE_TOP_UP_MIN_AMOUNT_PER_PAYMENT);
                PIN_REQUIRED = BusinessRuleConstants.DEFAULT_VALUE_TOP_UP_PIN_REQUIRED;
                VERIFICATION_REQUIRED = BusinessRuleConstants.DEFAULT_VALUE_TOP_UP_VERIFICATION_REQUIRED;
                LOCATION_REQUIRED = false;
                break;
            default:
                MAX_AMOUNT_PER_PAYMENT = new BigDecimal("0");
                MIN_AMOUNT_PER_PAYMENT = new BigDecimal("10000");
                PIN_REQUIRED = true;
                VERIFICATION_REQUIRED = false;
                LOCATION_REQUIRED = false;
                break;
        }
    }

    public BigDecimal getMAX_AMOUNT_PER_PAYMENT() {
        return MAX_AMOUNT_PER_PAYMENT;
    }

    public void setMAX_AMOUNT_PER_PAYMENT(BigDecimal MAX_AMOUNT_PER_PAYMENT) {
        this.MAX_AMOUNT_PER_PAYMENT = MAX_AMOUNT_PER_PAYMENT;
    }

    public BigDecimal getMIN_AMOUNT_PER_PAYMENT() {
        return MIN_AMOUNT_PER_PAYMENT;
    }

    public void setMIN_AMOUNT_PER_PAYMENT(BigDecimal MIN_AMOUNT_PER_PAYMENT) {
        this.MIN_AMOUNT_PER_PAYMENT = MIN_AMOUNT_PER_PAYMENT;
    }

    public boolean IS_PIN_REQUIRED() {
        return PIN_REQUIRED;
    }

    public void setPIN_REQUIRED(BigDecimal PIN_REQUIRED) {
        if (PIN_REQUIRED.compareTo(BigDecimal.ZERO) > 0)
            this.PIN_REQUIRED = true;
        else
            this.PIN_REQUIRED = false;
    }

    public boolean isVERIFICATION_REQUIRED() {
        return VERIFICATION_REQUIRED;
    }

    public void setVERIFICATION_REQUIRED(BigDecimal VERIFICATION_REQUIRED) {
        if (VERIFICATION_REQUIRED.compareTo(BigDecimal.ZERO) > 0)
            this.VERIFICATION_REQUIRED = true;
        else
            this.VERIFICATION_REQUIRED = false;
    }

    public boolean IS_LOCATION_REQUIRED() {
        return LOCATION_REQUIRED;
    }

    public void setLOCATION_REQUIRED(BigDecimal LOCATION_REQUIRED) {
        if (LOCATION_REQUIRED.compareTo(BigDecimal.ZERO) > 0)
            this.LOCATION_REQUIRED = true;
        else
            this.LOCATION_REQUIRED = false;
    }
}
