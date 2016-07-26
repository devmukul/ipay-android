package bd.com.ipay.ipayskeleton.Model.MMModule.Education;

public class Department {
    private int departmentId;
    private String departmentName;
    private String departmentShortCode;
    private String details;
    private Institution institute;

    public int getDepartmentId() {
        return departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public String getDepartmentShortCode() {
        return departmentShortCode;
    }

    public String getDetails() {
        return details;
    }

    public Institution getInstitute() {
        return institute;
    }
}
