package bd.com.ipay.ipayskeleton.Model.MMModule.ChangeCredentials;

public class PinInfoResponse {
    private String message;
    private boolean pinExists;
    private boolean isRequiredForSendMoney;
    private boolean isRequiredForRequestMoney;
    private boolean isRequiredForAddMoney;
    private boolean isRequiredForWithdrawMoney;
    private boolean isRequiredForAcceptMoneyRequest;
    private boolean isRequiredForPayment;
    private boolean isRequiredForTopup;
    private boolean isRequiredForAcceptInvoice;

    public String getMessage() {
        return message;
    }

    public boolean isPinExists() {
        return pinExists;
    }

    public boolean isRequiredForSendMoney() {
        return isRequiredForSendMoney;
    }

    public boolean isRequiredForRequestMoney() {
        return isRequiredForRequestMoney;
    }

    public boolean isRequiredForAddMoney() {
        return isRequiredForAddMoney;
    }

    public boolean isRequiredForWithdrawMoney() {
        return isRequiredForWithdrawMoney;
    }

    public boolean isRequiredForAcceptMoneyRequest() {
        return isRequiredForAcceptMoneyRequest;
    }

    public boolean isRequiredForPayment() {
        return isRequiredForPayment;
    }

    public boolean isRequiredForTopup() {
        return isRequiredForTopup;
    }

    public boolean isRequiredForAcceptInvoice() {
        return isRequiredForAcceptInvoice;
    }
}
