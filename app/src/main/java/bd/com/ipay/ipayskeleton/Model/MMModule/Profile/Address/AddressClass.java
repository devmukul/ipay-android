package bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Address;

import java.io.Serializable;

import bd.com.ipay.ipayskeleton.Utilities.Common.CountryList;
import bd.com.ipay.ipayskeleton.Utilities.Common.DistrictList;
import bd.com.ipay.ipayskeleton.Utilities.Common.ThanaList;

public class AddressClass implements Serializable {
    private String addressLine1;
    private String addressLine2;
    private String country;
    private int district;
    private int thana;
    private String postalCode;

    public AddressClass() {

    }

    public AddressClass(String addressLine1, String addressLine2, String country,
                        int district, int thana, String postalCode) {
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.country = country;
        this.district = district;
        this.thana = thana;
        this.postalCode = postalCode;
    }


    public AddressClass(String addressLine1, String addressLine2, String country,
                        String district, String thana, String postalCode) {
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.country = CountryList.countryNameToCountryCodeMap.get(country);
        this.district = DistrictList.districtNameToIdMap.get(district);
        this.thana = ThanaList.thanaNameToIdMap.get(thana);
        this.postalCode = postalCode;
    }


    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public String getCountryCode() {
        return country;
    }

    public int getDistrictCode() {
        return district;
    }

    public int getThanaCode() {
        return thana;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getThana() {
        return ThanaList.thanaIdToNameMap.get(thana);
    }

    public String getDistrict() {
        return DistrictList.districtIdToNameMap.get(district);
    }

    public String getCountry() {
        return CountryList.countryCodeToCountryNameMap.get(country);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (getAddressLine1() != null)
            builder.append(getAddressLine1()).append("\n");
        if (getAddressLine2() != null)
            builder.append(getAddressLine2()).append("\n");
        if (getThana() != null)
            builder.append(getThana()).append(", ");
        if (getDistrict() != null)
            builder.append(getDistrict()).append(" ");
        if (getPostalCode() != null)
            builder.append(getPostalCode());
        builder.append("\n");
        if (getCountry() != null)
            builder.append(getCountry());

        return builder.toString();
    }
}
