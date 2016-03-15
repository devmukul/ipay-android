package bd.com.ipay.ipayskeleton.Model.MMModule.Profile;

import java.util.HashSet;
import java.util.Set;

public class GetProfileInfoResponse {

    public String message;
    public String mobileNumber;
    public String name;
    public String gender;
    public String dob;
    public String email;
    public String occupation;
    public int emailVerificationStatus;
    public int accountType;
    public String verificationStatus;
    public int verifiedByCount;
    public String father;
    public String mother;
    public String spouse;

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

    public String getDob() {
        return dob;
    }

    public String getEmail() {
        return email;
    }

    public String getOccupation() {
        return occupation;
    }

    public int getEmailVerificationStatus() {
        return emailVerificationStatus;
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
}
