package bd.com.ipay.ipayskeleton.Model.MMModule.Business;

import android.os.Parcel;
import android.os.Parcelable;

public class Privilege implements Parcelable {
    private String name;
    private boolean hasAuthority;

    public Privilege(String name, boolean hasAuthority) {
        this.name = name;
        this.hasAuthority = hasAuthority;
    }

    public Privilege(String name) {
        this(name, true);
    }

    public String getName() {
        return name;
    }

    public boolean hasAuthority() {
        return hasAuthority;
    }

    public void setHasAuthority(boolean hasAuthority) {
        this.hasAuthority = hasAuthority;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeByte(this.hasAuthority ? (byte) 1 : (byte) 0);
    }

    protected Privilege(Parcel in) {
        this.name = in.readString();
        this.hasAuthority = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Privilege> CREATOR = new Parcelable.Creator<Privilege>() {
        @Override
        public Privilege createFromParcel(Parcel source) {
            return new Privilege(source);
        }

        @Override
        public Privilege[] newArray(int size) {
            return new Privilege[size];
        }
    };
}
