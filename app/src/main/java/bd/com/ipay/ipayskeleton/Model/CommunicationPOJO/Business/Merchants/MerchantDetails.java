package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Merchants;


import java.util.List;

public class MerchantDetails {
    private List<Branch> branches;
    private String businessLogo;
    private Location location;
    private String merchantName;

    public List<Branch> getBranches() {
        return branches;
    }

    public String getBusinessLogo() {
        return businessLogo;
    }

    public Location getLocation() {
        return location;
    }

    public String getMerchantName() {
        return merchantName;
    }
}
