package bd.com.ipay.ipayskeleton.Model.MMModule.Business.Employee;

import java.util.List;

public class GetAllBusinessContactResponse {
    private String message;
    private List<BusinessContact> businessContacts;

    public GetAllBusinessContactResponse(String message, List<BusinessContact> businessContacts) {
        this.message = message;
        this.businessContacts = businessContacts;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<BusinessContact> getBusinessContacts() {
        return businessContacts;
    }

    public void setBusinessContacts(List<BusinessContact> businessContacts) {
        this.businessContacts = businessContacts;
    }
}
