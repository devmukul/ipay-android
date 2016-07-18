package bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory;

import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class TransactionHistoryClass {

    private String originatingMobileNumber;
    private String receiverInfo;
    private double amount;
    private double fee;
    private double netAmount;
    private double balance;
    private int serviceID;
    private int statusCode;
    private String purpose;
    private String statusDescription;
    private String description;
    private String transactionID;
    private long time;
    private long requestTime;
    private long responseTime;
    private TransactionHistoryAdditionalInfo additionalInfo;

    public TransactionHistoryClass(String originatingMobileNumber, String receiverInfo, double amount,
                                   double fee, double netAmount, double balance, int serviceID, int statusCode,
                                   String purpose, String statusDescription, String description,
                                   String transactionID, long time, long requestTime, long responseTime,
                                   TransactionHistoryAdditionalInfo additionalInfo) {
        this.originatingMobileNumber = originatingMobileNumber;
        this.receiverInfo = receiverInfo;
        this.amount = amount;
        this.fee = fee;
        this.netAmount = netAmount;
        this.balance = balance;
        this.serviceID = serviceID;
        this.statusCode = statusCode;
        this.purpose = purpose;
        this.statusDescription = statusDescription;
        this.description = description;
        this.transactionID = transactionID;
        this.time = time;
        this.requestTime = requestTime;
        this.responseTime = responseTime;
        this.additionalInfo = additionalInfo;
    }

    public String getOriginatingMobileNumber() {
        return originatingMobileNumber;
    }

    public double getAmount() {
        return amount;
    }

    public double getFee() {
        return fee;
    }

    /**
     * @return The name of the receiver if found, otherwise the mobile number. For add money/withdraw
     * money, returns the bank name.
     */
    public String getReceiver() {
        if (serviceID != Constants.TRANSACTION_HISTORY_OPENING_BALANCE && originatingMobileNumber == null)
            return "";

        switch (serviceID) {
            case (Constants.TRANSACTION_HISTORY_TOP_UP_ROLLBACK):
                return getPhoneNumber(description);
            case (Constants.TRANSACTION_HISTORY_WITHDRAW_MONEY_ROLL_BACK):
                return getBankName();
            case (Constants.TRANSACTION_HISTORY_OPENING_BALANCE):
                return "";
            case (Constants.TRANSACTION_HISTORY_SEND_MONEY):
                if (additionalInfo != null)
                    return additionalInfo.getUserName();
                else
                    return "";
            case (Constants.TRANSACTION_HISTORY_ADD_MONEY):
                return getBankName();
            case (Constants.TRANSACTION_HISTORY_WITHDRAW_MONEY):
                return getBankName();
            case (Constants.TRANSACTION_HISTORY_TOP_UP):
                return receiverInfo;
            case (Constants.SERVICE_ID_REQUEST_INVOICE):
            case (Constants.TRANSACTION_HISTORY_PAYMENT):
                if (additionalInfo != null)
                    return additionalInfo.getUserName();
                else
                    return "";
        }

        return "";
    }

    public String getNetAmountFormatted(String userMobileNumber) {
        if (serviceID != Constants.TRANSACTION_HISTORY_OPENING_BALANCE && originatingMobileNumber == null || receiverInfo == null)
            return Utilities.formatTakaWithComma(netAmount);

            switch (serviceID) {
                case (Constants.TRANSACTION_HISTORY_TOP_UP_ROLLBACK):
                    return "+" + Utilities.formatTakaWithComma(netAmount);
                case (Constants.TRANSACTION_HISTORY_WITHDRAW_MONEY_ROLL_BACK):
                    return "+" + Utilities.formatTakaWithComma(netAmount);
                case (Constants.TRANSACTION_HISTORY_OPENING_BALANCE):
                    return "+" + Utilities.formatTakaWithComma(netAmount);
                case (Constants.TRANSACTION_HISTORY_SEND_MONEY):
                    if (originatingMobileNumber.equals(userMobileNumber)) {
                        return "+" + Utilities.formatTakaWithComma(netAmount);
                    }
                    else if (receiverInfo.equals(userMobileNumber)) {
                        return "-" + Utilities.formatTakaWithComma(netAmount);
                    }
                case (Constants.TRANSACTION_HISTORY_ADD_MONEY):
                    return "+" + Utilities.formatTakaWithComma(netAmount);
                case (Constants.TRANSACTION_HISTORY_WITHDRAW_MONEY):
                    return "-" + Utilities.formatTakaWithComma(netAmount);
                case (Constants.TRANSACTION_HISTORY_TOP_UP):
                    return "-" + Utilities.formatTakaWithComma(netAmount);
                case (Constants.SERVICE_ID_REQUEST_INVOICE):
                case (Constants.TRANSACTION_HISTORY_PAYMENT):
                    if (originatingMobileNumber.equals(userMobileNumber)) {
                        return "+" + Utilities.formatTakaWithComma(netAmount);
                    }
                    else if (receiverInfo.equals(userMobileNumber)) {
                        return "-" + Utilities.formatTakaWithComma(netAmount);
                    }
            }

            return  Utilities.formatTakaWithComma(netAmount);
        }

    public double getNetAmount() {
        return netAmount;
    }

    public double getBalance() {
        return balance;
    }

    public int getServiceID() {
        return serviceID;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public String getDescription() {
        return description;
    }

    public long getTime() {
        return time;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public long getRequestTime() {
        return requestTime;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public TransactionHistoryAdditionalInfo getAdditionalInfo() {
        return additionalInfo;
    }

    public String getReceiverInfo() {
        return receiverInfo;
    }

    public String getDescription(String userMobileNumber) {
        if (serviceID != Constants.TRANSACTION_HISTORY_OPENING_BALANCE && originatingMobileNumber == null )
            return "No information available";

        switch (serviceID) {
            case (Constants.TRANSACTION_HISTORY_TOP_UP_ROLLBACK):
                return "Top up failed to " + getPhoneNumber(description) + ", " + "returned " + Utilities.formatTaka(getNetAmount());
            case (Constants.TRANSACTION_HISTORY_WITHDRAW_MONEY_ROLL_BACK):
                return "Withdraw money failed to " + getPhoneNumber(description) + ", " + "returned " + Utilities.formatTaka(getNetAmount());
            case (Constants.TRANSACTION_HISTORY_OPENING_BALANCE):
                return "Opening balance from iPay";
            case (Constants.TRANSACTION_HISTORY_SEND_MONEY):
                if (originatingMobileNumber.equals(userMobileNumber)) {
                    if (statusCode == Constants.TRANSACTION_STATUS_ACCEPTED)
                        return "Sent " + Utilities.formatTaka(getNetAmount()) + " to " + additionalInfo.getUserName() + " (Successful)";
                    else if (statusCode == Constants.TRANSACTION_STATUS_PROCESSING)
                        return "Sending " + Utilities.formatTaka(getNetAmount()) + " to " + additionalInfo.getUserName() + " (In progress)";
                    else {
                        if (additionalInfo.getUserName() == null)
                            return "Failed to send money (" + statusDescription + ")";
                        else
                            return "Failed to send " + Utilities.formatTaka(getNetAmount()) + " to " + additionalInfo.getUserName() + " (" + statusDescription + ")";
                    }
                }
                else if (receiverInfo.equals(userMobileNumber)) {
                    if (statusCode == Constants.TRANSACTION_STATUS_ACCEPTED)
                        return "Received "+ Utilities.formatTaka(getNetAmount()) + " from " + additionalInfo.getUserName() + " (Successful)";
                    else if (statusCode == Constants.TRANSACTION_STATUS_PROCESSING)
                        return "Receiving "+ Utilities.formatTaka(getNetAmount()) + " from " + additionalInfo.getUserName() + " (in progress)";
                    else {
                        if (additionalInfo.getUserName() == null)
                            return "Failed to receive money (" + statusDescription + ")";
                        else
                            return "Failed to receive " + Utilities.formatTaka(getNetAmount()) + " from " + additionalInfo.getUserName() + " (" + statusDescription + ")";
                    }
                }
                else {
                    return "No information available";
                }
            case (Constants.TRANSACTION_HISTORY_ADD_MONEY):
                if (statusCode == Constants.TRANSACTION_STATUS_ACCEPTED)
                    return "Added " + Utilities.formatTaka(getNetAmount()) + "  from account " + getBankAccountNumber() + ", " + getBankName() + "(" + getBankBranch() + ")" + " (successful)";
                else if (statusCode == Constants.TRANSACTION_STATUS_PROCESSING)
                    return "Adding " + Utilities.formatTaka(getNetAmount()) + "  from account " + getBankAccountNumber() + ", " + getBankName() + "(" + getBankBranch() + ")" + " (in progress)";
                else
                    return "Failed to add " + Utilities.formatTaka(getNetAmount()) + " from account " + getBankAccountNumber() + ", " + getBankName() + "(" + getBankBranch() + ")" + " (" + statusDescription + ")";
            case (Constants.TRANSACTION_HISTORY_WITHDRAW_MONEY):
                if (statusCode == Constants.TRANSACTION_STATUS_ACCEPTED)
                    return "Transferred " + Utilities.formatTaka(getNetAmount()) + " to account " + getBankAccountNumber() + ", " + getBankName() + "(" + getBankBranch() + ")" +" (successful)";
                else if (statusCode == Constants.TRANSACTION_STATUS_PROCESSING)
                    return "Transferred " + Utilities.formatTaka(getNetAmount()) + " to account " + getBankAccountNumber() + ", " + getBankName() + "(" + getBankBranch() + ")" + " (in progress)";
                else
                    return "Failed to transfer " + Utilities.formatTaka(getNetAmount()) + " to account " + getBankAccountNumber() + ", " + getBankName() + "(" + getBankBranch() + ")" + " (" + statusDescription + ")";
            case (Constants.TRANSACTION_HISTORY_TOP_UP):
                if (statusCode == Constants.TRANSACTION_STATUS_ACCEPTED)
                    return "Mobile TopUp of " + Utilities.formatTaka(getNetAmount()) + " to " + receiverInfo + " (successful)";
                else if (statusCode == Constants.TRANSACTION_STATUS_PROCESSING)
                    return "Mobile TopUp of " + Utilities.formatTaka(getNetAmount()) + " to " + receiverInfo + " (in progress)";
                else
                    return "Mobile TopUp of " + Utilities.formatTaka(getNetAmount()) + " to " + receiverInfo + " failed (" + statusDescription + ")";
            case (Constants.SERVICE_ID_REQUEST_INVOICE):
            case (Constants.TRANSACTION_HISTORY_PAYMENT):
                if (originatingMobileNumber.equals(userMobileNumber)) {
                    if (statusCode == Constants.TRANSACTION_STATUS_ACCEPTED)
                        return "Payment of " + Utilities.formatTaka(getNetAmount()) + " sent to " + additionalInfo.getUserName() + " (Successful)";
                    else if (statusCode == Constants.TRANSACTION_STATUS_PROCESSING)
                        return "Sending payment of " + Utilities.formatTaka(getNetAmount()) + " to " + additionalInfo.getUserName() + " (In progress)";
                    else {
                        if (additionalInfo.getUserName() == null)
                            return "Payment sending failed (" + statusDescription + ")";
                        else
                            return "Sending payment of " + Utilities.formatTaka(getNetAmount()) + "  to " + additionalInfo.getUserName() + " failed (" + statusDescription + ")";
                    }
                }
                else if (receiverInfo.equals(userMobileNumber)) {
                    if (statusCode == Constants.TRANSACTION_STATUS_ACCEPTED)
                        return "Payment of " + Utilities.formatTaka(getNetAmount()) + " received from " + additionalInfo.getUserName() + " (Successful)";
                    else if (statusCode == Constants.TRANSACTION_STATUS_PROCESSING)
                        return "Receiving payment of " + Utilities.formatTaka(getNetAmount()) + " from " + additionalInfo.getUserName() + " (in progress)";
                    else {
                        if (additionalInfo.getUserName() == null)
                            return "Payment receiving failed (" + statusDescription + ")";
                        else
                            return "Receiving payment of " + Utilities.formatTaka(getNetAmount()) + "  from " + additionalInfo.getUserName() + " failed (" + statusDescription + ")";
                    }
                }
                else
                    return "No information available";
        }

        return "No information available";
    }

    public String getShortDescription(String userMobileNumber) {
        if (serviceID != Constants.TRANSACTION_HISTORY_OPENING_BALANCE && originatingMobileNumber == null)
            return "No Information Available";

        switch (serviceID) {
            case (Constants.TRANSACTION_HISTORY_TOP_UP_ROLLBACK):
                return "TopUp Rollback";
            case (Constants.TRANSACTION_HISTORY_WITHDRAW_MONEY_ROLL_BACK):
                return "Withdraw Money";
            case (Constants.TRANSACTION_HISTORY_OPENING_BALANCE):
                return "Opening Balance";
            case (Constants.TRANSACTION_HISTORY_SEND_MONEY):
                if (originatingMobileNumber.equals(userMobileNumber)) {
                    return "Send Money";
                }
                else if (receiverInfo.equals(userMobileNumber)) {
                    return "Receive Money";
                }
                else {
                    return "No Information Available";
                }
            case (Constants.TRANSACTION_HISTORY_ADD_MONEY):
                return "Add Money";
            case (Constants.TRANSACTION_HISTORY_WITHDRAW_MONEY):
                return "Withdraw Money";
            case (Constants.TRANSACTION_HISTORY_TOP_UP):
                return "Mobile TopUp";
            case (Constants.SERVICE_ID_REQUEST_INVOICE):
            case (Constants.TRANSACTION_HISTORY_PAYMENT):
                if (originatingMobileNumber.equals(userMobileNumber)) {
                    return "Make Payment";
                }
                else if (receiverInfo.equals(userMobileNumber)) {
                    return "Receive Payment";
                }
                else {
                    return "No Information Available";
                }
        }

        return "No Information Available";
    }

    private String getBankAccountNumber() {
        if (additionalInfo.getBankAccountNumber() == null)
            return "Bank";
        else
            return additionalInfo.getBankAccountNumber();
    }

    private String getBankName() {
        if (additionalInfo.getBankName() == null)
            return "Bank";
        else
            return additionalInfo.getBankName();
    }

    private String getBankBranch() {
        if (additionalInfo.getBranchName() == null)
            return "Bank";
        else
            return additionalInfo.getBranchName();
    }

    private String getPhoneNumber(String str){
        str = str.substring(str.indexOf("+") );
        return str.substring(0,14);
    }

    public double getAmount(String userMobileNumber) {
        if (serviceID != Constants.TRANSACTION_HISTORY_OPENING_BALANCE && (
                originatingMobileNumber == null || receiverInfo == null))
            return 0;

        switch (serviceID) {
            case (Constants.TRANSACTION_HISTORY_OPENING_BALANCE):
                return +netAmount;
            case (Constants.TRANSACTION_HISTORY_SEND_MONEY):
                if (originatingMobileNumber.equals(userMobileNumber))
                    return -netAmount;
                else if (receiverInfo.equals(userMobileNumber))
                    return +netAmount;
                else
                    return 0;
            case (Constants.TRANSACTION_HISTORY_ADD_MONEY):
                return +netAmount;
            case (Constants.TRANSACTION_HISTORY_WITHDRAW_MONEY):
                return -netAmount;
            case (Constants.TRANSACTION_HISTORY_TOP_UP):
                return -netAmount;
            case (Constants.SERVICE_ID_REQUEST_INVOICE):
            case (Constants.TRANSACTION_HISTORY_PAYMENT):
                if (originatingMobileNumber.equals(userMobileNumber))
                    return -netAmount;
                else if (receiverInfo.equals(userMobileNumber))
                    return +netAmount;
                else
                    return 0;
        }

        return 0;
    }

    @Override
    public String toString() {
        return "TransactionHistoryClass{" +
                "originatingMobileNumber='" + originatingMobileNumber + '\'' +
                ", receiverInfo='" + receiverInfo + '\'' +
                ", amount=" + amount +
                ", fee=" + fee +
                ", netAmount=" + netAmount +
                ", balance=" + balance +
                ", serviceID=" + serviceID +
                ", statusCode=" + statusCode +
                ", purpose='" + purpose + '\'' +
                ", statusDescription='" + statusDescription + '\'' +
                ", description='" + description + '\'' +
                ", transactionID='" + transactionID + '\'' +
                ", time=" + time +
                ", requestTime=" + requestTime +
                ", responseTime=" + responseTime +
                ", additionalInfo=" + additionalInfo +
                '}';
    }
}
