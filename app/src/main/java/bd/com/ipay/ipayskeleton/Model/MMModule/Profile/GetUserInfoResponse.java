package bd.com.ipay.ipayskeleton.Model.MMModule.Profile;

import java.util.HashSet;
import java.util.Set;

public class GetUserInfoResponse {

    public String message;
    public String name;
    public Set<UserProfilePictureClass> profilePictures = new HashSet<>();

    public GetUserInfoResponse() {
    }

    public String getMessage() {
        return message;
    }

    public String getName() {
        return name;
    }

    public Set<UserProfilePictureClass> getProfilePictures() {
        return profilePictures;
    }
}
