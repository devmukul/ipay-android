package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo;


public class SetParentInfoRequest {

    private final String fatherMobileNumber;
    private final String motherMobileNumber;
    private final String fatherName;
    private final String motherName;

    public SetParentInfoRequest(String fatherMobileNumber, String motherMobileNumber, String fatherName, String motherName) {
        this.fatherMobileNumber = fatherMobileNumber;
        this.motherMobileNumber = motherMobileNumber;
        this.fatherName = fatherName;
        this.motherName = motherName;
    }
}

