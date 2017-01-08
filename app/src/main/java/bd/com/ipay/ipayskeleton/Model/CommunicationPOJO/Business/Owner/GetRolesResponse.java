package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Owner;

import java.util.List;

public class GetRolesResponse {
    private String message;
    private List<Role> roles;

    public GetRolesResponse(String message, List<Role> roles) {
        this.message = message;
        this.roles = roles;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
