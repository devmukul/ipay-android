package bd.com.ipay.ipayskeleton.Model.MMModule.Education;

import android.os.Parcel;
import android.os.Parcelable;

public class EventParticipant implements Parcelable {
    private Integer eventParticipantsId;
    private String participantMobileNumber;
    private String participantName;
    private String participantRollNo;
    private String participantStatus;
    private Department department;
    private Integer isDeleted;

    public Integer getEventParticipantsId() {
        return eventParticipantsId;
    }

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

    public Department getDepartment() {
        return department;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.eventParticipantsId);
        dest.writeString(this.participantMobileNumber);
        dest.writeString(this.participantName);
        dest.writeString(this.participantRollNo);
        dest.writeString(this.participantStatus);
        dest.writeParcelable(this.department, flags);
        dest.writeValue(this.isDeleted);
    }

    public EventParticipant() {
    }

    protected EventParticipant(Parcel in) {
        this.eventParticipantsId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.participantMobileNumber = in.readString();
        this.participantName = in.readString();
        this.participantRollNo = in.readString();
        this.participantStatus = in.readString();
        this.department = in.readParcelable(Department.class.getClassLoader());
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
