package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Notification;

import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory.TransactionHistory;

public class FireBaseNotification {
    private double available_balance;
    private TransactionHistory transaction;

    public double getAvailable_balance() {
        return available_balance;
    }

    public TransactionHistory getTransaction() {
        return transaction;
    }
}
