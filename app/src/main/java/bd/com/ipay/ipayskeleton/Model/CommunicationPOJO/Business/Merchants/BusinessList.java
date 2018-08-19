
package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Merchants;

import java.util.List;

import bd.com.ipay.ipayskeleton.Model.BusinessContact.Outlets;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.UserAddress;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.UserAddressList;

public class BusinessList
{

    private UserAddressList businessAddress;
    private String businessLogo;
    private String businessType;
    private Location location;
    private String merchantMobileNumber;
    private String merchantName;
    private List<Outlets> outlets = null;

    public UserAddressList getAddressList() {
        return businessAddress;
    }

    public void setAddressList(UserAddressList businessAddress) {
        this.businessAddress = businessAddress;
    }

    public String getBusinessLogo() {
        return businessLogo;
    }

    public void setBusinessLogo(String businessLogo) {
        this.businessLogo = businessLogo;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getMerchantMobileNumber() {
        return merchantMobileNumber;
    }

    public void setMerchantMobileNumber(String merchantMobileNumber) {
        this.merchantMobileNumber = merchantMobileNumber;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public List<Outlets> getOutlets() {
        return outlets;
    }

    public void setOutlets(List<Outlets> outlets) {
        this.outlets = outlets;
    }


    public String getAddressString() {
        String addressString = null;
        if (businessAddress != null) {
            if (businessAddress.getOFFICE() != null) {
                List<UserAddress> office = businessAddress.getOFFICE();
                if (office != null) {
                    addressString = office.get(0).getAddressLine1();

                    if (!office.get(0).getAddressLine2().isEmpty())
                        addressString += office.get(0).getAddressLine2();
                }
            }
        }
        return addressString;
    }

    public String getThanaString() {
        String thanaString = null;
        if (businessAddress != null) {
            if (businessAddress.getOFFICE() != null) {
                List<UserAddress> office = businessAddress.getOFFICE();
                if (office != null) {
                    thanaString = office.get(0).getThana();
                }
            }
        }
        return thanaString;
    }

    public String getDistrictString() {
        String districtString = null;
        if (businessAddress != null) {
            if (businessAddress.getOFFICE() != null) {
                List<UserAddress> office = businessAddress.getOFFICE();
                if (office != null) {
                    districtString = office.get(0).getDistrict();
                }
            }
        }
        return districtString;
    }

}
