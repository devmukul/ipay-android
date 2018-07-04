package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Merchants;


import java.util.List;

public class GetAllMerchantsResponse {
    private List<MerchantDetails> branchResponseList;
    private String message;

    public List<MerchantDetails> getBranchResponseList() {
        return branchResponseList;
    }

    public String getMessage() {
        return message;
    }
}

