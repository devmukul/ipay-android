package bd.com.ipay.ipayskeleton.Model.BusinessContact;

import java.util.List;

import bd.com.ipay.ipayskeleton.Model.SqLiteDatabase.BusinessAccountEntry;

public class GetAllBusinessContactResponse {
    private String message;
    private List<BusinessAccountEntry> businessList;

    public GetAllBusinessContactResponse(String message, List<BusinessAccountEntry> businessList) {
        this.message = message;
        this.businessList = businessList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<BusinessAccountEntry> getBusinessContacts() {
        return businessList;
    }

    public void setBusinessContacts(List<BusinessAccountEntry> businessList) {
        this.businessList = businessList;
    }
}
