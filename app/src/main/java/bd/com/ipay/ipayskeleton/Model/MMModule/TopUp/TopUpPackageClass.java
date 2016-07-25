package bd.com.ipay.ipayskeleton.Model.MMModule.TopUp;

import android.os.Parcel;
import android.os.Parcelable;

import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.Resource;

public class TopUpPackageClass implements Resource, Parcelable  {

    private int id;
    private String name;

    public TopUpPackageClass() {
    }

    public TopUpPackageClass(int id, String name) {
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

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "PackageType{" +
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

    private TopUpPackageClass(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<TopUpPackageClass> CREATOR = new Parcelable.Creator<TopUpPackageClass>() {
        @Override
        public TopUpPackageClass createFromParcel(Parcel source) {
            return new TopUpPackageClass(source);
        }

        @Override
        public TopUpPackageClass[] newArray(int size) {
            return new TopUpPackageClass[size];
        }
    };
}


