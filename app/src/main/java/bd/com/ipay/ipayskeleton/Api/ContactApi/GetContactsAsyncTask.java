package bd.com.ipay.ipayskeleton.Api.ContactApi;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;

import java.util.List;

import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.Model.Contact.ContactNode;
import bd.com.ipay.ipayskeleton.Model.Contact.GetContactsResponse;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class GetContactsAsyncTask extends HttpRequestGetAsyncTask implements HttpResponseListener {

    private Context context;
    private GetContactsResponse mGetContactsResponse;

    public GetContactsAsyncTask(Context context) {
        super(Constants.COMMAND_GET_CONTACTS, Constants.BASE_URL_CONTACT + Constants.URL_GET_CONTACTS, context);
        this.context = context;
        mHttpResponseListener = this;
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            if (context != null) {
                return;
            }
        }

        try {
            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                Gson gson = new Gson();
                mGetContactsResponse = gson.fromJson(result.getJsonString(), GetContactsResponse.class);

                final List<ContactNode> mGetAllContactsResponse = mGetContactsResponse.getContactList();
                if (ProfileInfoCacheManager.isAccountSwitched()) {
                    new AsyncTask<Void,Void,Void>(){
                        @Override
                        protected Void doInBackground(Void... voids) {
                            DataHelper dataHelper = DataHelper.getInstance(context);
                            dataHelper.createBusinessContacts(mGetAllContactsResponse);
                            return null;
                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                } else {
                    SyncContactsAsyncTask syncContactsAsyncTask = new SyncContactsAsyncTask(context, mGetAllContactsResponse);
                    syncContactsAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
