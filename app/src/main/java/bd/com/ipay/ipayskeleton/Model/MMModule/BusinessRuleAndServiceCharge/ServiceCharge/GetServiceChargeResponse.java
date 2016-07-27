package bd.com.ipay.ipayskeleton.Model.MMModule.BusinessRuleAndServiceCharge.ServiceCharge;

import java.math.BigDecimal;

public class GetServiceChargeResponse {

    private BigDecimal maxTransactionFee;
    private BigDecimal perTransactionFlatFee;
    private BigDecimal perTransactionVeriableCharge;
    private Boolean isPinRequired;

    public GetServiceChargeResponse() {
    }

    private BigDecimal getMaxTransactionFee() {
        return maxTransactionFee;
    }

    private BigDecimal getPerTransactionFlatFee() {
        if (perTransactionFlatFee == null)
            return BigDecimal.ZERO;
        else
            return perTransactionFlatFee;
    }

    private BigDecimal getPerTransactionVeriableCharge() {
        if (perTransactionVeriableCharge == null)
            return BigDecimal.ZERO;
        else
            return perTransactionVeriableCharge;
    }

    public boolean isPinRequired() {
        if (isPinRequired == null)
            return true;
        else
            return isPinRequired;
    }

    public void setPinRequired(boolean pinRequired) {
        isPinRequired = pinRequired;
    }

    public BigDecimal getServiceCharge(BigDecimal amount) {
        try {
            BigDecimal calculatedServiceCharge = getPerTransactionFlatFee().add(((getPerTransactionVeriableCharge().multiply(amount))).divide(new BigDecimal(100)));

            if (getMaxTransactionFee() == null) return calculatedServiceCharge;
            else if (getMaxTransactionFee().compareTo(calculatedServiceCharge) > 0)
                return calculatedServiceCharge;
            else return getMaxTransactionFee();

        } catch (Exception e) {
            e.printStackTrace();
            return new BigDecimal(-1);
        }


    }
}
