package bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo.UserProfilePictureClass;

public class GetUserInfoResponse {

    public String message;
    public String name;
    public List<UserProfilePictureClass> profilePictures = new ArrayList<>();
    public String accountStatus;

    public GetUserInfoResponse() {
    }

    public String getMessage() {
        return message;
    }

    public String getName() {
        return name;
    }

    public List<UserProfilePictureClass> getProfilePictures() {
        return profilePictures;
    }

    public String getAccountStatus() {
        return accountStatus;
    }
}
