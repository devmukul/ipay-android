package bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
    public long signupTime;

    public List<UserProfilePictureClass> profilePictures = new ArrayList<>();

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

    public String getSignUpTime() {
        Date date = new Date();
        date.setTime(signupTime);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return simpleDateFormat.format(date);
    }

    public List<UserProfilePictureClass> getProfilePictures() {
        return profilePictures;
    }

}
