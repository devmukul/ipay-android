package bd.com.ipay.ipayskeleton.Api;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.Employee.BusinessContact;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.Employee.GetAllBusinessContactResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class GetAllBusinessListAsyncTask extends HttpRequestGetAsyncTask implements HttpResponseListener {
    GetAllBusinessContactResponse mBusinessContactResponse;
    List<BusinessContact> mBusinessContacts;
    private Context context;

    public GetAllBusinessListAsyncTask(Context context, String mUri) {
        super(Constants.COMMAND_GET_ALL_BUSINESS_LIST, mUri, context);
        mHttpResponseListener = this;
        context = context;
    }


    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            if (getContext() != null) {
                Toast.makeText(getContext(), R.string.contacts_sync_failed, Toast.LENGTH_LONG).show();
                return;
            }
        }
        try {
            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                Gson gson = new Gson();
                mBusinessContactResponse = gson.fromJson(result.getJsonString(), GetAllBusinessContactResponse.class);
                mBusinessContacts = mBusinessContactResponse.getBusinessContacts();
                Toast.makeText(getContext(), result.toString(), Toast.LENGTH_LONG).show();
                for(BusinessContact businessContact: mBusinessContacts)
                    Toast.makeText(getContext(),businessContact.getBusinessName(), Toast.LENGTH_LONG).show();


                // Save the friend list fetched from the server into the database
                //DataHelper dataHelper = DataHelper.getInstance(context);
                //dataHelper.createFriends(mBusinessContacts);
            } else {
                if (getContext() != null) {
                    Toast.makeText(getContext(), R.string.contacts_sync_failed, Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

            if (getContext() != null) {
                Toast.makeText(getContext(), R.string.contacts_sync_failed, Toast.LENGTH_LONG).show();
            }
        }

    }
}

