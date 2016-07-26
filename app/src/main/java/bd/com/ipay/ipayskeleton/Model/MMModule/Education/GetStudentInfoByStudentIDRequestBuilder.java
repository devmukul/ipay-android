package bd.com.ipay.ipayskeleton.Model.MMModule.Education;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class GetStudentInfoByStudentIDRequestBuilder {

    private int instituteID;
    private String studentID;

    public GetStudentInfoByStudentIDRequestBuilder(int instituteID, String studentID) {
        this.instituteID = instituteID;
        this.studentID = studentID;
    }

    public String getGeneratedUrl() {
        return Constants.BASE_URL_EDU + Constants.URL_GET_STUDENT_INFO_BY_STUDENT_ID
                + "/" + instituteID + "/" + studentID;
    }
}

