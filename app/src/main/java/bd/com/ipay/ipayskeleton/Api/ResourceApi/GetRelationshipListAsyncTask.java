package bd.com.ipay.ipayskeleton.Api.ResourceApi;

import android.content.Context;

import com.google.gson.Gson;

import java.util.List;

import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.GetRelationshipRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.GetRelationshipResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.Relationship;
import bd.com.ipay.ipayskeleton.Utilities.Common.CommonData;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class GetRelationshipListAsyncTask extends HttpRequestGetAsyncTask {
    public GetRelationshipListAsyncTask(Context context, final RelationshipLoadListener listener) {
        super(Constants.COMMAND_GET_RELATIONSHIP_LIST,
                new GetRelationshipRequestBuilder().getGeneratedUri(),
                context, true);

        this.mHttpResponseListener = new HttpResponseListener() {
            @Override
            public void httpResponseReceiver(GenericHttpResponse result) {

                try {
                    Gson gson = new Gson();
                    GetRelationshipResponse getRelationshipResponse = gson.fromJson(result.getJsonString(),
                            GetRelationshipResponse.class);

                    List<Relationship> relationshipList = getRelationshipResponse.getRelationships();
                    CommonData.setRelationshipList(relationshipList);

                    if (listener != null) {
                        listener.onLoadSuccess(relationshipList);
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

    public GetRelationshipListAsyncTask(Context context) {
        this(context, null);
    }

    public interface RelationshipLoadListener {
        void onLoadSuccess(List<Relationship> relationshipList);

        void onLoadFailed();
    }
}
