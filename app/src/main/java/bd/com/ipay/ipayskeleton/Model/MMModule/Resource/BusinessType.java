package bd.com.ipay.ipayskeleton.Model.MMModule.Resource;

public class BusinessType implements Resource {
    private int id;
    private String name;

    public BusinessType(int id, String name) {
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

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "BusinessType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
