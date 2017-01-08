package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Owner;

import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.Resource;

public class Role implements Resource {
    private int id;
    private String name;
    private String[] privileges;

    public Role(int id, String name, String[] privileges) {
        this.id = id;
        this.name = name;
        this.privileges = privileges;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getStringId() {
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getPrivileges() {
        return privileges;
    }

    public void setPrivileges(String[] privileges) {
        this.privileges = privileges;
    }
}
