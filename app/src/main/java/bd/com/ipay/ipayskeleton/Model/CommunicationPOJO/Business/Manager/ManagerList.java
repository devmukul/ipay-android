
package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Manager;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ManagerList {

    @SerializedName("id")
    private Integer id;
    @SerializedName("managerAccoutId")
    private Integer managerAccoutId;
    @SerializedName("managerName")
    private String managerName;
    @SerializedName("managerMobileNumber")
    private String managerMobileNumber;
    @SerializedName("roleName")
    private String roleName;
    @SerializedName("createdAt")
    private Integer createdAt;
    @SerializedName("profilePictures")
    private List<ProfilePicture> profilePictures = null;

    public ManagerList() {
    }

    public ManagerList(Integer id, Integer managerAccoutId, String managerName, String managerMobileNumber, String roleName, Integer createdAt, List<ProfilePicture> profilePictures) {
        super();
        this.id = id;
        this.managerAccoutId = managerAccoutId;
        this.managerName = managerName;
        this.managerMobileNumber = managerMobileNumber;
        this.roleName = roleName;
        this.createdAt = createdAt;
        this.profilePictures = profilePictures;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getManagerAccoutId() {
        return managerAccoutId;
    }

    public void setManagerAccoutId(Integer managerAccoutId) {
        this.managerAccoutId = managerAccoutId;
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

    public Integer getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Integer createdAt) {
        this.createdAt = createdAt;
    }

    public List<ProfilePicture> getProfilePictures() {
        return profilePictures;
    }

    public void setProfilePictures(List<ProfilePicture> profilePictures) {
        this.profilePictures = profilePictures;
    }



}
