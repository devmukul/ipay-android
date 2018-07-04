package bd.com.ipay.ipayskeleton.Model.BusinessContact;


import java.util.List;

import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Merchants.MerchantDetails;

public class TrendingBusiness {
    private String businessType;
    private List<MerchantDetails> branchResponseList;

    public String getBusinessType() {
        return businessType;
    }

    public List<MerchantDetails> getBranchResponseList() {
        return branchResponseList;
    }
}
