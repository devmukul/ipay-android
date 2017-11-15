package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRoles;

import java.util.List;

import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.Resource;

public class BusinessRole implements Resource {
    private long id;
    private String roleName;
    private List<BusinessService> mServiceList;

    public List<BusinessService> getmServiceList() {
        return mServiceList;
    }

    public BusinessRole(long id, String roleName) {
        this.id = id;
        this.roleName = roleName;
    }

    @Override
    public int getId() {
        return (int) id;
    }

    @Override
    public String getName() {
        return roleName;
    }

    @Override
    public String getStringId() {
        return Long.toString(id);
    }
}
