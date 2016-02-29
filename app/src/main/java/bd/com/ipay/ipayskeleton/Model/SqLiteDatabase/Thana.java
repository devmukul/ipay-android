package bd.com.ipay.ipayskeleton.Model.SqLiteDatabase;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Thana {
    @DatabaseField(id = true)
    public long id;

    @DatabaseField
    public String name;

    @DatabaseField(foreign = true)
    public long districtId;

    public Thana() {}

    public Thana(long id, String name, long districtId) {
        this.id = id;
        this.name = name;
        this.districtId = districtId;
    }
}
