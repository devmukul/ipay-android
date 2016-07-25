package bd.com.ipay.ipayskeleton.Model.MMModule.BusinessRuleAndServiceCharge.ServiceCharge;

import java.math.BigDecimal;

public class FeeCharge {

    private int messageType;
    private int messageId;
    private int moduleId;
    private int clientIP;
    private int clientTokenId;
    private int clientUserAgent;
    private BigDecimal maxTransactionFee;
    private BigDecimal perTransactionFlatFee;
    private BigDecimal perTransactionVeriableCharge;

    public FeeCharge() {
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getModuleId() {
        return moduleId;
    }

    public void setModuleId(int moduleId) {
        this.moduleId = moduleId;
    }

    public int getClientIP() {
        return clientIP;
    }

    public void setClientIP(int clientIP) {
        this.clientIP = clientIP;
    }

    public int getClientTokenId() {
        return clientTokenId;
    }

    public void setClientTokenId(int clientTokenId) {
        this.clientTokenId = clientTokenId;
    }

    public int getClientUserAgent() {
        return clientUserAgent;
    }

    public void setClientUserAgent(int clientUserAgent) {
        this.clientUserAgent = clientUserAgent;
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
