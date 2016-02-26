package com.ole.epustakalaya.helper;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import com.ole.epustakalaya.models.AudioBook;
import com.ole.epustakalaya.models.Book;
import com.ole.epustakalaya.models.Section;
import com.ole.epustakalaya.models.SubSection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by bishnu on 9/22/14.
 */
public class ServerSideHelper {

    private static String TAG = "ServerSideHelper";
    public static String PUST_SERVER = "http://pustakalaya.org/";
    public static String SCHOOL_SERVER = "http://192.168.5.200/fez/";    //Test Schoolserver comment for final build
//    public static String SCHOOL_SERVER = "http://172.18.0.1/fez/";     // http://172.168.96.1/fez/
    private Context context;

    //    public static String BASE_URL = "http://192.168.5.72/fez/"; //Local server
//    public static String BASE_URL = "http://pustakalaya.org/"; //pustakalaya server
    public String BASE_URL;


    public static String BOOK_COVER_PIC = "images/book/images/";

    public static String CATEGORY_URL = "api/category/";
    public static String BOOK_LIST_URL = "api/bookList/";
    public static String BOOK_DETAIL_URL = "api/view/";
    public static String EDITORS_PICK = "api/editors_pick";
    public static String LATEST_BOOKS_URL = "api/listAllBook/";
    public static String SEARCH_URL = "api/search/";

    public static String AUDIO_LIST_URL = "api/listAllAudioBook/"; /* page size and page is required */
    public static String AUDIO_LIST_CHP_URL = "api/listAllChapters/"; /* id is required */
    public static String AUDIO_SEARCH_URL = "api/serachAudioBook/";

    public static int LIMIT = 12;

    //    public static String BOOK_COVER_PIC = BASE_URL+"images/book/small_img/";
    public ServerSideHelper(Context context) {
        this.context = context;
        BASE_URL = getBase_URL();
    }


    public String getBase_URL() {
        Preference pref = new Preference(context);
        return pref.getServer();
    }

    public Section[] getSections() {
        ArrayList<Section> sectionArrayList = new ArrayList<Section>();
        SubSection[] subSections = new SubSection[1];
        SubSection subSec = new SubSection();

        sectionArrayList.add(new Section("Literature", "Pustakalaya:37", 0));
        sectionArrayList.add(new Section("Art", "Pustakalaya:576", 0));
        sectionArrayList.add(new Section("Course Materials", "Pustakalaya:38", 0));
        sectionArrayList.add(new Section("Teaching Materials", "Pustakalaya:40", 0));
        sectionArrayList.add(new Section("Reference Materials", "Pustakalaya:36", 0));
        sectionArrayList.add(new Section("Newspaper & Magazines", "Pustakalaya:39", 0));
        sectionArrayList.add(new Section("Other Materials", "Pustakalaya:165", 0));
        Log.v(TAG,"fetching subsctions");
        for (Section sec : sectionArrayList) {

            sec.subSections = getSubSections(sec.pid);
            sec.subSections = null;
            if(sec.subSections == null){
                sec.subSectionCount = 0;
            } else {
                sec.subSectionCount = sec.subSections.length;
            }

        }

        Section[] sections = new Section[sectionArrayList.size()];
        sectionArrayList.toArray(sections);

        //need to fetch the sebsection of individual section and populate on corresponding object

        return sections;
    }

    public SubSection[] getSubSections(String sectionId) {
        String url = getBase_URL() + CATEGORY_URL + sectionId;
        String data = getDataStringFromUrl(url);
        SubSection[] subSections = extractSubsections(data);
        return subSections;
    }

    public Book[] getBookList(String subSectionId) {
        //read pref file and the append require ordering prefereecne to server
        Preference preference = new Preference(context);
        String sortFieldType = preference.getSortField();
        String sortFieldOrder = preference.getSortOrder();
        /*now append the sort field type and order to the url below and call accordingly*/
        String url = getBase_URL() + BOOK_LIST_URL + subSectionId+"/"+sortFieldType+"/"+sortFieldOrder;
        String data = getDataStringFromUrl(url);
        return extractBookList(data);
    }

    public Book[] getLatestBookList(int page) {
        String url = getBase_URL() + LATEST_BOOKS_URL + page + "/" + LIMIT;
        String data = getDataStringFromUrl(url);
        return extractLatestBookList(data);
    }

    /*roshan on*/
    public Book[] getEditorsPickBooks() {
        String url = getBase_URL() + EDITORS_PICK;
        String data = getDataStringFromUrl(url);
        return extractEditorsPickBooks(data);
    }
    /*roshan off*/

    public Book getBookDetails(String bookId) {
        String url = getBase_URL() + BOOK_DETAIL_URL + bookId;
        String data = getDataStringFromUrl(url);
        Book book = extractBookDetail(data);
        if (book != null) {
            book.pid = bookId;
        }
        return book;
    }

