package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.IntroductionAndInvite;

import android.os.Parcel;
import android.os.Parcelable;

import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.Address.AddressClass;

public class AddressContainer implements Parcelable {
    private String addressType;
    private AddressClass address;

    protected AddressContainer(Parcel in) {
        addressType = in.readString();
        address = (AddressClass) in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(addressType);
        dest.writeSerializable(address);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AddressContainer> CREATOR = new Creator<AddressContainer>() {
        @Override
        public AddressContainer createFromParcel(Parcel in) {
            return new AddressContainer(in);
        }

        @Override
        public AddressContainer[] newArray(int size) {
            return new AddressContainer[size];
        }
    };

    public String getAddressType() {
        return addressType;
    }

    public AddressClass getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "AddressContainer{" +
                "addressType='" + addressType + '\'' +
                ", address=" + address +
                '}';
    }
}