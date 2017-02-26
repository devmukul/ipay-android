package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule;

import java.math.BigDecimal;

public class MandatoryBusinessRules {

    private BigDecimal MAX_AMOUNT_PER_PAYMENT;
    private BigDecimal MIN_AMOUNT_PER_PAYMENT;
    private boolean IS_PIN_REQUIRED;

    public MandatoryBusinessRules() {
        MAX_AMOUNT_PER_PAYMENT = new BigDecimal("-1");
        MIN_AMOUNT_PER_PAYMENT = new BigDecimal("-1");
        IS_PIN_REQUIRED = true;
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
        return IS_PIN_REQUIRED;
    }

    public void setIS_PIN_REQUIRED(boolean IS_PIN_REQUIRED) {
        this.IS_PIN_REQUIRED = IS_PIN_REQUIRED;
    }
}
