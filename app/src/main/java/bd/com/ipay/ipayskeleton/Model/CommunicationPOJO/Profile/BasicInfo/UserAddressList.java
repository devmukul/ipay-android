package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo;

import java.util.List;

public class UserAddressList {
    private List<UserAddress> OFFICE;
    private List<UserAddress> PRESENT;
    private List<UserAddress> PERMANENT;

    public List<UserAddress> getOFFICE() {
        return OFFICE;
    }

    public List<UserAddress> getPRESENT() {
        return PRESENT;
    }

    public List<UserAddress> getPERMANENT() {
        return PERMANENT;
    }
}
