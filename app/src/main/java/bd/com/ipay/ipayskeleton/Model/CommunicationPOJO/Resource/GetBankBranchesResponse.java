package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource;

import java.util.List;

public class GetBankBranchesResponse {

    private String message;
    private List<BankBranch> resource;

    public GetBankBranchesResponse() {

    }

    public String getMessage() {
        return message;
    }

    public List<BankBranch> getAvailableBranches() {
        return resource;
    }
}
