package bd.com.ipay.ipayskeleton.Model.MMModule.Profile;

import android.location.Address;

import bd.com.ipay.ipayskeleton.Utilities.Common.CountryList;
import bd.com.ipay.ipayskeleton.Utilities.Common.DistrictList;
import bd.com.ipay.ipayskeleton.Utilities.Common.ThanaList;

public class AddressClass {
    private String addressLine1;
    private String addressLine2;
    private String country;
    private int district;
    private int thana;
    private String postalCode;

    public AddressClass() {

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
        if (getPostalCode() != null)
            builder.append(getPostalCode()).append(" ");
        if (getThana() != null)
            builder.append(getThana()).append(", ");
        if (getDistrict() != null)
            builder.append(getDistrict());
        builder.append("\n");
        if (getCountry() != null)
            builder.append(getCountry());

        return builder.toString();
    }
}
