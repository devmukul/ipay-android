package bd.com.ipay.ipayskeleton.Model.Contact;

import android.net.Uri;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;

public class UpdateContactRequestBuilder {

    private String name;
    private String phoneNumber;
    private String relationship;
    private List<UpdateContactNode> updatedContacts;

    public UpdateContactRequestBuilder(String name, String phoneNumber, String relationship) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.relationship = relationship;

        updatedContacts = new ArrayList<>();
        updatedContacts.add(new UpdateContactNode(ContactEngine.formatMobileNumberBD(phoneNumber), name, relationship));
    }

    public UpdateContactRequestBuilder(List<ContactNode> contactList) {
        updatedContacts = new ArrayList<>();

        for (ContactNode contactNode : contactList) {
            updatedContacts.add(new UpdateContactNode(contactNode.getMobileNumber(), contactNode.getName()));
        }
    }

    public String getUpdateContactRequest() {
        UpdateContactRequest updateContactRequest = new UpdateContactRequest(updatedContacts);
        Gson gson = new Gson();
        String json = gson.toJson(updateContactRequest);
        return json;
    }

    public String generateUri() {
        Uri.Builder uri = Uri.parse(Constants.BASE_URL_CONTACT + Constants.URL_UPDATE_CONTACTS)
                .buildUpon();

        return uri.build().toString();
    }
}
