package bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;

public class ItemList implements Parcelable {
    private Long id;
    private BigDecimal rate;
    private BigDecimal quantity;
    private String item;
    private String description;
    private BigDecimal amount;

    public ItemList() {
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public String getItem() {
        return item;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeSerializable(this.rate);
        dest.writeSerializable(this.quantity);
        dest.writeString(this.item);
        dest.writeString(this.description);
        dest.writeSerializable(this.amount);
    }

    protected ItemList(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.rate = (BigDecimal) in.readSerializable();
        this.quantity = (BigDecimal) in.readSerializable();
        this.item = in.readString();
        this.description = in.readString();
        this.amount = (BigDecimal) in.readSerializable();
    }

    public static final Parcelable.Creator<ItemList> CREATOR = new Parcelable.Creator<ItemList>() {
        @Override
        public ItemList createFromParcel(Parcel source) {
            return new ItemList(source);
        }

        @Override
        public ItemList[] newArray(int size) {
            return new ItemList[size];
        }
    };
}
