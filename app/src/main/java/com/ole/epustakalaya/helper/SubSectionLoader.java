package com.ole.epustakalaya.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ole.epustakalaya.models.SubSection;

import java.util.List;

/**
 * Created by mjt-ole on 9/29/14.
 */
public class SubSectionLoader extends SQLiteOpenHelper {

    private static final int Database_version = 1;
    private static final String DATABASE_NAME = "epustakalayadb";

    private static final String TABLE_NAME = "subsection";
    private static final String uid = "_id";
    private static final String pid = "pid";
    private static final String title = "title";
    private static final String totalBooks = "totalBooks";
    private static final String parentId = "patentId";


    public SubSectionLoader(Context context) {
        super(context,DATABASE_NAME,null, Database_version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+" ("
                +uid+" INTEGER PRIMARY KEY,"
                +pid+" VARCHAR(255),"
                +title+" VARCHAR(255),"
                +totalBooks+" INTEGER(10)"
                +parentId+" VARCHAR(255)";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        String DEL_TABLE = "DROP TABLE IF EXISTS "+TABLE_NAME;

        onCreate(sqLiteDatabase);
    }

    public void addSubSection(SubSection subSection){

    }

    public SubSection getSubSection(String pid){

        return null;
    }

    public List<SubSection> getAllSubSections(String parentId){

        return null;
    }

    public int getSubSectionCount(String parentId){

        return 0;
    }

    public int updateSubSection(SubSection subSection){

        return 0;
    }

    public void deleteSubSection(SubSection subSection){

    }
}
