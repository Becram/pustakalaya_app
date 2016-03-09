package com.ole.epustakalaya;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.ExceptionReporter;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ole.epustakalaya.helper.MyBooksDB;
import com.ole.epustakalaya.helper.Preference;
import com.ole.epustakalaya.helper.ServerSideHelper;
import com.ole.epustakalaya.helper.Utility;
import com.ole.epustakalaya.helper.subSecdb;
import com.ole.epustakalaya.interfacesAndAdaptors.BookFetchListener;
import com.ole.epustakalaya.interfacesAndAdaptors.GetBooksAsync;
import com.ole.epustakalaya.models.Book;
import com.ole.epustakalaya.models.PustakalayApp;
import com.ole.epustakalaya.models.Section;
import com.ole.epustakalaya.models.SubSection;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity  implements ExpandableListView.OnChildClickListener, BookFetchListener {

    private static String TAG = "MyActivity";

    private ServerSideHelper serverSideHelper;

    private DrawerLayout drawerLayout;
    private Section[] sections;
    public ProgressDialog progressDialog;
    /*side drawer*/
    private ExpandableListView explistView;
    private SideAdaptor explistviewdrawerAdapter;
    private Context context;
    private ActionBarDrawerToggle bookDrawerListener;
    private LinearLayout splash;
    android.support.v7.app.ActionBar actionBar;
    private Book[] editorsPicks;
    private int countSplash=0;

    private HomepageFragment homepageFragment;
    private BookListFragment bookListFragmentFrag;

    private ProgressDialog pdLoading;

    Tracker tracker;

    public boolean amIExpectedToHideSearch;

    private Menu menu;

    boolean isSearchedFirstTime = true;
    boolean isInBackground=false;

    /*store current subsection pid in this variable*/
    private String current_subsection_pid;
    private String last_searched_string = null;

    private static WeakReference<MainActivity> wrActivity = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //bikram

        this.setTheme(R.style.myTheme);
        Log.v(TAG,"Before onCreate super call");

        /*
        * This may be the wrong workaround!!!
        * It demands more research..
        * But for now, if it solves the problem, then it is a solution :D
        * */
        if(savedInstanceState!=null){
            Log.v(TAG,"Something is saved for me but i don't want it anymore");
            super.onCreate(null);
        } else {
            Log.v(TAG,"Fresh Install");
            super.onCreate(savedInstanceState);
        }

        isInBackground = false;

        wrActivity = new WeakReference<MainActivity>(this);

        setContentView(R.layout.activity_my);
        this.context = getApplicationContext();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        splash = (LinearLayout) findViewById(R.id.splash);

        bookListFragmentFrag = new BookListFragment();

        serverSideHelper = new ServerSideHelper(this.context);
        progressDialog = new ProgressDialog(MainActivity.this);

        /*roshan on*/
//        actionBar =getActionBar(); //get action bar to customize urself 33B5E5
        actionBar = getSupportActionBar();
        actionBar.hide();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33B5E5")));
        explistView=(ExpandableListView) findViewById(R.id.drawerList);
        explistView.setDividerHeight(2);
        explistView.setGroupIndicator(null);
        explistView.setClickable(true);
         /*roshan off*/

        // Get tracker.
        tracker = ((PustakalayApp) getApplication()).getTracker(
                PustakalayApp.TrackerName.APP_TRACKER);

        Thread.UncaughtExceptionHandler myHandler = new ExceptionReporter(
                tracker,                                        // Currently used Tracker.
                Thread.getDefaultUncaughtExceptionHandler(),      // Current default uncaught exception handler.
                context);                                         // Context of the application.

// Make myHandler the new default uncaught exception handler.
        Thread.setDefaultUncaughtExceptionHandler(myHandler);

//        Utility.crashMe();

        /*roshan on*/
        /*check if subsection database exists and condition yourself accordingly*/
        if(subsecdatabaseExist()){
            Log.w("database", "database exists");
            extractsectionfromdatabase();
        }
        else{
            Log.w("do in background get sections async: ", "database does not exist");
            if(!(Utility.isConnected(context) && Utility.isServerAlive(context)) ){
//            if(!Utility.isConnected(context) ){
                if(!Utility.isConnected(context)) {
                    Log.v("Connection","Not connected to any network");
                }
                    try {
                    Log.w("database from assets folder", "copying from database");

                    copyDatabaseFromAssets();
                    Log.w("database from assets folder", "copy complete from database");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.w("database from assets folder", "database exists");
                extractsectionfromdatabase();
            }  else{
                new GetSectionsAsync().execute();
            }
        }
        /*roshan off*/


        bookDrawerListener = new ActionBarDrawerToggle(this,drawerLayout,R.drawable.menu,R.string.drawer_open,R.string.drawer_close){
            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);

            }

            @Override
            public void onDrawerOpened(View drawerView) {

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    super.onDrawerOpened(drawerView);
                    invalidateOptionsMenu();
                }
            }
        };

        drawerLayout.setDrawerListener(bookDrawerListener);
