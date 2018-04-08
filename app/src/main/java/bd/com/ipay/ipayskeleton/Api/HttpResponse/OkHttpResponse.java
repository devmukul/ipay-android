package bd.com.ipay.ipayskeleton.Api.HttpResponse;


import okhttp3.Response;

public class OkHttpResponse {

    private Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
