package bd.com.ipay.ipayskeleton.Model.MMModule.Business;

import java.util.List;

public class GetAllEmployeesResponse {

    private List<Employee> personList;
    private String message;

    public List<Employee> getPersonList() {
        return personList;
    }

    public String getMessage() {
        return message;
    }
}
