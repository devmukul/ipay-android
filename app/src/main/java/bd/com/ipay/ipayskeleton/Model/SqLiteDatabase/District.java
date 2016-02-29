package bd.com.ipay.ipayskeleton.Model.SqLiteDatabase;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.List;

@DatabaseTable
public class District {
    @DatabaseField(id = true)
    public long id;

    @DatabaseField
    public String name;

    @DatabaseField
    public List<Thana> thanas = new ArrayList<>();

    public District() {}

    public District(long id, String name) {
        this.id = id;
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
