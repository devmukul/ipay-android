package bd.com.ipay.ipayskeleton.Model.Contact;

public class UpdateContactNode {
    private String mobileNumber;
    private String contactName;
    private String relationship;

    public UpdateContactNode(String mobileNumber, String contactName) {
        this.mobileNumber = mobileNumber;
        this.contactName = contactName;
    }

    public UpdateContactNode(String mobileNumber, String contactName, String relationship) {
        this.mobileNumber = mobileNumber;
        this.contactName = contactName;
        this.relationship = relationship;
    }

}
