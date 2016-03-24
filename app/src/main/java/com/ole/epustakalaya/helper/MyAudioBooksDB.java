package com.ole.epustakalaya.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ole.epustakalaya.models.Book;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bikram on 3/23/16.
 */
public class MyAudioBooksDB extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "epustakalayadb";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "myAudioBooks";
    public static final String UID = "_id";
    public static final String BookName = "pdf";
    public static final String PID = "pid";
    public static final String imageName = "image";
    public static final String fileSize = "fileSize";

    private Context context;

    public MyAudioBooksDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MYBOOK_TABLE = "CREATE TABLE "+TABLE_NAME+"("
                +UID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                +PID+" VARCHAR(255), "
                +BookName+" VARCHAR(255), "
                +imageName+" VARCHAR(255), "
                +fileSize+" VARCHAR(30)"
                +")";
        db.execSQL(CREATE_MYBOOK_TABLE);
//        Toast.makeText(context,"on create",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
//        Toast.makeText(context,"on create",Toast.LENGTH_SHORT).show();
    }

    //add book
    public long addBook(Book book){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, PID+" =?",new String[]{String.valueOf(book.pid)});
        long success = 0;

        ContentValues values = new ContentValues();
        values.put(PID,book.pid);
        values.put(BookName,book.pdfFileURL);
        values.put(imageName,book.coverImageURL);
        values.put(fileSize,book.fileSize);

        db.beginTransaction();
        try {
            success = db.insert(TABLE_NAME,null,values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        db.close();
        return success;
    }

    //get book
    public Book getBook(String pid){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,new String[]{UID,BookName,imageName,fileSize}, PID+"=?",
                new String[]{String.valueOf(pid)},null,null,null,null);
        if (cursor !=null && cursor.getCount()>0) {
            cursor.moveToFirst();

            Book book = new Book();
            book.pid = pid;
            book.pdfFileURL = cursor.getString(1);
            book.coverImageURL = cursor.getString(2);
            book.fileSize = cursor.getString(3);
            cursor.close();
            db.close();

            return book;
        }

        db.close();
        return null;
    }


    //getting all books
    public Book[] getAllList(){

        List<Book> bookList = new ArrayList<Book>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM "+TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery,null);
        if (cursor.moveToFirst()){
            do {

                Book book = new Book();
                book.pid = cursor.getString(1);
                book.pdfFileURL = cursor.getString(2);
                book.coverImageURL = cursor.getString(3);
                book.fileSize = cursor.getString(4);

                Log.d("book", "***" + cursor.getString(0) + " *** " + book.pid + "***" + book.pdfFileURL + "***" + book.coverImageURL);
                bookList.add(book);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return bookList.toArray(new Book[bookList.size()]);
    }

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

    //Delete book from this table when download complets successfully
    public void deleteBook(Book book){
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_NAME, BookName+" =?",new String[]{String.valueOf(book.pdfFileURL)});
        database.close();
    }
}
