package bd.com.ipay.ipayskeleton.Api.ContactApi;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;

import com.google.gson.Gson;

import java.util.List;

import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.Contact.AddContactRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.Contact.AddContactResponse;
import bd.com.ipay.ipayskeleton.Model.Contact.ContactNode;
import bd.com.ipay.ipayskeleton.Model.Contact.UpdateContactRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.Contact.UpdateContactResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Logger;

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

            Logger.logI("New Contacts", contactDiff.newContacts.toString());
            Logger.logI("Updated Contacts", contactDiff.updatedContacts.toString());

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

        AddContactRequestBuilder addContactRequestBuilder = new AddContactRequestBuilder(contactList);
        mAddContactAsyncTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ADD_CONTACTS,
                addContactRequestBuilder.generateUri(), addContactRequestBuilder.getAddContactRequest(), context, this);
        mAddContactAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void updateContacts(List<ContactNode> contactList) {
        if (mUpdateContactResponse != null) {
            return;
        }

        if (contactList.isEmpty())
            return;

        UpdateContactRequestBuilder updateContactRequestBuilder = new UpdateContactRequestBuilder(contactList);
        mUpdateContactAsyncTask = new HttpRequestPostAsyncTask(Constants.COMMAND_UPDATE_CONTACTS,
                updateContactRequestBuilder.generateUri(), updateContactRequestBuilder.getUpdateContactRequest(), context, this);
        mUpdateContactAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (HttpErrorHandler.isErrorFound(result, context, null)) {
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
                    Logger.logI("Contact", context.getString(R.string.add_contact_successful));

                    new GetContactsAsyncTask(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    if (mAddContactResponse != null)
                        Logger.logE(context.getString(R.string.failed_add_contact), mAddContactResponse.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            mAddContactAsyncTask = null;

        } else if (result.getApiCommand().equals(Constants.COMMAND_UPDATE_CONTACTS)) {
            try {
                mUpdateContactResponse = gson.fromJson(result.getJsonString(), UpdateContactResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    Logger.logI("Contact", context.getString(R.string.update_contact_successful));
                    // Maybe we should download contacts again?
                } else {
                    Logger.logE(context.getString(R.string.failed_update_contact), mUpdateContactResponse.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            mUpdateContactAsyncTask = null;
        }
    }
}