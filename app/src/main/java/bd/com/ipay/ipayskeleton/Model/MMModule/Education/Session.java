package bd.com.ipay.ipayskeleton.Model.MMModule.Education;

import android.os.Parcel;
import android.os.Parcelable;

public class Session implements Parcelable {
    private Integer id;
    private String description;
    private String sessionName;
    private Integer instituteId;

    public Integer getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getSessionName() {
        return sessionName;
    }


    public Integer getInstituteId() {
        return instituteId;
    }

    public static Creator<Session> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.description);
        dest.writeString(this.sessionName);
        dest.writeValue(this.instituteId);
    }

    public Session() {
    }

    protected Session(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.description = in.readString();
        this.sessionName = in.readString();
        this.instituteId = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<Session> CREATOR = new Parcelable.Creator<Session>() {
        @Override
        public Session createFromParcel(Parcel source) {
            return new Session(source);
        }

        @Override
        public Session[] newArray(int size) {
            return new Session[size];
        }
    };
}
