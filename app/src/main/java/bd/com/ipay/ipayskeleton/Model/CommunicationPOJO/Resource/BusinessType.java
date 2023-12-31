package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource;

import android.os.Parcel;
import android.os.Parcelable;

public class BusinessType implements Resource, Parcelable {
    private int id;
    private String name;

    public BusinessType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
    }

    private BusinessType(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<BusinessType> CREATOR = new Parcelable.Creator<BusinessType>() {
        @Override
        public BusinessType createFromParcel(Parcel source) {
            return new BusinessType(source);
        }

        @Override
        public BusinessType[] newArray(int size) {
            return new BusinessType[size];
        }
    };
}
