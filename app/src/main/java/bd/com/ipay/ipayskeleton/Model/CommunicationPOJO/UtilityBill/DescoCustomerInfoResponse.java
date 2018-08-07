package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill;

public class DescoCustomerInfoResponse {
    private String accountNumber;
    private String billStatus;
    private String vatAmount;
    private String netAmount;
    private String totalPayableAmount;
    private String billNumber;
    private String message;
    private String lpc;
    private String dueDate;

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getBillStatus() {
        return billStatus;
    }

    public String getVatAmount() {
        return vatAmount;
    }

    public String getNetAmount() {
        return netAmount;
    }

    public String getTotalPayableAmount() {
        return totalPayableAmount;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public String getMessage() {
        return message;
    }

    public String getLpc() {
        return lpc;
    }

    public String getDueDate() {
        return dueDate;
    }
}
