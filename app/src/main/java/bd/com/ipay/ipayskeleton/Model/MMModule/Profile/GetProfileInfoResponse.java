package bd.com.ipay.ipayskeleton.Model.MMModule.Profile;

import java.util.HashSet;
import java.util.Set;

public class GetProfileInfoResponse {

    public String message;
    public String mobileNumber;
    public String name;
    public String gender;
    public String dob;
    public int occupation;
    public int accountType;
    public String verificationStatus;
    public int verifiedByCount;
    public String father;
    public String mother;
    public String spouse;
    public String fatherMobileNumber;
    public String motherMobileNumber;
    public String spouseMobileNumber;

    public Set<UserProfilePictureClass> profilePictures = new HashSet<>();

    public GetProfileInfoResponse() {
    }

    public String getMessage() {
        return message;
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

    public String getDateOfBirth() {
        return dob;
    }

    public int getOccupation() {
        return occupation;
    }

    public int getAccountType() {
        return accountType;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public int getVerifiedByCount() {
        return verifiedByCount;
    }

    public String getFather() {
        return father;
    }

    public String getMother() {
        return mother;
    }

    public String getSpouse() {
        return spouse;
    }

    public Set<UserProfilePictureClass> getProfilePictures() {
        return profilePictures;
    }

    public String getFatherMobileNumber() {
        return fatherMobileNumber;
    }

    public String getMotherMobileNumber() {
        return motherMobileNumber;
    }

    public String getSpouseMobileNumber() {
        return spouseMobileNumber;
    }
}
