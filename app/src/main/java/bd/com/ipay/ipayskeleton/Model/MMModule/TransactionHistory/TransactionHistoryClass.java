package bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class TransactionHistoryClass {

    public String originatingMobileNumber;
    public String receiverInfo;
    public double originatingAmount;
    public double effectiveAmount;
    public int serviceID;
    public Integer statusCode;
    public String statusDescription;
    public String purpose;
    public long time;

    public TransactionHistoryClass() {
    }

    public String getOriginatingMobileNumber() {
        return originatingMobileNumber;
    }

    public String getReceiverInfo() {
        return receiverInfo;
    }

    public double getOriginatingAmount() {
        return originatingAmount;
    }

    public double getEffectiveAmount() {
        return effectiveAmount;
    }

    public String getDescription(String userMobileNumber) {
        if (serviceID != Constants.TRANSACTION_HISTORY_OPENING_BALANCE && (
                originatingMobileNumber == null || receiverInfo == null))
            return "No information available";

        switch (serviceID) {
            case (Constants.TRANSACTION_HISTORY_OPENING_BALANCE):
                return "Opening balance from iPay";
            case (Constants.TRANSACTION_HISTORY_SEND_MONEY):
                if (originatingMobileNumber.equals(userMobileNumber))
                    return "Money sent to " + receiverInfo;
                else if (receiverInfo.equals(userMobileNumber))
                    return "Money received from " + originatingMobileNumber;
                else
                    return "No information available";
            case (Constants.TRANSACTION_HISTORY_ADD_MONEY):
                return "Money transfer from " + receiverInfo;
            case (Constants.TRANSACTION_HISTORY_WITHDRAW_MONEY):
                return "Money transfer to " + receiverInfo;
            case (Constants.TRANSACTION_HISTORY_TOP_UP):
                return "Mobile TopUp to " + receiverInfo;
            case (Constants.TRANSACTION_HISTORY_PAYMENT):
                if (originatingMobileNumber.equals(userMobileNumber))
                    return "Payment sent to " + receiverInfo;
                else if (receiverInfo.equals(userMobileNumber))
                    return "Payment received from " + originatingMobileNumber;
                else
                    return "No information available";
        }

        return "No information available";
    }

    public double getAmount(String userMobileNumber) {
        if (serviceID != Constants.TRANSACTION_HISTORY_OPENING_BALANCE && (
                originatingMobileNumber == null || receiverInfo == null))
            return 0;

        switch (serviceID) {
            case (Constants.TRANSACTION_HISTORY_OPENING_BALANCE):
                return +effectiveAmount;
            case (Constants.TRANSACTION_HISTORY_SEND_MONEY):
                if (originatingMobileNumber.equals(userMobileNumber))
                    return -originatingAmount;
                else if (receiverInfo.equals(userMobileNumber))
                    return +effectiveAmount;
                else
                    return 0;
            case (Constants.TRANSACTION_HISTORY_ADD_MONEY):
                return +effectiveAmount;
            case (Constants.TRANSACTION_HISTORY_WITHDRAW_MONEY):
                return -originatingAmount;
            case (Constants.TRANSACTION_HISTORY_TOP_UP):
                return -originatingAmount;
            case (Constants.TRANSACTION_HISTORY_PAYMENT):
                if (originatingMobileNumber.equals(userMobileNumber))
                    return -originatingAmount;
                else if (receiverInfo.equals(userMobileNumber))
                    return +effectiveAmount;
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
