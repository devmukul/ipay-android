package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory;

import android.content.Context;
import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;

import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Education.EducationPaymentDetails;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class TransactionHistoryAdditionalInfo implements Parcelable {
    private String userName;
    private String userMobileNumber;
    private String userProfilePic;
    private String bankAccountNumber;
    private String bankAccountName;
    private String bankName;
    private String branchName;
    private String bankCode;
    private String cardHolderName;
    private String cardNumber;
    private boolean isReceiver;
    private EducationPaymentDetails educationPayment;

    public TransactionHistoryAdditionalInfo() {

    }

    protected TransactionHistoryAdditionalInfo(Parcel in) {
        userName = in.readString();
        userMobileNumber = in.readString();
        userProfilePic = in.readString();
        bankAccountNumber = in.readString();
        bankAccountName = in.readString();
        bankName = in.readString();
        branchName = in.readString();
        bankCode = in.readString();
        cardHolderName = in.readString();
        cardNumber = in.readString();
        isReceiver = in.readByte() != 0;
        educationPayment = in.readParcelable(EducationPaymentDetails.class.getClassLoader());
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

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public int getBankIcon(Context context) {
        Resources resources = context.getResources();
        return resources.getIdentifier("ic_bank" + getBankCode(), "drawable",
                context.getPackageName());
    }

    public int getCardIcon() {
        if (cardNumber != null) {
            if (cardNumber.matches(Constants.VISA_CARD_STARTS_WITH_REGEX)) {
                return R.drawable.visa;
            } else if (cardNumber.matches(Constants.AMEX_CARD_STARTS_WITH_REGEX)) {
                return R.drawable.amex;
            } else if (cardNumber.matches(Constants.MASTER_CARD_STARTS_WITH_REGEX)) {
                return R.drawable.mastercard;
            } else {
                return R.drawable.basic_card;
            }
        } else {
            return R.drawable.basic_card;
        }
    }

    public boolean isReceiver() {
        return isReceiver;
    }

    public EducationPaymentDetails getEducationPayment() {
        return educationPayment;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TransactionHistoryAdditionalInfo> CREATOR = new Creator<TransactionHistoryAdditionalInfo>() {
        @Override
        public TransactionHistoryAdditionalInfo createFromParcel(Parcel in) {
            return new TransactionHistoryAdditionalInfo(in);
        }

        @Override
        public TransactionHistoryAdditionalInfo[] newArray(int size) {
            return new TransactionHistoryAdditionalInfo[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        dest.writeString(userMobileNumber);
        dest.writeString(userProfilePic);
        dest.writeString(bankAccountNumber);
        dest.writeString(bankAccountName);
        dest.writeString(bankName);
        dest.writeString(branchName);
        dest.writeString(bankCode);
        dest.writeString(cardHolderName);
        dest.writeString(cardNumber);
        dest.writeByte((byte) (isReceiver ? 1 : 0));
        dest.writeParcelable(educationPayment, flags);
    }
}
