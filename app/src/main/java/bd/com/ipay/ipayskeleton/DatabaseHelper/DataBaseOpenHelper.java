package bd.com.ipay.ipayskeleton.DatabaseHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import bd.com.ipay.ipayskeleton.Model.SqLiteDatabase.SubscriberEntry;

public class DataBaseOpenHelper extends OrmLiteSqliteOpenHelper {

    private int newVersion;
    private String name;

    private Dao<SubscriberEntry, Long> subscriberEntryDao;

    public DataBaseOpenHelper(Context context, String name, int version) {
        super(context, name, null, version);
        this.newVersion = version;
        this.name = name;
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource source) {

//        db.execSQL("create table if not exists "
//                + DBConstants.DB_TABLE_SUBSCRIBERS
//                + "(_id integer primary key autoincrement, mobile_number text not null, "
//                + "name text)");
        try {
            TableUtils.createTable(source, SubscriberEntry.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource source, int oldVersion, int newVersion) {

    }

    public Dao<SubscriberEntry, Long> getSubscriberEntryDao() {
        getWritableDatabase();
        if (subscriberEntryDao == null) {
            try {
                subscriberEntryDao = getDao(SubscriberEntry.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return subscriberEntryDao;
    }
}