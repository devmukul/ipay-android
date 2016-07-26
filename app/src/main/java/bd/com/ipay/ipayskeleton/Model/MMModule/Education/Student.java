package bd.com.ipay.ipayskeleton.Model.MMModule.Education;

public class Student {

    private int eventParticipantsId;
    private String participantMobileNumber;
    private String participantName;
    private String participantRollNo;
    private String participantStatus;
    private Department department;
    private int isDeleted;

    public int getEventParticipantsId() {
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

    public int getIsDeleted() {
        return isDeleted;
    }
}

