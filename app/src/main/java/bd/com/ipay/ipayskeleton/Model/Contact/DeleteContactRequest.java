package bd.com.ipay.ipayskeleton.Model.Contact;

import java.util.List;

public class DeleteContactRequest {
    private final List<DeleteContactNode> contacts;

    public DeleteContactRequest(List<DeleteContactNode> newContacts) {
        this.contacts = newContacts;
    }

    public List<DeleteContactNode> getNewContacts() {
        return contacts;
    }
}
