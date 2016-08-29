package bd.com.ipay.ipayskeleton.Model.MMModule.Business.Employee;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo.UserProfilePictureClass;

public class GetBusinessInformationResponse {

    private String businessName;
    private int businessType;
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

    public List<UserProfilePictureClass> getProfilePictures() {
        return profilePictures;
    }
}
