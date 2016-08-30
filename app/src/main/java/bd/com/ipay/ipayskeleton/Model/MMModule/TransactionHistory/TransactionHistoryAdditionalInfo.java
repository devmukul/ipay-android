package bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory;

import android.content.Context;
import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;

import bd.com.ipay.ipayskeleton.Model.MMModule.Education.EducationPaymentDetails;

public class TransactionHistoryAdditionalInfo implements Parcelable {
    private String userName;
    private String userMobileNumber;
    private String userProfilePic;
    private String bankAccountNumber;
    private String bankAccountName;
    private String bankName;
    private String branchName;
    private String bankCode;
    private EducationPaymentDetails educationPayment;

    public TransactionHistoryAdditionalInfo(String userName, String userMobileNumber, String userProfilePic,
                String bankAccountNumber, String bankAccountName, String bankName, String branchName, String bankCode) {
        this.userName = userName;
        this.userMobileNumber = userMobileNumber;
        this.userProfilePic = userProfilePic;
        this.bankAccountNumber = bankAccountNumber;
        this.bankAccountName = bankAccountName;
        this.bankName = bankName;
        this.branchName = branchName;
        this.bankCode = bankCode;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserMobileNumber() {
        return userMobileNumber;
    }

    public String getUserProfilePic() {
        return userProfilePic;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public String getBankAccountName() {
        return bankAccountName;
    }

    public String getBankName() {
        return bankName;
    }

    public String getBranchName() {
        return branchName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public int getBankIcon(Context context) {
        Resources resources = context.getResources();
        return resources.getIdentifier("bank" + getBankCode(), "drawable",
                context.getPackageName());
    }

    public int getBankIconWithBorder(Context context) {
        Resources resources = context.getResources();
        return resources.getIdentifier("ic_bank" + getBankCode(), "drawable",
                context.getPackageName());
    }

    public EducationPaymentDetails getEducationPayment() {
        return educationPayment;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userName);
        dest.writeString(this.userMobileNumber);
        dest.writeString(this.userProfilePic);
        dest.writeString(this.bankAccountNumber);
        dest.writeString(this.bankAccountName);
        dest.writeString(this.bankName);
        dest.writeString(this.branchName);
        dest.writeString(this.bankCode);
        dest.writeParcelable(this.educationPayment, flags);
    }

    protected TransactionHistoryAdditionalInfo(Parcel in) {
        this.userName = in.readString();
        this.userMobileNumber = in.readString();
        this.userProfilePic = in.readString();
        this.bankAccountNumber = in.readString();
        this.bankAccountName = in.readString();
        this.bankName = in.readString();
        this.branchName = in.readString();
        this.bankCode = in.readString();
        this.educationPayment = in.readParcelable(EducationPaymentDetails.class.getClassLoader());
    }

    public static final Parcelable.Creator<TransactionHistoryAdditionalInfo> CREATOR = new Parcelable.Creator<TransactionHistoryAdditionalInfo>() {
        @Override
        public TransactionHistoryAdditionalInfo createFromParcel(Parcel source) {
            return new TransactionHistoryAdditionalInfo(source);
        }

        @Override
        public TransactionHistoryAdditionalInfo[] newArray(int size) {
            return new TransactionHistoryAdditionalInfo[size];
        }
    };
}
