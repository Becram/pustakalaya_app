package com.ole.epustakalaya;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ole.epustakalaya.helper.MyBooksDB;
import com.ole.epustakalaya.helper.ServerSideHelper;
import com.ole.epustakalaya.helper.Utility;
import com.ole.epustakalaya.models.Book;
import com.ole.epustakalaya.models.PustakalayApp;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by bishnu on 11/13/14.
 */
public class BookDetailsActivity extends ActionBarActivity{

        private static String TAG="BookDetailsActivity";
    private static int PDF_OPENER =232;

        public Book book;
        private Context context;

        private ServerSideHelper serverSideHelper;

        private TextView tvTitle;
        private TextView tvAuthor;
        private TextView tvPublisher;
        private TextView tvPlaceOfPublication;
        private TextView tvLang;
        private TextView tvPageCount;
        private TextView tvDownloadCount, tvViewCount;
        private TextView tvSummary;
        private TextView tvFilesize;
        private LinearLayout llFileSize;
        private ImageView ivBookCoverPic;
        private Button btnDownloadOpen;
        private Button btnDelete;
        private LinearLayout llButtonsContainer;

        private Tracker tracker;

        private boolean isActivityOpen;
        private boolean isFileExist; //to change the download button to open and to show the delete button

        private ProgressDialog pdRuning;
        private AlertDialog alertDeleteDialog;

        DownloadManager downloadManger;
        private long myDownloadReference;
        private BroadcastReceiver downloadCompleteBroadcastReceiver;
        int placeholder = R.drawable.book_not_available;

    /*
    * pdf download variables
    * */

