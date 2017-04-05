package bd.com.ipay.ipayskeleton.Model.Contact;

import java.util.List;

public class AddContactRequest {
    private final List<AddContactNode> contacts;

    public AddContactRequest(List<AddContactNode> newContacts) {
        this.contacts = newContacts;
    }

    public List<AddContactNode> getNewContacts() {
        return contacts;
    }
}
