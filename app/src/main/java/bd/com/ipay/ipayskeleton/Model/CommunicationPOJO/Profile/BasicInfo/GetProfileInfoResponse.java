package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GetProfileInfoResponse {

    private String message;
    private String mobileNumber;
    private String name;
    private String gender;
    private String dob;
    private int occupation;
    private int accountType;
    private String verificationStatus;
    private int verifiedByCount;
    private String father;
    private String mother;
    private long signupTime;

    private final List<UserProfilePictureClass> profilePictures = new ArrayList<>();

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
