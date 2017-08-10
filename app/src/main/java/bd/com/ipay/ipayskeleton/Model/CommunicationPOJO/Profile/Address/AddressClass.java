package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.Address;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.List;

import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.District;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.Thana;
import bd.com.ipay.ipayskeleton.Utilities.Common.CountryList;

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

    private String getThana(List<Thana> thanaList) {
        if (thanaList != null) {
            for (Thana thana : thanaList) {
                if (thana.getId() == this.thana)
                    return thana.getName();

            }
        }

        return null;
    }

    private String getDistrict(List<District> districtList) {
        if (districtList != null) {
            for (District district : districtList) {
                if (district.getId() == this.district)
                    return district.getName();
            }
        }

        return null;
    }

    private String getCountry() {
        return CountryList.countryCodeToCountryNameMap.get(country);
    }

    public String toString(List<Thana> thanaList, List<District> districtList) {
        StringBuilder builder = new StringBuilder();

        if (!TextUtils.isEmpty(getAddressLine1()))
            builder.append(getAddressLine1()).append("\n");
        if (!TextUtils.isEmpty(getAddressLine2()))
            builder.append(getAddressLine2()).append("\n");

        String thanaName = getThana(thanaList);
        if (!TextUtils.isEmpty(thanaName))
            builder.append(thanaName.trim()).append(", ");

        String districtName = getDistrict(districtList);
        if (!TextUtils.isEmpty(districtName))
            builder.append(districtName.trim()).append(" ");

        if (!TextUtils.isEmpty(getPostalCode()))
            builder.append(" - ").append(getPostalCode());
        builder.append("\n");
        if (!TextUtils.isEmpty(getCountry()))
            builder.append(getCountry());

        return builder.toString();
    }
}
