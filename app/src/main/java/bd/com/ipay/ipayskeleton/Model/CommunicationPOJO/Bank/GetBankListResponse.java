package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Bank;

import java.util.List;

public class GetBankListResponse {

    private List<UserBankClass> bankAccountList;

    public GetBankListResponse() {
    }

    public List<UserBankClass> getBanks() {
        return bankAccountList;
    }

}
