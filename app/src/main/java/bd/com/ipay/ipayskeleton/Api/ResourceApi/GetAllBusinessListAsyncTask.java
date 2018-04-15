package bd.com.ipay.ipayskeleton.Api.ResourceApi;

import android.content.Context;

import com.google.gson.Gson;

import java.util.List;

import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.BusinessContact.GetAllBusinessContactResponse;
import bd.com.ipay.ipayskeleton.Model.SqLiteDatabase.BusinessAccountEntry;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class GetAllBusinessListAsyncTask extends HttpRequestGetAsyncTask implements HttpResponseListener {
    GetAllBusinessContactResponse mBusinessContactResponse;
    List<BusinessAccountEntry> mBusinessContacts;
    private Context context;

    public GetAllBusinessListAsyncTask(Context context, String mUri) {
        super(Constants.COMMAND_GET_ALL_BUSINESS_LIST, mUri, context,true);
        mHttpResponseListener = this;
        this.context = context;
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (HttpErrorHandler.isErrorFound(result,context,null)) {
            return;
        }
        try {
            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                Gson gson = new Gson();
                mBusinessContactResponse = gson.fromJson(result.getJsonString(), GetAllBusinessContactResponse.class);
                mBusinessContacts = mBusinessContactResponse.getBusinessContacts();

                // Save the list fetched from the server into the database
                DataHelper dataHelper = DataHelper.getInstance(context);
                dataHelper.createBusinessAccountsList(mBusinessContacts);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

