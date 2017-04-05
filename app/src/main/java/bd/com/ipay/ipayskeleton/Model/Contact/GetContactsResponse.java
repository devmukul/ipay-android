package bd.com.ipay.ipayskeleton.Model.Contact;

import java.util.List;

public class GetContactsResponse {
    private int totalCount;
    private List<ContactNode> contactList;

    public int getTotalCount() {
        return totalCount;
    }

    public List<ContactNode> getContactList() {
        return contactList;
    }

    @Override
    public String toString() {
        return "ContactNode{" +
                "totalCount='" + totalCount + '\'' +
                ", contactList=" + contactList +
                '}';
    }
}
