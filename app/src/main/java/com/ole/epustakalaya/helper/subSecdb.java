package com.ole.epustakalaya.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ole.epustakalaya.models.Section;
import com.ole.epustakalaya.models.SubSection;

import java.util.ArrayList;


/**
 * Created by roshan on 10/10/14.
 */
public class subSecdb extends SQLiteOpenHelper {

    public Section sections;

    public static final String DATABASE_NAME = "subsec_db";
    public static final int DATABASE_VERSION = 1;

    public static final String SUBSECTION_TABLE = "subsections";
    public static final String SUBSEC_UID = "subsec_uid";
    public static final String SUBSEC_ID = "subsec_id";
    public static final String SUBSEC_PARENTSEC_ID = "parentsec_id";
    public static final String SUBSEC_TITLE = "subsec_title";
    public static final String SUBSEC_BOOK_COUNT = "subsec_book_count";

    private Context context;

    public subSecdb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

//        the subsection database subsections table structure and creation
       String CREATE_SUBSECTION_TABLE = "CREATE TABLE IF NOT EXISTS "+SUBSECTION_TABLE+"("
                +SUBSEC_UID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                +SUBSEC_PARENTSEC_ID+" VARCHAR(255), "
                +SUBSEC_ID+" VARCHAR(255), "
                +SUBSEC_TITLE+" VARCHAR(255),"
                +SUBSEC_BOOK_COUNT+" INTEGER"+")";
        db.execSQL(CREATE_SUBSECTION_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2) {
        db.execSQL("DROP TABLE IF EXISTS "+SUBSECTION_TABLE);
        onCreate(db);

    }

   /*
    all CRUD operations
   */

    //add subsections to the database
    public long addsubsection(String sectionpid, String subsectionpid, String subsectiontitle, int subsectionbookcount){
       SQLiteDatabase db = this.getWritableDatabase();
        long success = 0;

        ContentValues values = new ContentValues();
        values.put(SUBSEC_PARENTSEC_ID,sectionpid);
        values.put(SUBSEC_ID,subsectionpid);
        values.put(SUBSEC_TITLE,subsectiontitle);
        values.put(SUBSEC_BOOK_COUNT,subsectionbookcount);

        db.beginTransaction();
        try {
            success = db.insert(SUBSECTION_TABLE,null,values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        db.close();

//        Log.e("getCount",""+getCountMyBooks());
        return success;
    }



    //getting all the section and subsections
    public Section[] getSectionsFromDb(){
        ArrayList<Section> sectionArrayList = new ArrayList<Section>();
        SubSection[] subSections = new SubSection[1];
        SubSection subSec = new SubSection();

//        we knoe all the sections bro so HC it
        Section section = new Section("Literature","Pustakalaya:37",0);
        section.subSections = getSubSections(section.pid); //get the corresponding section from the subsections database
        section.subSectionCount = section.subSections.length; // update the section's subsection count immediately
        sectionArrayList.add(section);

        section = new Section("Art","Pustakalaya:576",0);
        section.subSections = getSubSections(section.pid); //get the corresponding section from the subsections database
        section.subSectionCount = section.subSections.length;// update the section's subsection count immediately
        sectionArrayList.add(section);

        section = new Section("Course Materials","Pustakalaya:38",0);
        section.subSections = getSubSections(section.pid); //get the corresponding section from the subsections database
        section.subSectionCount = section.subSections.length;// update the section's subsection count immediately
        sectionArrayList.add(section);

        section = new Section("Teaching Materials","Pustakalaya:40",0);
        section.subSections = getSubSections(section.pid); //get the corresponding section from the subsections database
        section.subSectionCount = section.subSections.length;// update the section's subsection count immediately
        sectionArrayList.add(section);

        section = new Section("Reference Materials","Pustakalaya:36",0);
        section.subSections = getSubSections(section.pid); //get the corresponding section from the subsections database
        section.subSectionCount = section.subSections.length;// update the section's subsection count immediately
        sectionArrayList.add(section);

        section = new Section("Newspaper & Magazines","Pustakalaya:39",0);
        section.subSections = getSubSections(section.pid); //get the corresponding section from the subsections database
        section.subSectionCount = section.subSections.length;// update the section's subsection count immediately
        sectionArrayList.add(section);

        section = new Section("Other Materials","Pustakalaya:165",0);
        section.subSections = getSubSections(section.pid); //get the corresponding section from the subsections database
        section.subSectionCount = section.subSections.length;// update the section's subsection count immediately
        sectionArrayList.add(section);


        Section[] sections = new Section[sectionArrayList.size()];
        sectionArrayList.toArray(sections);

        return sections;

    }

//    gets the subsections
    public SubSection[] getSubSections(String sectionId){
        SubSection[] subSections = extractSubsections(sectionId);
        return subSections;
    }

//    extracts the subsections from database
    private SubSection[] extractSubsections(String sectionId){

            int len = getSubseccount(sectionId);

//            Log.w("subsectioncount: ", String.valueOf(len));

            SubSection[] subSections;
            subSections = new SubSection[len];

        String countQuery = "SELECT * FROM " + SUBSECTION_TABLE + " WHERE "+SUBSEC_PARENTSEC_ID+ " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, new String[]{sectionId});

        if (cursor.moveToFirst()){
            int countrow=0;
            do {
                SubSection subSection = new SubSection();
                subSection.pid = cursor.getString(2);
                subSection.title = cursor.getString(3);
                subSection.bookCount = cursor.getInt(4);
                subSections[countrow] = subSection;

//                Log.e("subsectable for "+sectionId+" ",cursor.getString(0)+" "+ cursor.getString(1) +" "+cursor.getString(2)+" "+cursor.getString(3)+" "+cursor.getString(4));

                countrow++;
            } while (cursor.moveToNext() && countrow<len);
        }
        cursor.close();
        db.close();
        return subSections;

    }

    //getting the row count all or some
    public int getSubseccount(String sectionId){
        int rowcount;
        if(sectionId == null) {

//            get all the row counted
            String countQuery = "SELECT * FROM " + SUBSECTION_TABLE;

            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.rawQuery(countQuery, null);
            rowcount = cursor.getCount();
            cursor.close();
        }

        else{

//            get the rows counted with particular sections-pid
            String countQuery = "SELECT * FROM " + SUBSECTION_TABLE + " WHERE "+SUBSEC_PARENTSEC_ID+ " = ?";
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.rawQuery(countQuery, new String[]{sectionId});
            rowcount = cursor.getCount();
            cursor.close();
            db.close();
        }

        return rowcount;
    }

}
