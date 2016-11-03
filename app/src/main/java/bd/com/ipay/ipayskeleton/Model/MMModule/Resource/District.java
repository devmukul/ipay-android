package bd.com.ipay.ipayskeleton.Model.MMModule.Resource;

public class District implements Resource {
    private int id;
    private String name;

    public District(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
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

    @Override
    public String toString() {
        return "District{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
