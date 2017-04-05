package bd.com.ipay.ipayskeleton.Utilities;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

public class HttpPatchRequestBase extends HttpEntityEnclosingRequestBase {
    public static final String METHOD_NAME = "PATCH";

    public String getMethod() {
        return METHOD_NAME;
    }

    public HttpPatchRequestBase(final String uri) {
        super();
        setURI(URI.create(uri));
    }

    public HttpPatchRequestBase(final URI uri) {
        super();
        setURI(uri);
    }

    public HttpPatchRequestBase() {
        super();
    }
}