        @Override
        public void onCreate(Bundle savedInstanceState) {
           this.setTheme(R.style.myTheme);

            super.onCreate(savedInstanceState);
            Utility.createPdfDir();
            setContentView(R.layout.book_details_fragment);
            this.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#60B4E5")));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            context = getApplicationContext();

            tracker = ((PustakalayApp) getApplication()).getTracker(
                    PustakalayApp.TrackerName.APP_TRACKER);

            tvTitle = (TextView) findViewById(R.id.tvTitle);
            tvAuthor = (TextView) findViewById(R.id.tvAuthor);
            tvPublisher = (TextView) findViewById(R.id.tvPublisher);
            tvPlaceOfPublication = (TextView) findViewById(R.id.tv_placeOfPublication);
            tvLang = (TextView) findViewById(R.id.tv_lang);
            tvPageCount = (TextView) findViewById(R.id.tv_PageCount);
            tvDownloadCount = (TextView) findViewById(R.id.tvDownloadCount);
            tvViewCount = (TextView) findViewById(R.id.tvViewsCount);
            tvSummary = (TextView) findViewById(R.id.tvSummary);
            llFileSize = (LinearLayout) findViewById(R.id.llFilesizeId);
            tvFilesize = (TextView) findViewById(R.id.tv_filesize);
            ivBookCoverPic = (ImageView) findViewById(R.id.ivCoverImage);
//        btnDownload = (Button) view.findViewById(R.id.btnDownload);
            llButtonsContainer = (LinearLayout) findViewById(R.id.bookButtons);

            btnDownloadOpen = (Button) findViewById(R.id.btnDownloadOpen);
            btnDelete = (Button) findViewById(R.id.btnDelete);

            serverSideHelper = new ServerSideHelper(this.context);
            pdRuning = new ProgressDialog(this);
            alertDeleteDialog = new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Delete Book")
                    .setMessage("Do you want to delete this book?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                                    tracker.send(new HitBuilders.EventBuilder()
                                            .setCategory("Clicks")  // category i.e. Player Buttons
                                            .setAction("delete")    // action i.e.  Play
                                            .setLabel("delete clicked from book details")    // label i.e.  any meta-data
                                            .build());
                                    deleteBook(book);
                        }
                    })
                    .setNegativeButton("No",null).create();

            downloadManger = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   alertDeleteDialog.show();
                }
            });

            btnDownloadOpen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    File pdfFile = new File(Utility.pdfDirectory+book.pdfFileURL);
                    if (pdfFile.exists()){
                        Uri uriPath = Uri.fromFile(pdfFile);
                        openPDFfile(uriPath);
                    } else {
                        Toast.makeText(context,"Oh! The file doesn't exist. We are trying to redownload it",Toast.LENGTH_SHORT).show();
                        downloadPDF();
                    }

                }
            });




            //register the broadcast receiver
            IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            downloadCompleteBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    //do something with downloaded file
                    if(myDownloadReference == reference){
                        DownloadManager.Query query = new DownloadManager.Query();
                        query.setFilterById(reference);
                        Cursor cursor = downloadManger.query(query);

                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        int status = cursor.getInt(columnIndex);
                        int fileNameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
//                    String savedFilePath = cursor.getString(fileNameIndex);
                        int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
//                    int reason  = cursor.getInt(columnReason);

                        //switching status
                        switch (status){
                            case DownloadManager.STATUS_SUCCESSFUL:
                                Toast.makeText(context, "Book Downloaded Successfully", Toast.LENGTH_SHORT).show();
//                            dbWrite();
                                new MyBooksDB(context).deleteBook(book);

                                if(isActivityOpen) {
                                    showSuitableButtons(true);
                                }
                                break;
                            case DownloadManager.STATUS_FAILED:
                                Toast.makeText(context,"Download Failed ",Toast.LENGTH_SHORT).show();
//                            Toast.makeText(context,"Reason: " + cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON)),Toast.LENGTH_SHORT).show();
                                break;
                            case DownloadManager.STATUS_RUNNING:
//                            Toast.makeText(context,"Download Running ",Toast.LENGTH_SHORT).show();
                                break;
                            case DownloadManager.STATUS_PAUSED:
                                Toast.makeText(context,"Download Paused ",Toast.LENGTH_SHORT).show();
                                break;
                            case DownloadManager.STATUS_PENDING:
                                Toast.makeText(context,"Download Pending ",Toast.LENGTH_SHORT).show();
                                break;
                        }

                    }
                }
            };

            context.registerReceiver(downloadCompleteBroadcastReceiver,intentFilter);

            Intent intent = getIntent();
            if(intent!=null){
                book = new Book();
                book.pid=intent.getStringExtra("bookPid");
            }
            if(book!=null && book.pid!=null) {
                new GetBookDetailsAsync().execute(book.pid);
            } else {
                Log.v(TAG, "Why book is null?");
                Toast.makeText(context,"Sorry. Something unexpected happened.",Toast.LENGTH_SHORT).show();
            }

        }


        public void showSuitableButtons(boolean fileExist) {
            if(fileExist){
                //show Open button and show delete button
                btnDelete.setVisibility(View.VISIBLE);
                btnDownloadOpen.setVisibility(View.VISIBLE);
                btnDownloadOpen.setText("Open");

                btnDownloadOpen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Tracker t = ((PustakalayApp) getApplication()).getTracker(
                                PustakalayApp.TrackerName.APP_TRACKER);
                        t.send(new HitBuilders.EventBuilder()
                                .setCategory("Clicks")  // category i.e. Player Buttons
                                .setAction("open")    // action i.e.  Play
                                .setLabel("open clicked from book details")    // label i.e.  any meta-data
                                .build());
                        File pdfFile = new File(Utility.pdfDirectory+book.pdfFileURL);

                        if (pdfFile.exists()){
                            Uri uriPath = Uri.fromFile(pdfFile);
                            Log.v("path",""+uriPath);

                            openPDFfile(uriPath);
                        } else {
                            Toast.makeText(context,"Oh! The file doesn't exist. We are trying to redownload it",Toast.LENGTH_SHORT).show();
                            downloadPDF();
                        }

                    }
                });

            } else {
                //show download button only
                btnDelete.setVisibility(View.GONE);
                btnDownloadOpen.setText("Download/read");
                btnDownloadOpen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(context,"downloading..",Toast.LENGTH_SHORT).show();

                        Tracker t = ((PustakalayApp) getApplication()).getTracker(
                                PustakalayApp.TrackerName.APP_TRACKER);

                        t.send(new HitBuilders.EventBuilder()
                                .setCategory("Clicks")  // category i.e. Player Buttons
                                .setAction("downld")    // action i.e.  Play
                                .setLabel("downld clicked from book details")    // label i.e.  any meta-data
                                .build());
                        downloadPDF();
                    }
                });
            }

        }

        @Override
        public void onResume()
        {
            super.onResume();
            IntentFilter filter = new IntentFilter();
            filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
//        ((MainActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(false);
//        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            isActivityOpen = true;
        }

        @Override
        public void onStop(){
            super.onStop();

        }

        @Override
        public void onPause(){
            super.onPause();
            if(pdRuning!=null){
                pdRuning.dismiss();
            }
            //commenting this will enable app receive the broadcast even when it is in background but what if app is killed??
//        context.unregisterReceiver(downloadCompleteBroadcastReceiver);
            isActivityOpen = false;
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        menuItem.setVisible(false);

        return true;
    }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            Intent intent;
            switch (id){
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
                case android.R.id.home:
                    onBackPressed();
                    break;
                default:
                    break;
            }

            return super.onOptionsItemSelected(item);
        }

        public void populateField(){
            populateField(null);
        }

        public void populateField(Book newBook){
            if(newBook!=null){
                this.book = newBook;
            }
            // Set screen name.
            tracker.setScreenName("book details");

            // Send a screen view.
            tracker.send(new HitBuilders.ScreenViewBuilder().build());


            tvTitle.setText(book.title);
            tvAuthor.setText(book.author);
            tvPublisher.setText(book.publisher);
            tvPlaceOfPublication.setText(book.place);
            tvLang.setText(book.lang);

            tvDownloadCount.setText(book.downloads+"");
            tvViewCount.setText(book.views+"");

            if(book.pageCount!=0){
                ((LinearLayout)findViewById(R.id.llPageCountId)).setVisibility(View.VISIBLE);
                tvPageCount.setText(book.pageCount + "");
            }


            if(book.fileSize!=null && !book.fileSize.equalsIgnoreCase("null") && !book.fileSize.equalsIgnoreCase("")){
                llFileSize.setVisibility(View.VISIBLE);
                tvFilesize.setText(Utility.sizeInBytesToReadableString(Integer.parseInt(book.fileSize)));
            }

            if ((book.description == null) || (book.description.equalsIgnoreCase("null"))) {
                tvSummary.setText("Summary not available.");
            } else {
                Log.e("book", "**" + book.description);
                tvSummary.setText(book.description);
            }

            if((new File(Utility.pdfDirectory+book.pdfFileURL)).exists()){
                showSuitableButtons(true);
            } else {
                showSuitableButtons(false);
            }

            getSupportActionBar().setSubtitle(book.title); // setting book subtitle

            File file = new File(Utility.pdfDirectory+book.coverImageURL);
            if(file.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                ivBookCoverPic.setImageBitmap(myBitmap);
            } else {
                /*ImageLoader imageLoader = new ImageLoader(context);
                imageLoader.DisplayImage(new ServerSideHelper(context).getBase_URL()+ServerSideHelper.BOOK_COVER_PIC+book.coverImageURL,ivBookCoverPic,null);*/
                Picasso.with(context) //
                        .load(new ServerSideHelper(context).getBase_URL()+ServerSideHelper.BOOK_COVER_PIC+book.coverImageURL) //
                        .placeholder(placeholder) //
                        .fit() //
                        .tag(context) //
                        .into(ivBookCoverPic);
            }

            if(book.pdfFileURL==null||book.pdfFileURL.equalsIgnoreCase("null")){
                if(book.externalLink != null || !book.externalLink.equalsIgnoreCase("null")) {

                    btnDownloadOpen.setText("Open Link");
                    String desc = (book.description!=null&&book.description.equalsIgnoreCase("null"))?"":book.description;
                    tvSummary.setText(desc+" \n"+book.externalLink);
                }
            }

        /*ViewGroup.LayoutParams params = ivBookCoverPic.getLayoutParams();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        ivBookCoverPic.setLayoutParams(params);
*/
        }

        public class GetBookDetailsAsync extends AsyncTask<String, Void, Void> {
            @Override
            protected void onPreExecute(){
                super.onPreExecute();
                pdRuning.setMessage("loading...");
                pdRuning.show();
                pdRuning.setCancelable(false);
                pdRuning.setCanceledOnTouchOutside(false);
            }

            @Override
            protected Void doInBackground(String... strings) {
                book = serverSideHelper.getBookDetails(book.pid);
                return null;
            }

            @Override
            protected void onPostExecute(Void res){
                super.onPostExecute(res);
                pdRuning.dismiss();
                if(book!=null) {
                    populateField();
                } else {
                    Toast.makeText(context,"Something wrong happened.",Toast.LENGTH_SHORT).show();
                }
            }
        }

        public void dbWrite(boolean isDownloading){
            MyBooksDB db = new MyBooksDB(this.context);
            book.isDownloading = isDownloading;
            db.addBook(book);
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        void downloadPDF(){

//        ServerSideHelper server = new ServerSideHelper(getActivity().getApplicationContext());
//        server.downloadFile(book,null,null);
            if(book.pdfFileURL==null||book.pdfFileURL.equalsIgnoreCase("null")){
                if(book.externalLink != null || !book.externalLink.equalsIgnoreCase("null")){
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(book.externalLink));
                    startActivity(i);
                } else {
                    Toast.makeText(context,"No pdf or external Link is Available.",Toast.LENGTH_SHORT).show();
                }

                return;
            }

            Uri downloadFileUrl = Uri.parse(new ServerSideHelper(context).getBase_URL()+"eserv.php?pid="+book.pid+"&dsID="+book.pdfFileURL+"&mobile=1337");
            Uri localFileUrl = Uri.fromFile(new File(Utility.pdfDirectory + book.pdfFileURL));
            DownloadManager.Request request = new DownloadManager.Request(downloadFileUrl);
            request.setDescription("Downloading ...")
                    .setTitle(book.title);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                // only for honeycomb
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            }

            //The server need to send the content-length on http response
            //http://stackoverflow.com/questions/26401069/android-download-manager-always-displays-indefinite-progress-bar-and-not-progres
            request.setDestinationUri(localFileUrl);
            request.setVisibleInDownloadsUi(true);
            dbWrite(true);
            myDownloadReference = downloadManger.enqueue(request);
            saveCoverImge();

        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        private void saveCoverImge(){
            Uri imageUrl =  Uri.parse(new ServerSideHelper(context).getBase_URL()+ServerSideHelper.BOOK_COVER_PIC+book.coverImageURL);

            String coverImageName=book.pdfFileURL.substring(0,book.pdfFileURL.length()-4);
            coverImageName += book.coverImageURL.substring(book.coverImageURL.lastIndexOf("."));

            Uri localCoverImageUrl = Uri.fromFile(new File(Utility.pdfDirectory + coverImageName));
            Log.v("IMAGEName",coverImageName);

            DownloadManager.Request request = new DownloadManager.Request(imageUrl);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                // only for honeycomb
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
            }
            request.setDestinationUri(localCoverImageUrl);
            downloadManger.enqueue(request);
        }

        public void openPDFfile(final Uri path){
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Intent nInt = intent.setDataAndType(path, "application/pdf");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        if( Utility.isIntentAvailable(getApplicationContext(),intent)) {
                            startActivity(intent);
                        } else {
                            Utility.showNoPDFReaderAlert(this);
                        }
        }


    private void deleteBook(Book book){
            //delete the coverimage
            File imgFile = new File(Utility.pdfDirectory+book.coverImageURL);
            imgFile.delete();
            //delete the pdf
            File pdfFile = new File(Utility.pdfDirectory+book.pdfFileURL);
            boolean deleted = pdfFile.delete();
            if (deleted){
                Toast.makeText(context, "Book Deleted", Toast.LENGTH_SHORT).show();
//            getFragmentManager().beginTransaction().remove(ListMyBooks.this).commit();
//            populateBooks();
                showSuitableButtons(false);
            }
        }
    }