//        getActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*roshan on*/
         /*add fragment*/
        this.homepageFragment = new HomepageFragment();

        Bundle args = new Bundle();
        args.putString("splash","splash");

        homepageFragment.supportFragmentManager = getSupportFragmentManager();
        homepageFragment.setArguments(args);

        homepageFragment.ownBooks = Utility.getAllDownloadedBook(this.context);

            if(countSplash<1){
//                this.mainPage.latestBooks = getLatestBooksAsync.execute(1).get();
                new GetBooksAsync(context,this,BookTypeEditorsPicks).execute();
                countSplash++;
            } else {
                fragDo(homepageFragment,R.id.container,"MainPage");
            }

    }

    @Override
    protected void onSaveInstanceState(Bundle icicle) {
        super.onSaveInstanceState(icicle);
        isInBackground = true;

    }

    public void presentBookDetailFragment(Context context,Book book) {

        Intent intent = new Intent(context,BookDetailsActivity.class);
        intent.putExtra("bookPid", book.pid);
        startActivity(intent);
    }

    public void fragDo(Fragment frag,int r,String str){
    // to solve this
  //      java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
// check whther activity is forground or not and the only
        if(isInBackground){
            //nothing can be done
            return;
        }
        String backStateName = frag.getClass().getName();

        if ((wrActivity.get() != null) && (wrActivity.get().isFinishing() != true)) {
        android.support.v4.app.FragmentManager manager = wrActivity.get().getSupportFragmentManager();
//      FragmentManager manager = getFragmentManager();

        boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);

        if (!fragmentPopped) { //fragment not in back stack, create it.
            android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(r, frag, str);
            transaction.addToBackStack(backStateName);
            transaction.commitAllowingStateLoss();
        }

        }
    }

    @Override
    public void onResume(){
        Log.v(TAG, "Resumed from background");
        super.onResume();
        isInBackground = false;
        //they says we need to handle background to foreground
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        bookDrawerListener.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        bookDrawerListener.syncState();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        if(amIExpectedToHideSearch){
            MenuItem menuItem = menu.findItem(R.id.action_search);
            menuItem.setVisible(false);
        } else {
            MenuItem menuItem = menu.findItem(R.id.action_search);
            menuItem.setVisible(true);
        }

        this.menu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = drawerLayout.isDrawerOpen(GravityCompat.START);
        MenuItemCompat.collapseActionView(menu.findItem(R.id.action_search));
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item){

        /*Log.i("mjt :o " ,item.getItemId()+"");*/
        if(bookDrawerListener.onOptionsItemSelected(item)){
            return true;
        }

        /*roshan on*/
        /*define a new intent object and new preference object*/
        Intent intent;
        Preference preference = new Preference(getApplicationContext());
        //tets
        /*get current action bar details*/
        ActionBar listbookactionbar = getSupportActionBar();
        String listbooktitle = (String) listbookactionbar.getTitle();
        String listbooksubtitle = (String) listbookactionbar.getSubtitle();

        switch (item.getItemId()){
            case R.id.action_settings:
                intent = new Intent(this,SettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
                break;

            case R.id.action_about_us:
                intent = new Intent(this,AboutUsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
                break;

            case R.id.author_sort:
                item.setChecked(true);
                break;

            case R.id.title_sort:
                item.setChecked(true);
                break;

            case R.id.date_sort:
                item.setChecked(true);
                break;

            case R.id.author_ascending:
                item.setChecked(true);
                //write to the preference keySortBy keySortOrder
                preference.setSortField("author");
                preference.setSortOrder("asc");
                //Call getBookAsync with new url
                new GetBookListAsync(listbooktitle,listbooksubtitle,true).execute(current_subsection_pid);
                break;

            case R.id.author_descending:
                item.setChecked(true);
                //write to the preference keySortBy keySortOrder
                preference.setSortField("author");
                preference.setSortOrder("dsc");
                //Call getBookAsync with new url
                new GetBookListAsync(listbooktitle,listbooksubtitle,true).execute(current_subsection_pid);
                break;

            case R.id.title_ascending:
                item.setChecked(true);
                //write to the preference keySortBy keySortOrder
                preference.setSortField("title");
                preference.setSortOrder("asc");
                //Call getBookAsync with new url
                new GetBookListAsync(listbooktitle,listbooksubtitle,true).execute(current_subsection_pid);
                break;

            case R.id.title_descending:
                item.setChecked(true);
                //write to the preference keySortBy keySortOrder
                preference.setSortField("title");
                preference.setSortOrder("dsc");
                //Call getBookAsync with new url
                new GetBookListAsync(listbooktitle,listbooksubtitle,true).execute(current_subsection_pid);
                break;

            case R.id.date_ascending:
                item.setChecked(true);
                item.setChecked(true);
                //write to the preference keySortBy keySortOrder
                preference.setSortField("date");
                preference.setSortOrder("asc");
                //Call getBookAsync with new url
                new GetBookListAsync(listbooktitle,listbooksubtitle,true).execute(current_subsection_pid);
                break;

            case R.id.date_descending:
                item.setChecked(true);
                //write to the preference keySortBy keySortOrder
                preference.setSortField("date");
                preference.setSortOrder("dsc");
                //Call getBookAsync with new url
                new GetBookListAsync(listbooktitle,listbooksubtitle,true).execute(current_subsection_pid);
                break;

            case R.id.action_search:
                /*Log.i("search_action :o ", "action_search_clicked");*/

                /*if navigation drawer is open please close*/
                if(drawerLayout.isDrawerOpen(GravityCompat.START))
                {
                    drawerLayout.closeDrawers();
                }

                /*see the MenuItemCompat- it is used to handle search view for getting action view and collapsing action view for lower android versions*/
                final android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(item);
                searchView.setQueryHint(getString(R.string.search_hint));

                /*to display the users last search query string*/
                if(last_searched_string != null){
                    searchView.setQuery(last_searched_string,false);
                }

                // To automatically hide keyboard when keyboard  is hidden
                searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        Log.d(TAG, "mSearchView focus changed: " + hasFocus);
                        if (!hasFocus) {
                            MenuItemCompat.collapseActionView(item);
                        }
                    }
                });

                /*actions on typing over the search widget and clicking search ime button*/
                searchView.setOnQueryTextListener( new android.support.v7.widget.SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        /*Log.i("search_action :o " ,"search_button_clicked");*/

                        MenuItemCompat.collapseActionView(item);
                        last_searched_string = s;
                        if(!Utility.isConnected(context)){
                            Utility.giveNoConnectionMessage(context);
                            return true;
                        }

                        tracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Click")  // category i.e. Player Buttons
                                .setAction("Search")    // action i.e.  Play
                                .setLabel(""+s)    // label i.e.  any meta-data
                                .build());

                        new GetSearchedBookListAsysnc(s,isSearchedFirstTime).execute();
                        if(isSearchedFirstTime){
                            isSearchedFirstTime = false;
                        }
                        return true;
                    }
                    @Override
                    public boolean onQueryTextChange(String s) {
                       /* Log.i("search_action :o " ,"typing...");*/
                        return false;
                    }
                });
                break;


            default:
                return super.onOptionsItemSelected(item);
                }
        /*roshan off*/
        return true;
    }

    @Override
    public void onBackPressed() {
        Log.v(TAG, "Back Pressed");
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawers();
            return;
        }

        if (getSupportFragmentManager().getBackStackEntryCount()>1){
            getSupportFragmentManager().popBackStack();

        } else {
//            finish();
            moveTaskToBack(true);
            return;
        }
    }

    public void homeClicked(View v){
        //go to home
//        actionBar = getActionBar();
        actionBar.setTitle(R.string.app_name);
        actionBar.setSubtitle(null);

        this.homepageFragment = new HomepageFragment();
        pdLoading = new ProgressDialog(this);
        pdLoading.setMessage("Loading...");
        pdLoading.setCancelable(false);
        pdLoading.setCanceledOnTouchOutside(false);
        pdLoading.show();

        new GetBooksAsync(context,this,BookTypeEditorsPicks).execute();


        homepageFragment.supportFragmentManager = getSupportFragmentManager();
        homepageFragment.supportFragmentManager.popBackStack(null, getSupportFragmentManager().POP_BACK_STACK_INCLUSIVE);
        /*frag.editorspicks = editorsPicks;*/
        drawerLayout.closeDrawers();
    }

    /*roshan on*/

