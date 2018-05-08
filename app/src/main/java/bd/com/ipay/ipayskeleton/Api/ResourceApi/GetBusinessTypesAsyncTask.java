package bd.com.ipay.ipayskeleton.Api.ResourceApi;

import android.content.Context;

import com.google.gson.Gson;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
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
                context, true);

        this.mHttpResponseListener = new HttpResponseListener() {
            @Override
            public void httpResponseReceiver(GenericHttpResponse result) {

                try {
                    Gson gson = new Gson();
                    GetBusinessTypeResponse getBusinessTypeResponse = gson.fromJson(result.getJsonString(),
                            GetBusinessTypeResponse.class);

                    List<BusinessType> businessTypes = getBusinessTypeResponse.getBusinesses();

                    if (businessTypes.size() > 0) {
                        Collections.sort(businessTypes, new Comparator<BusinessType>() {
                            @Override
                            public int compare(final BusinessType object1, final BusinessType object2) {
                                return object1.getName().compareTo(object2.getName());
                            }
                        });
                    }

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
