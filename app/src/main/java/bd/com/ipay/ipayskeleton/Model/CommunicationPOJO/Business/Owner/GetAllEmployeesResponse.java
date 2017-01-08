package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Owner;

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
