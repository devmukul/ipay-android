package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill;

public class CreditCardBillPayRequest {
    private String amount;
    private String pin;
    private String otp;
    private String creditCardNumber;
    private String cardHolderName;
    private boolean saveMyCardInfo;
    private String bankCode;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public boolean isSaveMyCardInfo() {
        return saveMyCardInfo;
    }

    public void setSaveMyCardInfo(boolean saveMyCardInfo) {
        this.saveMyCardInfo = saveMyCardInfo;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public CreditCardBillPayRequest(String amount, String pin, String otp, String creditCardNumber, String cardHolderName, boolean saveMyCardInfo, String bankCode) {

        this.amount = amount;
        this.pin = pin;
        this.otp = otp;
        this.creditCardNumber = creditCardNumber;
        this.cardHolderName = cardHolderName;
        this.saveMyCardInfo = saveMyCardInfo;
        this.bankCode = bankCode;
    }
}
