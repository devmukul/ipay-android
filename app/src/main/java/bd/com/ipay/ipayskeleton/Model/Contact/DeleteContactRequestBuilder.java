package bd.com.ipay.ipayskeleton.Model.Contact;

import android.net.Uri;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;

public class DeleteContactRequestBuilder {
    private String phoneNumber;
    private List<DeleteContactNode> deletedContacts;

    public DeleteContactRequestBuilder(String phoneNumber) {
        this.phoneNumber = phoneNumber;

        deletedContacts = new ArrayList<>();
        deletedContacts.add(new DeleteContactNode(ContactEngine.formatMobileNumberBD(phoneNumber)));
    }

    public String getDeleteContactRequest() {
        DeleteContactRequest deleteContactRequest = new DeleteContactRequest(deletedContacts);
        Gson gson = new Gson();
        String json = gson.toJson(deleteContactRequest);
        return json;
    }

    public String generateUri() {
        Uri.Builder uri = Uri.parse(Constants.BASE_URL_CONTACT + Constants.URL_DELETE_CONTACTS)
                .buildUpon();

        return uri.build().toString();
    }
}
