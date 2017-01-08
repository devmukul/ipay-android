package bd.com.ipay.ipayskeleton.Api;

import android.content.Context;

import com.google.gson.Gson;

import java.util.List;

import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.BusinessType;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.BusinessTypeRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.GetBusinessTypeResponse;
import bd.com.ipay.ipayskeleton.Utilities.Common.CommonData;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

/**
 * Asynchronously loads all business types supported by our systems.
 * Loaded businesses are saved into {@link CommonData}.
 * <p/>
 * If you want to do something with the result after loading the list, pass
 * a {@link GetBusinessTypesAsyncTask.BusinessTypeLoadListener} to the constructor.
 */
public class GetBusinessTypesAsyncTask extends HttpRequestGetAsyncTask {
    public GetBusinessTypesAsyncTask(Context context, final BusinessTypeLoadListener listener) {
        super(Constants.COMMAND_GET_BUSINESS_TYPE_LIST,
                new BusinessTypeRequestBuilder().getGeneratedUri(),
                context);

        this.mHttpResponseListener = new HttpResponseListener() {
            @Override
            public void httpResponseReceiver(GenericHttpResponse result) {

                try {
                    Gson gson = new Gson();
                    GetBusinessTypeResponse getBusinessTypeResponse = gson.fromJson(result.getJsonString(),
                            GetBusinessTypeResponse.class);

                    List<BusinessType> businessTypes = getBusinessTypeResponse.getBusinesses();
                    CommonData.setBusinessTypes(businessTypes);

                    if (listener != null) {
                        listener.onLoadSuccess(businessTypes);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (listener != null) {
                        listener.onLoadFailed();
                    }
                }
            }
        };
    }

    public GetBusinessTypesAsyncTask(Context context) {
        this(context, null);
    }

    public interface BusinessTypeLoadListener {
        void onLoadSuccess(List<BusinessType> businessTypes);

        void onLoadFailed();
    }
}
