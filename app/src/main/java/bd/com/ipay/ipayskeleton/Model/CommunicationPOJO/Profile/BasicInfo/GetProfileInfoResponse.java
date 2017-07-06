package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class GetProfileInfoResponse {

    private String message;
    private int accountId;
    private String mobileNumber;
    private String name;
    private String dob;
    private String gender;
    private String primaryEmail;
    private String organizationName;
    private int occupation;
    private int accountType;
    private List<UserProfilePictureClass> profilePictures;
    private String verificationStatus;
    private int verifiedByCount;
    private long signupTime;

    public String getMessage() {
        return message;
    }

    public int getAccountId() {
        return accountId;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getName() {
        return name;
    }

    public String getDob() {
        return dob;
    }

    public String getGender() {
        return gender;
    }

    public String getPrimaryEmail() {
        return primaryEmail;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public int getOccupation() {
        return occupation;
    }

    public int getAccountType() {
        return accountType;
    }

    public List<UserProfilePictureClass> getProfilePictures() {
        return profilePictures;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public int getVerifiedByCount() {
        return verifiedByCount;
    }

    public long getSignupTime() {
        return signupTime;
    }

    public String getSignupTimeFormatted() {
        Date date = new Date();
        date.setTime(signupTime);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return simpleDateFormat.format(date);
    }
}