    public Book[] getSearchedBookList(String keyword){
        String url = null;
        try {
            url = getBase_URL()+SEARCH_URL+ URLEncoder.encode(keyword, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String data = getDataStringFromUrl(url);
        Book[] books = extractBookList(data);
        return books;
    }

    /* Audo section */
    public AudioBook[] getAudioBookList() {
        //read pref file and the append require ordering prefereecne to server
        Preference preference = new Preference(context);
        String sortFieldType = preference.getSortField();
        String sortFieldOrder = preference.getSortOrder();
        /*now append the sort field type and order to the url below and call accordingly*/
        String url = getBase_URL() + AUDIO_LIST_URL +"/"+sortFieldType+"/"+sortFieldOrder;
        String data = getDataStringFromUrl(url);
        return extractAudioBookList(data);
    }

    private AudioBook[] extractAudioBookList(String data ){
        AudioBook[] abooks;
        JSONObject json = null;
        try {
            Log.v(TAG, data);
            json = new JSONObject(data);
            JSONArray jsonArray = json.getJSONArray("content");

            int len = jsonArray.length();
            abooks = new AudioBook[len];

            for (int i = 0; i < len; i++) {
                JSONObject jo = (JSONObject) jsonArray.get(i);

                AudioBook book = new AudioBook();

                book.id = jo.getString("id");
                book.title = jo.getString("title");
                book.coverImageURL = jo.getString("cover");
                book.author = jo.getString("author");
                book.lang = jo.optString("lang","nep");
                book.views = Integer.parseInt(jo.getString("views"));

                abooks[i] = book;
            }

            return abooks;
        } catch (Exception e) {
            Log.e(TAG, "Something wrong fond on data sent from server::: " + e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
    }

    /* Returns fully populated AudioBook object  */
    private AudioBook extractAudioBook(String data){
        AudioBook abook = new AudioBook();

        return abook;
    }

    private AudioBook.Chapter[] extractAudioBookChapter(String data) {
        AudioBook.Chapter[] chapters;

        JSONObject json = null;
        try {
            Log.v(TAG, data);
            json = new JSONObject(data);
            JSONArray jsonArray = json.getJSONArray("content");

            int len = jsonArray.length();
            chapters = new AudioBook.Chapter[len];

            for (int i = 0; i < len; i++) {
                JSONObject jo = (JSONObject) jsonArray.get(i);

                AudioBook.Chapter chapter = new AudioBook.Chapter();

                chapter.id = jo.getString("id");
                chapter.title = jo.getString("title");
                chapter.fileURL = jo.getString("fileURL");
                chapters[i] = chapter;
            }

            return chapters;
        } catch (Exception e) {
            Log.e(TAG, "Something wrong fond on data sent from server::: " + e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
    }

    private Book extractBookDetail(String stringData) {
        Book book = new Book();

        JSONObject json = null;
        try {
            Log.v(TAG, stringData);
            json = new JSONObject(stringData);
            String type = json.getString("type");
            JSONArray jsBooks = json.getJSONArray("content");

            JSONObject jsBook1 = (JSONObject) jsBooks.get(0);
            JSONObject jsBook2 = jsBooks.length() > 1 ? (JSONObject) jsBooks.get(1) : null;

            book.title = jsBook1.getString("title");
            book.coverImageURL = jsBook1.optString("bookCover");
            book.description = jsBook1.optString("description");
            book.pdfFileURL = jsBook1.optString("pdf");
            book.xmlFileURL = jsBook2 != null ? jsBook2.optString("pdf") : ""; // ?? What is the logic behind sending xml file under the name of pdf, and sending array for just a single value change??
            book.externalLink = jsBook1.optString("external_link");
            book.author = jsBook1.optString("author"); //??why not author?? changed
            book.publisher = jsBook1.optString("publisher");
            book.lang = jsBook1.optString("lang");
            book.place = jsBook1.optString("place");
            book.views = Integer.parseInt(jsBook1.optString("views"));
            book.downloads = Integer.parseInt(jsBook1.optString("countDownload"));
            String pageC = jsBook1.optString("page");
            book.pageCount = pageC.length()>0?Integer.parseInt(jsBook1.optString("page")):0;
            book.fileSize = jsBook1.optString("filesize");
//            book.pageCount = Integer.parseInt(jsBook1.getString("pageCount"));  //plz server, can u send me this? :D
            return book;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private SubSection[] extractSubsections(String dataString) {

        SubSection[] subSections;

        JSONObject json = null;
        try {
            Log.v(TAG, dataString);
            json = new JSONObject(dataString);
            JSONArray jsonArray = json.getJSONArray("content");

            int len = jsonArray.length();
            subSections = new SubSection[len];

            for (int i = 0; i < len; i++) {
                JSONObject jo = (JSONObject) jsonArray.get(i);
                SubSection subSection = new SubSection();

                subSection.title = jo.getString("title");
                subSection.pid = jo.getString("pid");
                subSection.bookCount = Integer.parseInt(jo.getString("totalBooks"));

                subSections[i] = subSection;
            }

            return subSections;
        } catch (Exception e) {
            Log.e(TAG, "Something wrong found on data sent from server::: " + e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
    }


    private Book[] extractBookList(String dataString) {
        Book[] books;

        JSONObject json = null;
        try {
            Log.v(TAG, dataString);
            json = new JSONObject(dataString);
            JSONArray jsonArray = json.getJSONArray("content");

            int len = jsonArray.length();
            books = new Book[len];

            for (int i = 0; i < len; i++) {
                JSONObject jo = (JSONObject) jsonArray.get(i);
                Book book = new Book();

                book.title = jo.getString("title");
                book.coverImageURL = jo.getString("bookCover");
                book.pid = jo.getString("pid");
                book.author = jo.getString("author");
                book.publisher = jo.optString("publisher","publisher");
                book.views = Integer.parseInt(jo.getString("view"));
                book.downloads = Integer.parseInt(jo.getString("download"));

                books[i] = book;
            }

            return books;
        } catch (Exception e) {
            Log.e(TAG, "Something wrong fond on data sent from server::: " + e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
    }

    private Book[] extractLatestBookList(String dataString) {
        Book[] books;

        JSONObject json = null;
        try {
            Log.v(TAG, dataString);
            json = new JSONObject(dataString);
            JSONArray jsonArray = json.getJSONArray("content");

            int len = jsonArray.length();
            books = new Book[len];

            for (int i = 0; i < len; i++) {
                JSONObject jo = (JSONObject) jsonArray.get(i);
                Book book = new Book();

                book.title = jo.getString("title");
                book.coverImageURL = jo.getString("bookCover");
                book.pid = jo.getString("pid");

                books[i] = book;
            }

            return books;
        } catch (Exception e) {
            Log.e(TAG, "Something wrong fond on data sent from server::: " + e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
    }

    /*roshan on*/
    private Book[] extractEditorsPickBooks(String dataString) {
        Book[] books;
        JSONObject json = null;
        try {
            Log.v(TAG, dataString);
            json = new JSONObject(dataString);
            JSONArray jsonArray = json.getJSONArray("content");

            int len = jsonArray.length();
            books = new Book[len];

            for (int i = 0; i < len; i++) {
                JSONObject jo = (JSONObject) jsonArray.get(i);
                Book book = new Book();
                book.coverImageURL = jo.getString("bookCover");
                book.pid = jo.getString("pid");
                books[i] = book;
            }

            return books;
        } catch (Exception e) {
            Log.e(TAG, "Something wrong fond on data sent from server::: " + e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
    }
    /*roshan off*/

    private String getDataStringFromUrl(String myUrl) {
        if(!Utility.isConnected(context)){
            return null;
        }

        Log.v("SERVER", myUrl);

        if(!Patterns.WEB_URL.matcher(myUrl).matches()){
            Log.v(TAG,"Invalid URL,");
//            Utility.showAlert(context,"Provided server URL is invalid. Please verify it from app's setting page.");
            return null;
        }

        HttpParams httpParameters = new BasicHttpParams();
// Set the timeout in milliseconds until a connection is established.
// The default value is zero, that means the timeout is not used.
        int timeoutConnection = 3000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
// Set the default socket timeout (SO_TIMEOUT)
// in milliseconds which is the timeout for waiting for data.
        int timeoutSocket = 5000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        HttpClient httpClient = new DefaultHttpClient(httpParameters);

        HttpGet httpGet = new HttpGet(myUrl);
        HttpResponse response;

        try {
            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                InputStream inputStream = entity.getContent();
                String result = convertInputToStream(inputStream);

                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private String convertInputToStream(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();

        String line = null;

        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * pdf downloader
     * */
    public File downloadFile(Book book, NotificationCompat.Builder mBuilder, NotificationManager notificationManager) {

        String dest_file_path = book.pdfFileURL;
        int downloadedSize = 0, totalsize;
        String download_file_url = null;
        float per = 0;



        download_file_url = getBase_URL()+"eserv.php?pid="+book.pid+"&dsID="+book.pdfFileURL+"&mobile=1337";

        File file = null;
        try {

            URL url = new URL(download_file_url);
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);

            // connect
            urlConnection.connect();

            // set the path where we want to save the file
            File folder = new File(Environment.getExternalStorageDirectory()+"/Epustakalaya/pdf");
            Log.e("path",""+folder);

            if (!folder.exists()){
                folder.mkdirs();
            }
            // create a new file, to save the downloaded file
            file = new File(folder, dest_file_path);

            FileOutputStream fileOutput = new FileOutputStream(file);

            // Stream used for reading the data from the internet
            InputStream inputStream = urlConnection.getInputStream();

            // this is the total size of the file which we are
            // downloading
            totalsize = urlConnection.getContentLength();


            // create a buffer...
            byte[] buffer = new byte[1024 * 1024];
            int bufferLength = 0;

            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;
                per = ((float) downloadedSize / totalsize) * 100; //calculating downloaded size
                //notification progress
                mBuilder.setProgress(100, (int) per,false);
                notificationManager.notify(1,mBuilder.build());
            }

            /**
             * notification complete
             * */
            mBuilder.setContentText("Download complete")
                    .setProgress(0,0,false);
            notificationManager.notify(1,mBuilder.build());

            // close the output stream when complete //
            fileOutput.close();

        } catch (final MalformedURLException e) {

        } catch (final IOException e) {

        } catch (final Exception e) {

        }
        return file;
    }


}

