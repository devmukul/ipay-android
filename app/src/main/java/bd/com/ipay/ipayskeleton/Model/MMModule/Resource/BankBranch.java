package bd.com.ipay.ipayskeleton.Model.MMModule.Resource;

public class BankBranch {
    private long id;
    private String name;

    public BankBranch(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "BankBranch{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
