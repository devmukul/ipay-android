package bd.com.ipay.ipayskeleton.Api.HttpResponse;

import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;

public interface HttpResponseListener {
    void httpResponseReceiver(GenericHttpResponse result);
}
