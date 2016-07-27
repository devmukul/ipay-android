package bd.com.ipay.ipayskeleton.Model.MMModule.Education;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;

import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.Resource;

public class PayableItem implements Resource, Parcelable {
    private BigDecimal instituteFee;
    private Integer institutesPayableListId;
    private PayableAccountHead payableAccountHead;
    private Integer status;

    public BigDecimal getInstituteFee() {
        return instituteFee;
    }

    public Integer getInstitutesPayableListId() {
        return institutesPayableListId;
    }

    public PayableAccountHead getPayableAccountHead() {
        return payableAccountHead;
    }

    public Integer getStatus() {
        return status;
    }

    @Override
    public int getId() {
        return payableAccountHead.getPayableAccountHeadId();
    }

    @Override
    public String getName() {
        return payableAccountHead.getName();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.getInstituteFee().doubleValue());
        dest.writeParcelable(this.getPayableAccountHead(), flags);
    }

    public PayableItem() {
    }

    protected PayableItem(Parcel in) {
        this.instituteFee = new BigDecimal(in.readDouble());
        this.payableAccountHead = in.readParcelable(PayableAccountHead.class.getClassLoader());
    }

    public static final Parcelable.Creator<PayableItem> CREATOR = new Parcelable.Creator<PayableItem>() {
        @Override
        public PayableItem createFromParcel(Parcel source) {
            return new PayableItem(source);
        }

        @Override
        public PayableItem[] newArray(int size) {
            return new PayableItem[size];
        }
    };

    public void setInstituteFee(BigDecimal instituteFee) {
        this.instituteFee = instituteFee;
    }
}
