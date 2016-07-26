package bd.com.ipay.ipayskeleton.Model.MMModule.Education;

import android.os.Parcel;
import android.os.Parcelable;

public class InvoicePayableAccountRelation implements Parcelable {
    public Integer invoicePayableAccountRelation;
    public PayableAccountHead payableAccountHead;
    public Double fee;

    public Integer getInvoicePayableAccountRelation() {
        return invoicePayableAccountRelation;
    }

    public PayableAccountHead getPayableAccountHead() {
        return payableAccountHead;
    }

    public Double getFee() {
        return fee;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.invoicePayableAccountRelation);
        dest.writeParcelable(this.payableAccountHead, flags);
        dest.writeValue(this.fee);
    }

    public InvoicePayableAccountRelation() {
    }

    protected InvoicePayableAccountRelation(Parcel in) {
        this.invoicePayableAccountRelation = (Integer) in.readValue(Integer.class.getClassLoader());
        this.payableAccountHead = in.readParcelable(PayableAccountHead.class.getClassLoader());
        this.fee = (Double) in.readValue(Double.class.getClassLoader());
    }

    public static final Parcelable.Creator<InvoicePayableAccountRelation> CREATOR = new Parcelable.Creator<InvoicePayableAccountRelation>() {
        @Override
        public InvoicePayableAccountRelation createFromParcel(Parcel source) {
            return new InvoicePayableAccountRelation(source);
        }

        @Override
        public InvoicePayableAccountRelation[] newArray(int size) {
            return new InvoicePayableAccountRelation[size];
        }
    };
}
