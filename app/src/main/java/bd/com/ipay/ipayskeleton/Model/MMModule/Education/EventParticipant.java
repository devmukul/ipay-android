package bd.com.ipay.ipayskeleton.Model.MMModule.Education;

public class EventParticipant {
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
}
