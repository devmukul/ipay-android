package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource;

import android.os.Parcel;
import android.os.Parcelable;

public class Occupation implements Resource, Parcelable {
    private int id;
    private String name;

    protected Occupation(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Occupation> CREATOR = new Creator<Occupation>() {
        @Override
        public Occupation createFromParcel(Parcel in) {
            return new Occupation(in);
        }

        @Override
        public Occupation[] newArray(int size) {
            return new Occupation[size];
        }
    };

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getStringId() {
        return null;
    }

    @Override
    public int getId() {
        return id;

    }

}
