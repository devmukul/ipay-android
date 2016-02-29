package bd.com.ipay.ipayskeleton.Model.MMModule.Resource;

import java.util.List;

public class GetAvailableBankResponse {

    private String message;
    private List<Bank> resource;

    public GetAvailableBankResponse() {

    }

    public String getMessage() {
        return message;
    }

    public List<Bank> getAvailableBanks() {
        return resource;
    }
}
