package bd.com.ipay.ipayskeleton.Model.MMModule.Profile;

import java.util.List;

public class GetIdentificationDocumentResponse {
    private String message;
    private List<IdentificationDocument> identificationDocumentList;

    public GetIdentificationDocumentResponse() {

    }

    public String getMessage() {
        return message;
    }

    public List<IdentificationDocument> getDocuments() {
        return identificationDocumentList;
    }
}
