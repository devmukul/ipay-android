package bd.com.ipay.ipayskeleton.Model.MMModule.Resource;

public class Occupation implements Resource {
    private int id;
    private String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getId() {
        return id;

    }
}
