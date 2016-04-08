package com.ole.epustakalaya.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.ole.epustakalaya.models.AudioBookDB;
import com.ole.epustakalaya.models.Book;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by bishnu on 9/23/14.
 */
public class Utility {
    public static String NO_CONNECTION_MSG = "Seems like you are not connected to network. Check internet settings";
    public static String pdfDirectory = Environment.getExternalStorageDirectory()+"/Epustakalaya/pdf/";
    private  Context myContext;

    public static void crashMe(){
        Book book=null;
        Log.v("LEts Crash","Crash Now "+book.publisher);
    }

    public static void getAudioFilesFromDirs(String Dir){
        ArrayList<String> PIDS = new ArrayList<String>();

        Log.d("Files", "Path: " + Dir);
        File f = new File(Dir);
        File file[] = f.listFiles();
        if(file != null){
            Log.d("Files", "Size: "+ file.length);
            for (int i=0; i < file.length; i++)
            {    String s=file[i].getName();

                Log.d("Files", "FileName:" + s);
                String upToNCharacters = s.substring(0, Math.min(s.length(), 3));
                PIDS.add(upToNCharacters);
            }
            Log.d("PIDS",String.valueOf(PIDS));

        }

    }

    public static void giveNoConnectionMessage(final Context context){
         Toast.makeText(context,NO_CONNECTION_MSG, Toast.LENGTH_SHORT).show();

    }

