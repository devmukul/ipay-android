package bd.com.ipay.ipayskeleton.Model.MMModule.Education;

import android.os.Parcel;
import android.os.Parcelable;

public class Session implements Parcelable {
    private Integer sessionId;
    private String description;
    private String sessionName;
    private Institution institute;

    public Integer getSessionId() {
        return sessionId;
    }

    public String getDescription() {
        return description;
    }

    public String getSessionName() {
        return sessionName;
    }

    public Institution getInstitute() {
        return institute;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.sessionId);
        dest.writeString(this.description);
        dest.writeString(this.sessionName);
        dest.writeParcelable(this.institute, flags);
    }

    public Session() {
    }

    protected Session(Parcel in) {
        this.sessionId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.description = in.readString();
        this.sessionName = in.readString();
        this.institute = in.readParcelable(Institution.class.getClassLoader());
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
