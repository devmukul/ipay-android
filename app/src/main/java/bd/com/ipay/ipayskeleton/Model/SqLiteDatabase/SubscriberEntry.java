package bd.com.ipay.ipayskeleton.Model.SqLiteDatabase;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import bd.com.ipay.ipayskeleton.DatabaseHelper.DBConstants;

@DatabaseTable(tableName = DBConstants.DB_TABLE_SUBSCRIBERS)
public class SubscriberEntry {
    @DatabaseField(generatedId = true)
    private long _id;

    @DatabaseField(columnName = DBConstants.KEY_MOBILE_NUMBER)
    private String mobileNumber;

    @DatabaseField(columnName = DBConstants.KEY_NAME)
    private String name;

    public SubscriberEntry() {}

    public SubscriberEntry(String mobileNumber, String name) {
        this.mobileNumber = mobileNumber;
        this.name = name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getName() {
        return name;
    }

}
