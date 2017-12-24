package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class TransactionHistory implements Parcelable {
    private final long insertTime;
    private final String accountId;
    private final int serviceId;
    private final String transactionId;
    private final String message;
    private final double amount;
    private final double fee;
    private final double netAmount;
    private final String purpose;
    private final String description;
    private final String shortDesc;
    private final int statusCode;
    private final String statusInWord;
    private final String type;
    private final Double accountBalance;
    private final Double availableBalance;
    private final TransactionHistoryAdditionalInfo otherParty;
    private final String[] actions = null;

    public String getOriginatingMobileNumber() {
        return ProfileInfoCacheManager.getMobileNumber();
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
        return otherParty.getMobileNumber();
    }

    public double getNetAmount() {
        return netAmount;
    }

    public String getNetAmountFormatted() {
        if (type.equals(Constants.TRANSACTION_TYPE_CREDIT))
            return "+" + amount;
        else if (type.equals(Constants.TRANSACTION_TYPE_DEBIT))
            return "-" + amount;
        return "" + amount;
    }

    public Double getBalance() {
        return availableBalance;
    }

    public int getServiceID() {
        return serviceId;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getPurpose() {
        return purpose;
    }

    public long getTime() {
        return insertTime;
    }

    public String getTransactionID() {
        return transactionId;
    }

    public TransactionHistoryAdditionalInfo getAdditionalInfo() {
        return otherParty;
    }

    public String getDescription() {
        return description;
    }

    public String getShortDescription() {
        if (shortDesc != null)
            return shortDesc;
        return "No Information Available";
    }

    public long getInsertTime() {
        return insertTime;
    }

    public String getAccountId() {
        return accountId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getMessage() {
        return message;
    }

    public String getStatusInWord() {
        return statusInWord;
    }

    public String getType() {
        return type;
    }

    public Double getAccountBalance() {
        return accountBalance;
    }

    public Double getAvailableBalance() {
        return availableBalance;
    }

    public TransactionHistoryAdditionalInfo getOtherParty() {
        return otherParty;
    }

    public String[] getActions() {
        return actions;
    }

    public String getStatus() {
        return statusInWord;
    }

    @Override
    public String toString() {
        return "TransactionHistory{" +
                "insertTime=" + insertTime +
                ", accountId='" + accountId + '\'' +
                ", serviceId=" + serviceId +
                ", transactionId='" + transactionId + '\'' +
                ", message='" + message + '\'' +
                ", amount=" + amount +
                ", fee=" + fee +
                ", netAmount=" + netAmount +
                ", purpose='" + purpose + '\'' +
                ", description='" + description + '\'' +
                ", statusCode=" + statusCode +
                ", statusInWord='" + statusInWord + '\'' +
                ", type='" + type + '\'' +
                ", accountBalance=" + accountBalance +
                ", availableBalance=" + availableBalance +
                ", otherParty=" + otherParty +
                ", actions=" + Arrays.toString(actions) +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.amount);
        dest.writeDouble(this.fee);
        dest.writeDouble(this.netAmount);
        if (this.availableBalance != null)
            dest.writeDouble(this.availableBalance);
        else {
            Double availableBalance = new Double(0.0);
            dest.writeDouble(availableBalance);
        }
        if (this.accountBalance != null)
            dest.writeDouble(this.accountBalance);
        else {
            Double accountBalance = new Double(0.0);
            dest.writeDouble(accountBalance);
        }
        dest.writeInt(this.serviceId);
        dest.writeInt(this.statusCode);
        dest.writeString(this.purpose);
        dest.writeString(this.statusInWord);
        dest.writeString(this.description);
        dest.writeString(this.shortDesc);
        dest.writeString(this.transactionId);
        dest.writeString(this.accountId);
        dest.writeLong(this.insertTime);
        dest.writeString(this.type);
        dest.writeParcelable(this.otherParty, flags);
        if (actions != null)
            dest.writeArray(this.actions);
        dest.writeString(this.message);
    }

    protected TransactionHistory(Parcel in) {
        this.amount = in.readDouble();
        this.fee = in.readDouble();
        this.netAmount = in.readDouble();
        this.availableBalance = in.readDouble();
        this.accountBalance = in.readDouble();
        this.serviceId = in.readInt();
        this.statusCode = in.readInt();
        this.purpose = in.readString();
        this.statusInWord = in.readString();
        this.description = in.readString();
        this.shortDesc = in.readString();
        this.transactionId = in.readString();
        this.insertTime = in.readLong();
        this.accountId = in.readString();
        this.message = in.readString();
        this.type = in.readString();
        this.otherParty = in.readParcelable(TransactionHistoryAdditionalInfo.class.getClassLoader());
        if (this.actions != null)
            in.readStringArray(actions);
    }

    public static final Parcelable.Creator<TransactionHistory> CREATOR = new Parcelable.Creator<TransactionHistory>() {
        @Override
        public TransactionHistory createFromParcel(Parcel source) {
            return new TransactionHistory(source);
        }

        @Override
        public TransactionHistory[] newArray(int size) {
            return new TransactionHistory[size];
        }
    };
}
