package bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo;


public class GetParentInfoResponse {

    private String message;
    private String fatherMobileNumber;
    private String motherMobileNumber;
    private String fatherName;
    private String motherName;

    public String getMotherName() {
        return motherName;
    }

    public String getMessage() {
        return message;
    }

    public String getFatherMobileNumber() {
        return fatherMobileNumber;
    }

    public String getMotherMobileNumber() {
        return motherMobileNumber;
    }

    public String getFatherName() {
        return fatherName;
    }
}
