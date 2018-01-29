package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule;

import java.math.BigDecimal;

public class MandatoryBusinessRules {

    private BigDecimal MAX_AMOUNT_PER_PAYMENT;
    private BigDecimal MIN_AMOUNT_PER_PAYMENT;
    private boolean PIN_REQUIRED;
    private boolean VERIFICATION_REQUIRED;
    private boolean LOCATION_REQUIRED;

    public MandatoryBusinessRules() {
        MAX_AMOUNT_PER_PAYMENT = new BigDecimal("-1");
        MIN_AMOUNT_PER_PAYMENT = new BigDecimal("-1");
        PIN_REQUIRED = true;
        VERIFICATION_REQUIRED = false;
        LOCATION_REQUIRED = false;
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

    public boolean isLOCATION_REQUIRED() {
        return LOCATION_REQUIRED;
    }

    public void setLOCATION_REQUIRED(boolean LOCATION_REQUIRED) {
        this.LOCATION_REQUIRED = LOCATION_REQUIRED;
    }
}
