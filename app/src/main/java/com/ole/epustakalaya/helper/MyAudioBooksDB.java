package com.ole.epustakalaya.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ole.epustakalaya.models.AudioBook;
import com.ole.epustakalaya.models.AudioBookDB;
import com.ole.epustakalaya.models.Book;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bikram on 3/23/16.
 */
public class MyAudioBooksDB extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "epustakalayaaudiodb";
    public static final int DATABASE_VERSION = 8;
    public static final String TABLE_NAME = "myAudioBooks";
    public static final String UID = "_id";
    public static final String aPID = "pid";
    public static final String aTitle = "title";
    public static final String aAuthor = "author";

    public static final String aImage = "coverImageURL";
    public static final String aViews= "views";
    public static final String aTrackURL= "trackURL";

    private Context context;

    public MyAudioBooksDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MYAUDIOBOOK_TABLE = "CREATE TABLE "+TABLE_NAME+"("
                +UID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                +aPID+" VARCHAR(255), "
                +aTitle+" VARCHAR(255), "
                +aAuthor+" VARCHAR(255),"
                +aImage+" VARCHAR(50), "
                +aTrackURL+" VARCHAR(50) "
                +")";
        db.execSQL(CREATE_MYAUDIOBOOK_TABLE);
//        Toast.makeText(context,"on create",Toast.LENGTH_SHORT).show();
        Log.d("db","creating atable");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
//        Toast.makeText(context,"on create",Toast.LENGTH_SHORT).show();
    }

    //add book
    public long addAudioBook(String mPid,String mTitle,String mAuthor,String mCover,String mtrack){
        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_NAME, PID+" =?",new String[]{String.valueOf(book.id)});
        long success = 0;

        ContentValues avalues = new ContentValues();
        avalues.put(aPID,mPid);
        avalues.put(aTitle,mTitle);
        avalues.put(aAuthor,mAuthor);
        avalues.put(aImage,mCover);
        avalues.put(aTrackURL,mtrack);

        db.beginTransaction();
        try {
            success = db.insert(TABLE_NAME,null,avalues);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        db.close();
        return success;
    }

    //get book
    public AudioBookDB getBook(String pid){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,new String[]{UID,aPID,aTitle,aAuthor,aImage,aTrackURL}, aPID+"=?",
                new String[]{String.valueOf(pid)},null,null,null,null);
        if (cursor !=null && cursor.getCount()>0) {
            cursor.moveToFirst();

            AudioBookDB abook = new AudioBookDB();
            abook.setPID(pid);
            abook.setTitle(cursor.getString(2));
            abook.setAuthor(cursor.getString(3));
            abook.setCover(cursor.getString(4));
            abook.setURL(cursor.getString(5));
            cursor.close();
            db.close();

            return abook;
        }

        db.close();
        return null;
    }
    public List<AudioBookDB> getAllContacts() {
        List<AudioBookDB> DBList = new ArrayList<AudioBookDB>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                AudioBookDB contact = new AudioBookDB();
                contact.setPID(cursor.getString(1));
                contact.setTitle(cursor.getString(2));
                contact.setAuthor(cursor.getString(3));
                contact.setCover(cursor.getString(4));
                contact.setURL(cursor.getString(5));
                // Adding contact to list
                DBList.add(contact);

            } while (cursor.moveToNext());
        }

        // return contact list
        return DBList;
    }

//    //getting all books
//    public List<AudioBookDB> getAllList(){
//
//        List<AudioBookDB> bookList = new ArrayList<AudioBookDB>();
//        SQLiteDatabase db = this.getReadableDatabase();
//        String selectQuery = "SELECT * FROM "+TABLE_NAME;
//        AudioBookDB book = new AudioBookDB();
//        Cursor cursor = db.rawQuery(selectQuery,null);
//        if (cursor.moveToFirst()){
//
//            do {
//
//
//                book.pid = cursor.getString(1);
//                book.title = cursor.getString(2);
//                book.author = cursor.getString(3);
//                book.coverImageURL = cursor.getString(4);
//                book.views = cursor.getInt(5);
//
//                Log.d("book", "***" + cursor.getString(0) + " *** " + book.pid + "***" + book.title + "***" + book.coverImageURL);
//
////                bookList.add(book);
//
//            } while (cursor.moveToNext());
//
//            cursor.close();
//
//        }
//        db.close();
//        return book;
////        return bookList.toArray(new AudioBookDB[bookList.size()]);
//
//    }

    //getting all count
    public int getCountMyBooks(){
        String countQuery = "SELECT * FROM "+TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(countQuery,null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    /* Method for fetching record from Database */
    public ArrayList<AudioBookDB> getAllDownloadABooks() {
        String query = "SELECT * FROM " + TABLE_NAME;
        ArrayList<AudioBookDB> my_aDownloads = new ArrayList<AudioBookDB>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor c = database.rawQuery(query, null);
        if (c != null) {
            while (c.moveToNext()) {
//                int code = c.getInt(c.getColumnIndex());
                String pid = c.getString(c.getColumnIndex(aPID));
                String title = c.getString(c.getColumnIndex(aTitle));
                String author = c.getString(c.getColumnIndex(aAuthor));
                String cover = c.getString(c.getColumnIndex(aImage));
                String url = c.getString(c.getColumnIndex(aTrackURL));

                AudioBookDB ABDB = new AudioBookDB();
                ABDB.setPID(pid);
                ABDB.setTitle(title);
                ABDB.setAuthor(author);
                ABDB.setCover(cover);
                ABDB.setURL(url);

                Log.v("DBHelper: ", "PID: " + pid);
                Log.v("DBHelper: ", "Title: " + title);
                Log.v("DBHelper: ", "Author: " + author);
                Log.v("DBHelper: ", "Cover: " + cover);
                Log.v("DBHelper: ", "Url: " + url);

                my_aDownloads.add(ABDB);
            }
        }

        return my_aDownloads;

    }



    //Delete book from this table when download complets successfully
    public void deleteBook(Book book){

        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_NAME, aTitle +" =?",new String[]{String.valueOf(book.pdfFileURL)});
        database.close();

    }
}
