package bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory;

import android.util.Log;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

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

    public String getOriginatingMobileNumber() {
        return originatingMobileNumber;
    }

    public double getAmount() {
        return amount;
    }

    public double getFee() {
        return fee;
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
        if (serviceID != Constants.TRANSACTION_HISTORY_OPENING_BALANCE && (
                originatingMobileNumber == null || receiverInfo == null))
            return "No information available";

        switch (serviceID) {
            case (Constants.TRANSACTION_HISTORY_OPENING_BALANCE):
                return "Opening balance from iPay";
            case (Constants.TRANSACTION_HISTORY_SEND_MONEY):
                if (originatingMobileNumber.equals(userMobileNumber)) {
                    if (statusCode == Constants.TRANSACTION_STATUS_ACCEPTED)
                        return "Money sent to " + additionalInfo.getUserName();
                    else if (statusCode == Constants.TRANSACTION_STATUS_PROCESSING)
                        return "Sending money to " + additionalInfo.getUserName() + " in progress";
                    else {
                        if (additionalInfo.getUserName() == null)
                            return "Money sending failed (" + statusDescription + ")";
                        else
                            return "Sending money to " + additionalInfo.getUserName() + " failed (" + statusDescription + ")";
                    }
                }
                else if (receiverInfo.equals(userMobileNumber)) {
                    if (statusCode == Constants.TRANSACTION_STATUS_ACCEPTED)
                        return "Money received from " + additionalInfo.getUserName();
                    else if (statusCode == Constants.TRANSACTION_STATUS_PROCESSING)
                        return "Money receive from " + additionalInfo.getUserName() + " in progress";
                    else {
                        if (additionalInfo.getUserName() == null)
                            return "Money receiving failed (" + statusDescription + ")";
                        else
                            return "Receiving money from " + additionalInfo.getUserName() + " failed (" + statusDescription + ")";
                    }
                }
                else {
                    return "No information available";
                }
            case (Constants.TRANSACTION_HISTORY_ADD_MONEY):
                if (statusCode == Constants.TRANSACTION_STATUS_ACCEPTED)
                    return "Transferred money from " + getBankTitle();
                else if (statusCode == Constants.TRANSACTION_STATUS_PROCESSING)
                    return "Money transfer from " + getBankTitle() + " in progress";
                else
                    return "Money transfer from " + getBankTitle() + " failed (" + statusDescription + ")";
            case (Constants.TRANSACTION_HISTORY_WITHDRAW_MONEY):
                if (statusCode == Constants.TRANSACTION_STATUS_ACCEPTED)
                    return "Transferred money to " + getBankTitle();
                else if (statusCode == Constants.TRANSACTION_STATUS_PROCESSING)
                    return "Money transfer to " + getBankTitle() + " in progress";
                else
                    return "Money transfer to " + getBankTitle() + " failed (" + statusDescription + ")";
            case (Constants.TRANSACTION_HISTORY_TOP_UP):
                if (statusCode == Constants.TRANSACTION_STATUS_ACCEPTED)
                    return "Mobile TopUp to " + receiverInfo + " successful";
                else if (statusCode == Constants.TRANSACTION_STATUS_PROCESSING)
                    return "Mobile TopUp to " + receiverInfo + " in progress";
                else
                    return "Mobile TopUp to " + receiverInfo + " failed (" + statusDescription + ")";
            case (Constants.TRANSACTION_HISTORY_PAYMENT):
                if (originatingMobileNumber.equals(userMobileNumber)) {
                    if (statusCode == Constants.TRANSACTION_STATUS_ACCEPTED)
                        return "Payment sent to " + additionalInfo.getUserName();
                    else if (statusCode == Constants.TRANSACTION_STATUS_PROCESSING)
                        return "Sending payment to " + additionalInfo.getUserName() + " in progress";
                    else {
                        if (additionalInfo.getUserName() == null)
                            return "Payment sending failed (" + statusDescription + ")";
                        else
                            return "Sending payment to " + additionalInfo.getUserName() + " failed (" + statusDescription + ")";
                    }
                }
                else if (receiverInfo.equals(userMobileNumber)) {
                    if (statusCode == Constants.TRANSACTION_STATUS_ACCEPTED)
                        return "Payment received from " + additionalInfo.getUserName();
                    else if (statusCode == Constants.TRANSACTION_STATUS_PROCESSING)
                        return "Receiving payment from " + additionalInfo.getUserName() + " in progress";
                    else {
                        if (additionalInfo.getUserName() == null)
                            return "Payment receiving failed (" + statusDescription + ")";
                        else
                            return "Receiving payment from " + additionalInfo.getUserName() + " failed (" + statusDescription + ")";
                    }
                }
                else
                    return "No information available";
        }

        return "No information available";
    }

    private String getBankTitle() {
        if (additionalInfo.getBankAccountNumber() == null)
            return "bank";
        else
            return additionalInfo.getBankAccountNumber();
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
}
