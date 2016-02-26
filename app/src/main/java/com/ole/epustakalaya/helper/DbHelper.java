package com.ole.epustakalaya.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by mjt-ole on 9/23/14.
 */
public class DbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "epustakalayadb";
    public static final String TABLE_NAME = "subsection";
    public static final int DATABASE_VERSION = 2;
    public static final String UID = "_id";
    public static final String Name = "name";
    public static final String pid = "pid";
    public static final String parent_id = "parent_id";
    private static final String CREATE_TABLE = "CREATE TABLE "
            +TABLE_NAME+" ("
            +UID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
            +Name+" VARCHAR(255), "
            +pid+" VARCHAR(255), "
            +parent_id+" VARCHAR(255));";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS "
            +TABLE_NAME;
    private Context context;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        Toast.makeText(context,"on create",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DbHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL(DROP_TABLE);
        onCreate(db);
        Toast.makeText(context,"on upgrade",Toast.LENGTH_SHORT).show();
    }
}
