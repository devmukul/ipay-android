package bd.com.ipay.ipayskeleton.Model.MMModule.ForgetPassword;

import android.os.Parcel;
import android.os.Parcelable;

public class TrustedOtpReceiver implements Parcelable {
    private final long personId;
    private final String name;
    private final String relationship;
    private final String mobileNumber;
    private final boolean eligibleForAccountRecovery;

    public TrustedOtpReceiver(long personId, String name, String relationship, String mobileNumber, boolean eligibleForAccountRecovery) {
        this.personId = personId;
        this.name = name;
        this.relationship = relationship;
        this.mobileNumber = mobileNumber;
        this.eligibleForAccountRecovery = eligibleForAccountRecovery;
    }

    public long getPersonId() {
        return personId;
    }

    public String getName() {
        return name;
    }

    public String getRelationship() {
        return relationship;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public boolean isEligibleForAccountRecovery() {
        return eligibleForAccountRecovery;
    }

    public static Creator<TrustedOtpReceiver> getCREATOR() {
        return CREATOR;
    }

    private TrustedOtpReceiver(Parcel src) {
        personId = src.readLong();
        name = src.readString();
        relationship = src.readString();
        mobileNumber = src.readString();
        eligibleForAccountRecovery = (src.readByte() == 1);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(personId);
        dest.writeString(name);
        dest.writeString(relationship);
        dest.writeString(mobileNumber);
        dest.writeByte((byte) (eligibleForAccountRecovery ? 1 : 0));
    }

    public static final Parcelable.Creator<TrustedOtpReceiver> CREATOR =
            new Creator<TrustedOtpReceiver>() {
        @Override
        public TrustedOtpReceiver createFromParcel(Parcel source) {
            return new TrustedOtpReceiver(source);
        }

        @Override
        public TrustedOtpReceiver[] newArray(int size) {
            return new TrustedOtpReceiver[size];
        }
    };
}
