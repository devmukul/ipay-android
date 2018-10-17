package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource;

import android.os.Parcel;
import android.os.Parcelable;

public class AccountName implements Resource, Parcelable {
    private String name;

    public AccountName(String name) {
        this.name = name;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getStringId() {
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "BusinessType{" +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
    }

    private AccountName(Parcel in) {
        this.name = in.readString();
    }

    public static final Creator<AccountName> CREATOR = new Creator<AccountName>() {
        @Override
        public AccountName createFromParcel(Parcel source) {
            return new AccountName(source);
        }

        @Override
        public AccountName[] newArray(int size) {
            return new AccountName[size];
        }
    };
}
