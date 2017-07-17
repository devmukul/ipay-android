package bd.com.ipay.ipayskeleton.Model.Contact;

import android.net.Uri;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;

public class AddContactRequestBuilder {

    private String name;
    private String phoneNumber;
    private String relationship;
    private List<AddContactNode> newContacts;

    public AddContactRequestBuilder(String name, String phoneNumber, String relationship) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.relationship = relationship;

        newContacts = new ArrayList<>();
        newContacts.add(new AddContactNode(ContactEngine.formatMobileNumberBD(phoneNumber), name, relationship));
    }

    public AddContactRequestBuilder(List<ContactNode> contactList) {
        newContacts = new ArrayList<>();

        for (ContactNode contactNode : contactList) {
            newContacts.add(new AddContactNode(contactNode.getMobileNumber(), contactNode.getName()));
        }
    }

    public String getAddContactRequest() {
        AddContactRequest addContactRequest = new AddContactRequest(newContacts);
        Gson gson = new Gson();
        String json = gson.toJson(addContactRequest);
        return json;
    }

    public String generateUri() {
        Uri.Builder uri = Uri.parse(Constants.BASE_URL_CONTACT + Constants.URL_ADD_CONTACTS)
                .buildUpon();

        return uri.build().toString();
    }
}
