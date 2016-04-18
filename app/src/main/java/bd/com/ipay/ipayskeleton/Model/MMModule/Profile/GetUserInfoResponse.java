package bd.com.ipay.ipayskeleton.Model.MMModule.Profile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GetUserInfoResponse {

    public String message;
    public String name;
    public List<UserProfilePictureClass> profilePictures = new ArrayList<>();

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
}
