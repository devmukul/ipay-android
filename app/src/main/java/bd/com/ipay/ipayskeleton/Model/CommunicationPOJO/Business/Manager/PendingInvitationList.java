
package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Manager;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.UserProfilePictureClass;

public class PendingInvitationList {

    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("managerAccountId")
    @Expose
    private long managerAccountId;
    @SerializedName("managerName")
    @Expose
    private String managerName;
    @SerializedName("managerMobileNumber")
    @Expose
    private String managerMobileNumber;
    @SerializedName("roleName")
    @Expose
    private String roleName;
    @SerializedName("createdAt")
    @Expose
    private long createdAt;
    @SerializedName("profilePictures")
    @Expose
    private List<UserProfilePictureClass> profilePictures = null;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getManagerAccountId() {
        return managerAccountId;
    }

    public void setManagerAccountId(long managerAccountId) {
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
