package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Employee;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.UserProfilePictureClass;

public class GetBusinessInformationResponse {

    private String businessName;
    private int businessType;
    private String mobileNumber;
    private String verificationStatus;
    private final List<UserProfilePictureClass> profilePictures = new ArrayList<>();

    public String getBusinessName() {
        return businessName;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public int getBusinessType() {
        return businessType;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public List<UserProfilePictureClass> getProfilePictures() {
        return profilePictures;
    }
}
