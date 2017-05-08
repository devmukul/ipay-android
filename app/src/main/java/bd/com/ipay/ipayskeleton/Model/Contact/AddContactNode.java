package bd.com.ipay.ipayskeleton.Model.Contact;

public class AddContactNode {
    private String mobileNumber;
    private String contactName;
    private String relationship;

    public AddContactNode() {

    }

    public AddContactNode(String mobileNumber, String contactName) {
        this.mobileNumber = mobileNumber;
        this.contactName = contactName;
    }

    public AddContactNode(String mobileNumber, String contactName, String relationship) {
        this.mobileNumber = mobileNumber;
        this.contactName = contactName;
        this.relationship = relationship;
    }

    public String getContactNumber() {
        return mobileNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public String getRelationship() {
        return relationship;
    }
}