    public static boolean createPdfDir(){
        boolean ret = true;

        File file = new File(pdfDirectory);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e("CreatDir", "Problem creating pdf folder");
                ret = false;
            }
        }
        return ret;
    }

    public static void showNoPDFReaderAlert(final Context context){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context).setTitle("Alert !")
                .setMessage("Sorry we can't find any application capable to open pdf. Please install at least one.")
                .setCancelable(true)
                .setPositiveButton("Go to App Store", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=" + "pdf reader")));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                alertDialogBuilder.create().show();
    }

    public static void showAlert(final Context context, String msg){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context).setTitle("Alert !")
                .setMessage(msg)
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alertDialogBuilder.create().show();
    }


    public static boolean isIntentAvailable(Context ctx, Intent intent) {
        final PackageManager mgr = ctx.getPackageManager();
        List<ResolveInfo> list =
                mgr.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    public static Boolean isConnected(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public static Boolean isServerAlive(Context context){
        try{
            URL myUrl = new URL(new Preference(context).getServer());
            URLConnection connection = myUrl.openConnection();
            connection.setConnectTimeout(3000);
            connection.connect();
            return true;
        } catch (Exception e) {
            // Handle your exceptions
            return false;
        }
    }

    public static boolean isDownloadManagerAvailable(Context context) {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
                return false;
            }
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setClassName("com.android.providers.downloads.ui", "com.android.providers.downloads.ui.DownloadList");
            List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
                    PackageManager.MATCH_DEFAULT_ONLY);
            return list.size() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
                int count=is.read(bytes, 0, buffer_size);
                if(count==-1)
                    break;
                os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }

    public static boolean backUpMyBooksDB(Context context){

            try
            {
                File sd = Environment.getExternalStorageDirectory();
                File data = Environment.getDataDirectory();

                if (sd.canWrite())
                {
                    String currentDBPath = "/data/"+context.getPackageName()+"/databases/epustakalayadb";
                    String backupDBPath = "/Epustakalaya/pdf/"+"epustakalayadb";
                    File currentDB = new File(data, currentDBPath);
                    File backupDB = new File(sd, backupDBPath);

                    if (currentDB.exists()) {
                        FileChannel src = new FileInputStream(currentDB).getChannel();
                        FileChannel dst = new FileOutputStream(backupDB).getChannel();
                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();
                        return true;
                    }
                }
            } catch (Exception e) {
                Log.w("Settings Backup", e);
            }
        return false;
    }

   public static String sizeInBytesToReadableString(int sizeByte){

        String redableSize="";
        DecimalFormat numberFormat = new DecimalFormat("#.00");
        if(sizeByte>=(1024*1024)) {
            redableSize = numberFormat.format((float)sizeByte / (1024 * 1024)) + " MB";
        } else if(sizeByte>=1024) {
            redableSize = numberFormat.format((float)sizeByte/1024) + " KB";
        } else {
            redableSize = sizeByte + " Byte";
        }
        Log.v("Utility",redableSize);
        return redableSize;
    }

    /**
     *
     * @param expected  expected filesize in bytes
     * @param value     The exact fielsize in bytes
     * @return      return true if the value fall into 2 KB boundary of expected fileSize
     */
    public static boolean fileIsApproxSame(long expected, long value){
        int marginKb=2;
        long high = expected+(marginKb*1024);
        long low = expected-(marginKb*1024);

        Log.v("Utility","Exp: "+expected+" exact = "+value);

        if(value >= low && value <= high){
            return true;
        }else {
            return false;
        }
    }

    public static Book[] getAllDownloadedBook(Context context){

        Book[] downloadingBooks = new MyBooksDB(context).getAllList();

        ArrayList<Book> bookList = new ArrayList<Book>();
        Book book;
        File pdfDirectory = new File(Utility.pdfDirectory);
        File[] files = pdfDirectory.listFiles();

        /*If no books are there*/
        if(files ==null){
            return null;
        }
       /* Arrays.sort(files, new Comparator<File>() {
            public int compare(File f1, File f2) {
                return f1.lastModified() > f2.lastModified() ? -1 : 1;
            }
        });
        */
        for(File file : files){

            if( !file.isDirectory() && file.getName().endsWith(".pdf")){
                String pngImageUrl,jpgImageUrl,gifImageUrl;
                String pdfFile = file.getName();
                book = new Book();

                String imgFileWithOutExtension = pdfFile.substring(0,pdfFile.length()-3);

                //This is very nasty but i think this is only workaround when we are not supposed to mentain db
                if((new File(Utility.pdfDirectory+imgFileWithOutExtension+"png")).exists()){
                    book.coverImageURL = imgFileWithOutExtension+"png";
                } else if( (new File(Utility.pdfDirectory+imgFileWithOutExtension+"jpg")).exists()){
                    book.coverImageURL = imgFileWithOutExtension+"jpg";
                }  else if((new File(Utility.pdfDirectory+imgFileWithOutExtension+"jpeg")).exists()){
                    book.coverImageURL=imgFileWithOutExtension+"jpeg";
                } else if((new File(Utility.pdfDirectory+imgFileWithOutExtension+"gif")).exists()){
                    book.coverImageURL=imgFileWithOutExtension+"gif";
                }

                for(Book dwBook :  downloadingBooks){
                    Long fileSize=(long)0;
                    try {
                        fileSize = Long.parseLong(dwBook.fileSize);
                    }catch ( Exception e){

                    }
                    if(dwBook.pdfFileURL.equals(pdfFile) && !fileIsApproxSame(fileSize, new File(pdfFile).length())){
                        book.isDownloading = true;
                        break;
                    }
                }
                book.pdfFileURL = pdfFile;
                bookList.add(book);
            }
        }
        return bookList.toArray(new Book[bookList.size()]);
    }

    public AudioBookDB[] getAllAudioDownloads(){

        MyAudioBooksDB db=new MyAudioBooksDB(myContext);

        List<AudioBookDB> contacts = db.getAllContacts();
        for (AudioBookDB cn : contacts) {
            String log = "PID: " + cn.getPID() + " ,BookTitle: " + cn.getTitle() + " ,Author: " + cn.getAuthor()+ " ,Image: " + cn.getCover()+ " ,URL: " + cn.getURL();
            // Writing Contacts to log
            Log.d("Name: ", log);
        }

     return null;

    }

}
