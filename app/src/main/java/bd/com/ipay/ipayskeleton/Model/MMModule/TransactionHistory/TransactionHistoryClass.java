package bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory;

import java.util.HashSet;
import java.util.Set;

import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.UserProfilePictureClass;

public class TransactionHistoryClass {

    public String otherMobileNumber;
    public int transactionType;
    public double amount;
    public int serviceType;
    public String description;
    public Integer currencyCode;
    public Integer status;
    public long time;
    public String otherUserName;
//    public Set<UserProfilePictureClass> otherUserprofilePictures = new HashSet<>();

    public TransactionHistoryClass() {
    }

    public String getOtherMobileNumber() {
        return otherMobileNumber;
    }

    public int getTransactionType() {
        return transactionType;
    }

    public double getAmount() {
        return amount;
    }

    public int getServiceType() {
        return serviceType;
    }

    public String getDescription() {
        return description;
    }

    public Integer getCurrencyCode() {
        return currencyCode;
    }

    public Integer getStatus() {
        return status;
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
