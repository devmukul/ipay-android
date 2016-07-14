package bd.com.ipay.ipayskeleton.Model.MMModule.Resource;

public class Bank implements Resource {
    private int id;
    private String name;
    private String bankCode;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getBankCode() {
        return bankCode;
    }

    @Override
    public String toString() {
        return "Bank{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", bankCode='" + bankCode + '\'' +
                '}';
    }
}
