package bd.com.ipay.ipayskeleton.Model.MMModule.Education;

import android.os.Parcel;
import android.os.Parcelable;

public class EventParticipant implements Parcelable {
    private Integer id;
    private String participantMobileNumber;
    private String participantName;
    private String participantRollNo;
    private String participantStatus;
    private Integer departmentId;
    private Integer instituteId;
    private String departmentName;
    private Integer isDeleted;

    public String getParticipantMobileNumber() {
        return participantMobileNumber;
    }

    public String getParticipantName() {
        return participantName;
    }

    public String getParticipantRollNo() {
        return participantRollNo;
    }

    public String getParticipantStatus() {
        return participantStatus;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public Integer getId() {
        return id;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public Integer getInstituteId() {
        return instituteId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public static Creator<EventParticipant> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.participantMobileNumber);
        dest.writeString(this.participantName);
        dest.writeString(this.participantRollNo);
        dest.writeString(this.participantStatus);
        dest.writeValue(this.departmentId);
        dest.writeValue(this.instituteId);
        dest.writeString(this.departmentName);
        dest.writeValue(this.isDeleted);
    }

    public EventParticipant() {
    }

    protected EventParticipant(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.participantMobileNumber = in.readString();
        this.participantName = in.readString();
        this.participantRollNo = in.readString();
        this.participantStatus = in.readString();
        this.departmentId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.instituteId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.departmentName = in.readString();
        this.isDeleted = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<EventParticipant> CREATOR = new Parcelable.Creator<EventParticipant>() {
        @Override
        public EventParticipant createFromParcel(Parcel source) {
            return new EventParticipant(source);
        }

        @Override
        public EventParticipant[] newArray(int size) {
            return new EventParticipant[size];
        }
    };
}
