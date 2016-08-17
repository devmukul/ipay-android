package bd.com.ipay.ipayskeleton.Model.MMModule.Education;

import android.os.Parcel;
import android.os.Parcelable;

public class PayableAccountHead implements Parcelable {
    private Integer id;
    private String description;
    private String name;

    public Integer getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.description);
        dest.writeString(this.name);
    }

    public PayableAccountHead() {
    }

    protected PayableAccountHead(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.description = in.readString();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<PayableAccountHead> CREATOR = new Parcelable.Creator<PayableAccountHead>() {
        @Override
        public PayableAccountHead createFromParcel(Parcel source) {
            return new PayableAccountHead(source);
        }

        @Override
        public PayableAccountHead[] newArray(int size) {
            return new PayableAccountHead[size];
        }
    };
}
