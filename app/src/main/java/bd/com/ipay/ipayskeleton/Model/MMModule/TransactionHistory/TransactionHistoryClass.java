package bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory;

import android.util.Log;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class TransactionHistoryClass {

    public String originatingMobileNumber;
    public String receiverInfo;
    public int serviceID;
    public Integer statusCode;
    public String statusDescription;
    public long time;

    public double amount;
    public double fee;
    public double netAmount;
    public double balance;
    public String purpose;
    public String description;
    public String transactionID;

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

    public String getDescription() {
        return description;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public TransactionHistoryClass() {
    }

    public String getOriginatingMobileNumber() {
        return originatingMobileNumber;
    }

    public String getReceiverInfo() {
        return receiverInfo;
    }

//    public String getDescription(String userMobileNumber) {
//        if (serviceID != Constants.TRANSACTION_HISTORY_OPENING_BALANCE && (
//                originatingMobileNumber == null || receiverInfo == null))
//            return "No information available";
//
//        switch (serviceID) {
//            case (Constants.TRANSACTION_HISTORY_OPENING_BALANCE):
//                return "Opening balance from iPay";
//            case (Constants.TRANSACTION_HISTORY_SEND_MONEY):
//                if (originatingMobileNumber.equals(userMobileNumber))
//                    return "Money sent to " + receiverInfo;
//                else if (receiverInfo.equals(userMobileNumber))
//                    return "Money received from " + originatingMobileNumber;
//                else
//                    return "No information available";
//            case (Constants.TRANSACTION_HISTORY_ADD_MONEY):
//                return "Money transfer from " + receiverInfo;
//            case (Constants.TRANSACTION_HISTORY_WITHDRAW_MONEY):
//                return "Money transfer to " + receiverInfo;
//            case (Constants.TRANSACTION_HISTORY_TOP_UP):
//                return "Mobile TopUp to " + receiverInfo;
//            case (Constants.TRANSACTION_HISTORY_PAYMENT):
//                if (originatingMobileNumber.equals(userMobileNumber))
//                    return "Payment sent to " + receiverInfo;
//                else if (receiverInfo.equals(userMobileNumber))
//                    return "Payment received from " + originatingMobileNumber;
//                else
//                    return "No information available";
//        }
//
//        return "No information available";
//    }

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

    public int getServiceID() {
        return serviceID;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public String getPurpose() {
        return purpose;
    }

    public long getTime() {
        return time;
    }
}