//    populate sections on menu
    void populateSectionsOnMenu(Section[] sections){
        /*Log.w("from populate section menu: ", "now i can populate sections menu");*/
        explistviewdrawerAdapter = new SideAdaptor(sections,explistView,this.context,actionBar);
        explistView.setAdapter(explistviewdrawerAdapter);
    }


//    check if subsection database exists
    public boolean subsecdatabaseExist(){
        String dbFilepath = context.getDatabasePath("subsec_db").toString();
        /*Log.w("From db exist - Db file path",dbFilepath);*/
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(dbFilepath, null,
                    SQLiteDatabase.OPEN_READONLY);
            checkDB.close();
        } catch (SQLiteException e) {
            // database doesn't exist yet.
        }

        return checkDB != null ? true : false;
    }

//    populate subsection database when called server for sections extraction
    public void populatesubsectionsondatabase(Section[] sections){
        subSecdb db = new subSecdb(this.context);

//        int subsecrowCount;

//        following for loop adds the subsections to the database
            Log.w("DATABASE POPULATE: ", "database is being populated since it is the fresh install");
            for(Section sec : sections){
                for(SubSection subSec : sec.subSections){
                    db.addsubsection(sec.pid, subSec.pid, subSec.title,subSec.bookCount);
                }
            }

//        more of a log for counting all the rows in subsection database
        /*subsecrowCount = db.getSubseccount(null);
        Log.e("subsectioncount","count = "+subsecrowCount);*/

//        extract subsections from database
        extractsectionfromdatabase();
    }

