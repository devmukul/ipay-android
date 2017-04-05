package bd.com.ipay.ipayskeleton.Model.Contact;

import java.util.List;

public class UpdateContactRequest {
    private final List<UpdateContactNode> contacts;

    public UpdateContactRequest(List<UpdateContactNode> contacts) {
        this.contacts = contacts;
    }

    public List<UpdateContactNode> getContacts() {
        return contacts;
    }
}
