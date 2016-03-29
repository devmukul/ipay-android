package bd.com.ipay.ipayskeleton.Model.MMModule.ServiceCharge;

import java.math.BigDecimal;

public class GetServiceChargeResponse {

    public BigDecimal maxTransactionFee;
    public BigDecimal perTransactionFlatFee;
    public BigDecimal perTransactionVeriableCharge;

    public GetServiceChargeResponse() {
    }

    public BigDecimal getMaxTransactionFee() {
        return maxTransactionFee;
    }

    public BigDecimal getPerTransactionFlatFee() {
        return perTransactionFlatFee;
    }

    public BigDecimal getPerTransactionVeriableCharge() {
        return perTransactionVeriableCharge;
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

    public String getServiceChargeDescription(BigDecimal amount) {
        BigDecimal calculatedServiceCharge = getPerTransactionFlatFee().add(((getPerTransactionVeriableCharge().multiply(amount))).divide(new BigDecimal(100)));
        BigDecimal actualServiceCharge;

        if (getMaxTransactionFee().compareTo(calculatedServiceCharge) > 0)
            actualServiceCharge = calculatedServiceCharge;
        else actualServiceCharge = getMaxTransactionFee();

        if (actualServiceCharge.compareTo(new BigDecimal(0)) == 0)
            return "There are no extra charges for this transaction!";
        else if (actualServiceCharge.compareTo(new BigDecimal(0)) > 0)
            return "You'll be charged " + actualServiceCharge + " Tk. for this transaction.";
        else return null;
    }
}
