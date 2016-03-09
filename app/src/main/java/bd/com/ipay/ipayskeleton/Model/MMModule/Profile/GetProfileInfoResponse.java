package bd.com.ipay.ipayskeleton.Model.MMModule.Profile;

import java.util.HashSet;
import java.util.Set;

public class GetProfileInfoResponse {

    public String mobileNumber;
    public String name;
    public String gender;
    public String dob;

    public String country;
    public String email;
    public String NIDNumber;
    public String occupation;
    public String addressLine1;
    public String addressLine2;
    public String city;
    public String district;
    public String postalCode;
    public int emailVerificationStatus;

    public Set<UserProfilePictureClass> profilePictures = new HashSet<>();

    public GetProfileInfoResponse() {
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getDob() {
        return dob;
    }

    public String getCountry() {
        return country;
    }

    public String getEmail() {
        return email;
    }

    public String getNIDNumber() {
        return NIDNumber;
    }

    public String getOccupation() {
        return occupation;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public String getCity() {
        return city;
    }

    public String getDistrict() {
        return district;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public Set<UserProfilePictureClass> getProfilePictures() {
        return profilePictures;
    }

    public int getEmailVerificationStatus() {
        return emailVerificationStatus;
    }
}
