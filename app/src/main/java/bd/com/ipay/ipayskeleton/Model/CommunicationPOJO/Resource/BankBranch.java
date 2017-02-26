package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource;

import android.os.Parcel;
import android.os.Parcelable;

public class BankBranch implements Parcelable {
    private String district;
    private String routingNumber;
    private String name;


    protected BankBranch(Parcel in) {
        district = in.readString();
        routingNumber = in.readString();
        name = in.readString();
    }

    public static final Creator<BankBranch> CREATOR = new Creator<BankBranch>() {
        @Override
        public BankBranch createFromParcel(Parcel in) {
            return new BankBranch(in);
        }

        @Override
        public BankBranch[] newArray(int size) {
            return new BankBranch[size];
        }
    };

    public String getDistrict() {
        return district;
    }

    public String getRoutingNumber() {
        return routingNumber;
    }

    public String getName() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(district);
        parcel.writeString(routingNumber);
        parcel.writeString(name);
    }
}
