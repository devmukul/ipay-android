package bd.com.ipay.ipayskeleton.Api.ContactApi;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.Model.Contact.AddContactRequest;
import bd.com.ipay.ipayskeleton.Model.Contact.AddContactResponse;
import bd.com.ipay.ipayskeleton.Model.Contact.ContactNode;
import bd.com.ipay.ipayskeleton.Model.Contact.AddContactNode;
import bd.com.ipay.ipayskeleton.Model.Contact.UpdateContactNode;
import bd.com.ipay.ipayskeleton.Model.Contact.UpdateContactRequest;
import bd.com.ipay.ipayskeleton.Model.Contact.UpdateContactResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;

public class SyncContactsAsyncTask extends AsyncTask<String, Void, ContactEngine.ContactDiff> implements HttpResponseListener {

    private HttpRequestPostAsyncTask mAddContactAsyncTask;
    private AddContactResponse mAddContactResponse;

    private HttpRequestPostAsyncTask mUpdateContactAsyncTask;
    private UpdateContactResponse mUpdateContactResponse;

    private static boolean contactsSyncedOnce;

    private final Context context;
    private final List<ContactNode> serverContacts;

    public SyncContactsAsyncTask(Context context, List<ContactNode> serverContacts) {
        this.context = context;
        this.serverContacts = serverContacts;
    }

    @Override
    protected ContactEngine.ContactDiff doInBackground(String... params) {

        // Save the contact list fetched from the server into the database
        DataHelper dataHelper = DataHelper.getInstance(context);
        dataHelper.createContacts(serverContacts);

        // IMPORTANT: Perform this check only after saving all server contacts into the database!
        if (contactsSyncedOnce)
            return null;
        else
            contactsSyncedOnce = true;

        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {

            // Read phone contacts
            List<ContactNode> phoneContacts = ContactEngine.getAllContacts(context);

            // Calculate the difference between phone contacts and server contacts
            ContactEngine.ContactDiff contactDiff = ContactEngine.getContactDiff(phoneContacts, serverContacts);

            Log.i("New Contacts", contactDiff.newContacts.toString());
            Log.i("Updated Contacts", contactDiff.updatedContacts.toString());

            return contactDiff;
        } else {
            return null;
        }

    }

    @Override
    protected void onPostExecute(ContactEngine.ContactDiff contactDiff) {
        if (contactDiff != null) {
            addContacts(contactDiff.newContacts);
            updateContacts(contactDiff.updatedContacts);
        }

    }

    private void addContacts(List<ContactNode> contactList) {
        if (mAddContactAsyncTask != null) {
            return;
        }

        if (contactList.isEmpty())
            return;

        List<AddContactNode> newContacts = new ArrayList<>();
        for (ContactNode contactNode : contactList) {
            newContacts.add(new AddContactNode(contactNode.getMobileNumber(), contactNode.getName()));
        }

        AddContactRequest addContactRequest = new AddContactRequest(newContacts);
        Gson gson = new Gson();
        String json = gson.toJson(addContactRequest);

        mAddContactAsyncTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ADD_CONTACTS,
                Constants.BASE_URL_CONTACT + Constants.URL_ADD_CONTACTS, json, context, this);
        mAddContactAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void updateContacts(List<ContactNode> contactList) {
        if (mUpdateContactResponse != null) {
            return;
        }

        if (contactList.isEmpty())
            return;

        List<UpdateContactNode> updateContacts = new ArrayList<>();
        for (ContactNode contactNode : contactList) {
            UpdateContactNode updateContactNode = new UpdateContactNode(
                    contactNode.getMobileNumber(), contactNode.getName());
            updateContacts.add(updateContactNode);
        }

        UpdateContactRequest updateContactRequest = new UpdateContactRequest(updateContacts);
        Gson gson = new Gson();
        String json = gson.toJson(updateContactRequest);

        mUpdateContactAsyncTask = new HttpRequestPostAsyncTask(Constants.COMMAND_UPDATE_CONTACTS,
                Constants.BASE_URL_CONTACT + Constants.URL_UPDATE_CONTACTS, json, context, this);
        mUpdateContactAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mAddContactAsyncTask = null;
            mUpdateContactAsyncTask = null;
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_ADD_CONTACTS)) {
            try {
                mAddContactResponse = gson.fromJson(result.getJsonString(), AddContactResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    // Server contacts updated, download contacts again
                    Log.i("Friend", "Create friend successful");
                    new GetContactsAsyncTask(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    if (mAddContactResponse != null)
                        Log.e(context.getString(R.string.failed_add_friend), mAddContactResponse.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            mAddContactAsyncTask = null;

        } else if (result.getApiCommand().equals(Constants.COMMAND_UPDATE_CONTACTS)) {
            try {
                mUpdateContactResponse = gson.fromJson(result.getJsonString(), UpdateContactResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    Log.i("Friend", "Update friend successful");
                    // Maybe we should download contacts again?
                } else {
                    Log.e(context.getString(R.string.failed_update_friend), mUpdateContactResponse.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            mUpdateContactAsyncTask = null;
        }
    }
}