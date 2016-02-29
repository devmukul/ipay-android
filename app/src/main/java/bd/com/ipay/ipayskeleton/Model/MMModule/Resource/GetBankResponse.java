package bd.com.ipay.ipayskeleton.Model.MMModule.Resource;

import java.util.List;

public class GetBankResponse {

    private String message;
    private List<Bank> resource;

    public GetBankResponse() {

    }

    public String getMessage() {
        return message;
    }

    public List<Bank> getBanks() {
        return resource;
    }
}
