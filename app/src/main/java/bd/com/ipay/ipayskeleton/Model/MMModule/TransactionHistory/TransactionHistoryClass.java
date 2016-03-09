package bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory;

public class TransactionHistoryClass {

    public String receiverInfo;
    public int transactionType;
    public double effectiveAmount;
    public int serviceType;
    public String purpose;
    public Integer currencyCode;
    public Integer statusCode;
    public long time;
    public String otherUserName;
//    public Set<UserProfilePictureClass> otherUserprofilePictures = new HashSet<>();

    public TransactionHistoryClass() {
    }

    public String getReceiverInfo() {
        return receiverInfo;
    }

    public int getTransactionType() {
        return transactionType;
    }

    public double getEffectiveAmount() {
        return effectiveAmount;
    }

    public int getServiceType() {
        return serviceType;
    }

    public String getPurpose() {
        return purpose;
    }

    public Integer getCurrencyCode() {
        return currencyCode;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public long getTime() {
        return time;
    }

    public String getOtherUserName() {
        return otherUserName;
    }

//    public Set<UserProfilePictureClass> getOtherUserprofilePictures() {
//        return otherUserprofilePictures;
//    }
}