//    extract sections from database
    public void extractsectionfromdatabase(){
        subSecdb db = new subSecdb(this.context);
        sections = db.getSectionsFromDb();
        populateSectionsOnMenu(sections);
    }

    // copy your assets db to the new system DB
    private void copyDatabaseFromAssets() throws IOException{
        //By calling this method and empty database will be created into the default system path
        //of your application so we are gonna be able to overwrite that database with our database.

        String ASSETDB = "database/subsec_db";
        String OUTPUT_DB_FILE = "subsec_db";
        String APP_DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        /*Log.w("FROM COPY DATABASE - ",APP_DB_PATH);*/
        SQLiteDatabase emptydatabase = null;
        emptydatabase = this.openOrCreateDatabase(APP_DB_PATH+OUTPUT_DB_FILE,context.MODE_PRIVATE,null);
        //Open your local db as the input stream
        InputStream myInput = context.getAssets().open(ASSETDB);

        // Path to the just created empty db
        String outFileName = APP_DB_PATH + OUTPUT_DB_FILE;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
        emptydatabase.close();
    }
    /*roshan off*/

    public void clickedSubSections(String pid, String subtitle, String title) {
        drawerLayout.closeDrawers();
        /*roshan on*/
        new GetBookListAsync(title,subtitle).execute(pid);
        /*roshan off*/
    }
    @Override
    protected void onPause(){
        super.onPause();
      if(progressDialog!=null){
          progressDialog.dismiss();
      }
    }

    @Override
    public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i2, long l) {
        return false;
    }

    @Override
    public void onFetchedBookReceived(Book[] books, int BooksType) {
        if(books == null){
           // Toast.makeText(context,"Seems like you  are not connected to Internet.",Toast.LENGTH_SHORT).show();
            Utility.giveNoConnectionMessage(context);
            fragDo(homepageFragment,R.id.container,"MainPage");
            if(pdLoading!=null){
                pdLoading.dismiss();
            }
            return;
        }
        if(BookTypeEditorsPicks == BooksType){
            this.homepageFragment.editorspicks = books;
            Log.v(TAG,books.length+ " Editors Picks fetched Now latest books is fetching");
            new GetBooksAsync(context,this,BookTypeLatestBooksFirstLoad,1).execute();
        } else if(BookTypeLatestBooksFirstLoad == BooksType){
            this.homepageFragment.latestBooks = books;
            //then present the fragment now
            fragDo(homepageFragment,R.id.container,"MainPage");
            if(pdLoading!=null){
                pdLoading.dismiss();
            }
        }
    }

    //    getting and populating the navigation drawer expandable list view
    private class GetSectionsAsync extends AsyncTask<Void , Void, Void> {
        Section[] sections;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            sections = serverSideHelper.getSections();
            return null;
        }

        @Override
        protected void onPostExecute(Void results) {
            super.onPostExecute(results);

            /*roshan on*/

            populatesubsectionsondatabase(sections);
//            progressDialog.dismiss();
            /*roshan off*/
        }
    }

    /*get the book list*/
    private class GetBookListAsync extends AsyncTask<String , Void, String> {
        Book[] fetchedBooks;
        private String title,subtitle;
        boolean justReload;                 //For the Sort one?

        public GetBookListAsync(String title, String subtitle){
            this.title = title;
            this.subtitle = subtitle;
            this.justReload = false;
        }

        public GetBookListAsync(String title, String subtitle,boolean justReload){
            this.title = title;
            this.subtitle = subtitle;
            this.justReload = justReload;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        progressDialog.setMessage("loading...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected String doInBackground(String... params) {
            Log.v(TAG,"What is this");
            fetchedBooks = serverSideHelper.getBookList(params[0]);
            return params[0];
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            current_subsection_pid = result;

            if(fetchedBooks==null){
                Toast.makeText(getApplicationContext(),"No Connection...",Toast.LENGTH_SHORT).show();
                return;
            }

            if(bookListFragmentFrag.isVisible()){
                bookListFragmentFrag.isFromSearch = false;
                bookListFragmentFrag.updateTheList(fetchedBooks, title, subtitle);
                return;
            }

            actionBar.setTitle(title);
            actionBar.setSubtitle(subtitle);
            bookListFragmentFrag.books = fetchedBooks;
            bookListFragmentFrag.isFromSearch = false;

            fragDo(bookListFragmentFrag,R.id.container,"mainPage");

            /*when the book list is seen set the current_subsection_pid*/


        }
    }


    //Get Search BookListAsync
    private class GetSearchedBookListAsysnc extends AsyncTask<String , Void, String> {
        Book[] searchedBooks;
        private String keyword;
        boolean isFirstTime;

        public GetSearchedBookListAsysnc(String keyword, boolean isSearchedFirstTime){
            this.keyword = keyword;
            this.isFirstTime = isSearchedFirstTime;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Searching...");
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected String doInBackground(String... params) {
            Log.v(TAG,"What is this");
            searchedBooks = serverSideHelper.getSearchedBookList(keyword);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if(searchedBooks==null){
                Toast.makeText(getApplicationContext(),"No Books Found.",Toast.LENGTH_LONG).show();
                return;
            }
            if( bookListFragmentFrag.isVisible()){
                bookListFragmentFrag.isFromSearch = true;
                bookListFragmentFrag.updateTheList(searchedBooks,"Search",keyword);
                return;
            }

            actionBar.setTitle("Search");
            actionBar.setSubtitle(keyword);
            bookListFragmentFrag.books = searchedBooks;
            bookListFragmentFrag.isFromSearch = true;

            fragDo(bookListFragmentFrag,R.id.container,"mainPage");
                // we need to remove the sort button
                // But i think, inserting pagination and adding sort feature on searched book will be nice rather
                // What do you say?


        }
    }

    /*
    * adaptor for drawer
    * */

    class SideAdaptor extends BaseExpandableListAdapter
    {
        private ArrayList<Object> childItems;
        private ArrayList<String> parentItems,child;
        private ExpandableListView expandableListView;
        private Context context;
        private android.support.v7.app.ActionBar actionBar;
        private int lastExpandedGroupPosition = -1;
        private View lastchildview = null;
        private Section[] sections;

        public SideAdaptor(Section[] sections,ExpandableListView expandableListView, Context context,android.support.v7.app.ActionBar actionBar){
            this.sections = sections;
            this.expandableListView = expandableListView;
            this.actionBar = actionBar;
            this.context = context;
            this.parentItems = new ArrayList<String>();
            this.childItems = new ArrayList<Object>();

            for(Section sec : sections){
                System.out.println(sec);

                this.parentItems.add(sec.title);
                ArrayList<String> child = new ArrayList<String>();
                for(SubSection subSec : sec.subSections){
    //                System.out.println(subSec);
                    child.add(subSec.title);
                }
                this.childItems.add(child);
            }
        }

        @Override
        public int getGroupCount() {
            return parentItems.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return ((ArrayList<String>) childItems.get(groupPosition)).size();
        }

        @Override
        public Object getGroup(int i) {
            return null;
        }

        @Override
        public Object getChild(int i, int i2) {
            return null;
        }

        @Override
        public long getGroupId(int i) {
            return 0;
        }

        @Override
        public long getChildId(int i, int i2) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            View row = null;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.parent_view,null,false);

            } else {
                row = convertView;
            }

            TextView childCount = (TextView) row.findViewById(R.id.childcount);
            CheckedTextView textViewGroupName = (CheckedTextView) row.findViewById(R.id.textViewGroupName);

            childCount.setText(""+getChildrenCount(groupPosition));
            textViewGroupName.setText(parentItems.get(groupPosition));
            textViewGroupName.setChecked(isExpanded);

            return row;
        }

        @Override
        public View getChildView(final int groupPosition,final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            this.child = (ArrayList<String>) this.childItems.get(groupPosition);
            TextView textView = null;
            View row = null;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.child_view,null,false);

            } else {
                row = convertView;
            }

            textView = (TextView) row.findViewById(R.id.textViewChild);
            textView.setText(child.get(childPosition));

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String tag = ""+view.getTag();
    //                new GetBookListAsync().execute(tag);


                    if (lastchildview!= null){
                        lastchildview.setBackgroundColor(Color.parseColor("#E7E8E9"));
                    }
                    lastchildview = view;
                    view.setBackgroundColor(Color.parseColor("#5490CC"));

                    /*roshan on*/
                    /*send the title and subtitle as params*/
                    clickedSubSections(tag,parentItems.get(groupPosition),child.get(childPosition));
                    /*roshan off*/
                }
            });

            row.setTag(sections[groupPosition].subSections[childPosition].pid);
            return row;
        }

        @Override
        public boolean isChildSelectable(int i, int i2) {
            return false;
        }

        @Override
        public void onGroupCollapsed(int groupPosition) {
            actionBar.setTitle(R.string.app_name);
            actionBar.setSubtitle(null);
            super.onGroupCollapsed(groupPosition);
        }

        @Override
        public void onGroupExpanded(int groupPosition) {
            super.onGroupExpanded(groupPosition);

            /*roshan on*/
            actionBar.setTitle(R.string.app_name);
            actionBar.setSubtitle(null);
            /*actionBar.setTitle(parentItems.get(groupPosition));*/
            /*roshan off*/

            if (lastExpandedGroupPosition!= -1 && lastExpandedGroupPosition != groupPosition){
                actionBar.setSubtitle(null);
                expandableListView.collapseGroup(lastExpandedGroupPosition);
            }
            lastExpandedGroupPosition = groupPosition;

        }
    }

}