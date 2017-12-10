
package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Manager;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.UserProfilePictureClass;

public class ManagerList {

    @SerializedName("id")
    private int id;
    @SerializedName("managerAccountId")
    private int managerAccountId;
    @SerializedName("managerName")
    private String managerName;
    @SerializedName("managerMobileNumber")
    private String managerMobileNumber;
    @SerializedName("roleName")
    private String roleName;
    @SerializedName("createdAt")
    private long createdAt;
    @SerializedName("profilePictures")
    private List<UserProfilePictureClass> profilePictures = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getManagerAccountId() {
        return managerAccountId;
    }

    public void setManagerAccountId(int managerAccountId) {
        this.managerAccountId = managerAccountId;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getManagerMobileNumber() {
        return managerMobileNumber;
    }

    public void setManagerMobileNumber(String managerMobileNumber) {
        this.managerMobileNumber = managerMobileNumber;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public List<UserProfilePictureClass> getProfilePictures() {
        return profilePictures;
    }

    public void setProfilePictures(List<UserProfilePictureClass> profilePictures) {
        this.profilePictures = profilePictures;
    }

}
