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
            case (Constants.TRANSACTION_HISTORY_TOPUP):
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
            case (Constants.TRANSACTION_HISTORY_TOPUP):
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

//    const processTransactionLists = (transactions = []) => {
//        const LENGTH = transactions.length;
//        let temp = [];
//        if (LENGTH > 0) {
//            const {TransactionServiceID} = AppSettings;
//            const {mobileNumber} = $localStorage;
//            let transaction={}, service, time, item={};
//
//            for (let i = 0; i < LENGTH; i++) {
//                transaction = transactions[i];
//                service = TransactionServiceID[transaction.serviceID];
//                time = $filter('date')(transaction.time, 'MMM d, y - h:mm a');
//                switch (service) {
//                    case "OPENING_BALANCE":
//                        temp.push({
//                                time,
//                                description: 'Opening Balance from iPay',
//                            amount: transaction.effectiveAmount,
//                            status: getStatus(transaction.statusCode),
//                            currencyFilter: '+ Tk. '
//                        });
//                        break;
//                    case "SEND_MONEY":
//                        item = {
//                                time,
//                                status: getStatus(transaction.statusCode)
//                        };
//                        if (transaction.originatingMobileNumber === mobileNumber) {
//                            item.description = `Money send to ${transaction.receiverInfo}`;
//                            item.amount = transaction.originatingAmount;
//                            item.currencyFilter = '- Tk. ';
//                        } else if (transaction.receiverInfo === mobileNumber) {
//                            item.description = `Money received from ${transaction.originatingMobileNumber}`;
//                            item.amount = transaction.effectiveAmount;
//                            item.currencyFilter = '+ Tk. ';
//                        } else {
//                            item.description = `No information available ...`;
//                            item.amount = 0;
//                            item.currencyFilter = 'Tk. ';
//                        }
//                        temp.push(item);
//                        break;
//                    case "ADD_MONEY":
//                        temp.push({
//                                time,
//                                description: `Money transfer from ${transaction.receiverInfo} to iPay`,
//                    amount: transaction.effectiveAmount,
//                            status: getStatus(transaction.statusCode),
//                            currencyFilter: '+ Tk. '
//                    });
//                    break;
//                    case "WITHDRAW_MONEY":
//                        temp.push({
//                                time,
//                                description: `Money transfer from iPay to ${transaction.receiverInfo}`,
//                    amount: transaction.originatingAmount,
//                            status: getStatus(transaction.statusCode),
//                            currencyFilter: '- Tk. '
//                    });
//                    break;
//                    case "TOPUP":
//                        temp.push({
//                                time,
//                                description: `Mobile Top Up to ${transaction.receiverInfo}`,
//                    amount: transaction.originatingAmount,
//                            status: getStatus(transaction.statusCode),
//                            currencyFilter: '- Tk. '
//                    });
//                    break;
//                    case "PAYMENT":
//                        item = {
//                                time,
//                                status: getStatus(transaction.statusCode)
//                        };
//                        if (transaction.originatingMobileNumber === mobileNumber) {
//                            item.description = `Payment send to ${transaction.receiverInfo}`;
//                            item.amount = transaction.originatingAmount;
//                            item.currencyFilter = '- Tk. ';
//                        } else if (transaction.receiverInfo === mobileNumber) {
//                            item.description = `Payment received from ${transaction.originatingMobileNumber}`;
//                            item.amount = transaction.effectiveAmount;
//                            item.currencyFilter = '+ Tk. ';
//                        } else {
//                            item.description = `No information available ...`;
//                            item.amount = 0;
//                            item.currencyFilter = 'Tk. ';
//                        }
//                        temp.push(item);
//                        break;
//                    default:
//                        temp.push({
//                                time,
//                                description: `No information available ...`,
//                        amount: 0,
//                                status: getStatus(transaction.statusCode),
//                            currencyFilter: 'Tk. '
//                        });
//                        break;
//                }
//            }
//        }
//        return temp || [];
//    };
}
