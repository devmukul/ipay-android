package bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo.UserProfilePictureClass;

public class GetUserInfoResponse {

    private String message;
    private String name;
    private final List<UserProfilePictureClass> profilePictures = new ArrayList<>();
    private String accountStatus;
    private int accountType;

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

    public int getAccountType() { return accountType; }
}
