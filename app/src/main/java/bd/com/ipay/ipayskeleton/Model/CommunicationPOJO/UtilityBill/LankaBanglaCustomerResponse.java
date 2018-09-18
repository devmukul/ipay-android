package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill;

public class LankaBanglaCustomerResponse {
    private String message;
    private String name;
    private String cardNumber;
    private String creditLimit;
    private String creditBalance;
    private String minimumPay;


    public LankaBanglaCustomerResponse(String message, String name, String cardNumber, String creditLimit, String creditBalance, String minimumPay) {
        this.message = message;
        this.name = name;
        this.cardNumber = cardNumber;
        this.creditLimit = creditLimit;
        this.creditBalance = creditBalance;
        this.minimumPay = minimumPay;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(String creditLimit) {
        this.creditLimit = creditLimit;
    }

    public String getCreditBalance() {
        return creditBalance;
    }

    public void setCreditBalance(String creditBalance) {
        this.creditBalance = creditBalance;
    }

    public String getMinimumPay() {
        return minimumPay;
    }

    public void setMinimumPay(String minimumPay) {
        this.minimumPay = minimumPay;
    }
}
