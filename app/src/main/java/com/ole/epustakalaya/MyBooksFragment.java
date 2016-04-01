package com.ole.epustakalaya;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ole.epustakalaya.helper.MyBooksDB;
import com.ole.epustakalaya.helper.Utility;
import com.ole.epustakalaya.interfacesAndAdaptors.BooksGridAdapter;
import com.ole.epustakalaya.interfacesAndAdaptors.MyBooksGridAdapter;
import com.ole.epustakalaya.models.Book;
import com.ole.epustakalaya.models.PustakalayApp;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by roshan on 17/10/14.
 */
public class MyBooksFragment extends Fragment implements View.OnCreateContextMenuListener {

    private static String TAG="MyBooksFragment";

    public Book[] myBooks;
    private Context context;
    private Book selectedBook;
    private MyBooksGridAdapter myBookAdapter;
    private GridView gridView;
    private TextView noBooksNotice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        Tracker t = ((PustakalayApp) getActivity().getApplication()).getTracker(
                PustakalayApp.TrackerName.APP_TRACKER);
        t.setScreenName("On my book");
        t.send(new HitBuilders.ScreenViewBuilder().build());

        View rootView = inflater.inflate(R.layout.editors_pick, container, false);
        gridView =      (GridView) rootView.findViewById(R.id.editors_pick_gridview);
        noBooksNotice = (TextView) rootView.findViewById(R.id.tvNoBooksNotice);
//        gridView.setBackgroundColor(Color.parseColor("#e0f2f1"));
        this.context =  getActivity().getApplicationContext();

        myBooks = Utility.getAllDownloadedBook(getActivity().getApplicationContext());
        //may need to filter the still downloading books
        myBooks = cleanBooksArray(myBooks);

        if(myBooks ==null || myBooks.length == 0){

            gridView.setVisibility(View.GONE);
            noBooksNotice.setVisibility(View.VISIBLE);
            return rootView;

        }
        myBookAdapter = new MyBooksGridAdapter(getActivity().getApplicationContext(),myBooks);
        gridView.setAdapter(myBookAdapter);
        myBookAdapter.notifyDataSetChanged();
        registerForContextMenu(gridView);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//                Toast.makeText(getActivity().getApplicationContext(), " " + position, Toast.LENGTH_SHORT).show();
                Book book = myBooks[position];
//                presentBookDetailFragment(book);
                readBook(book);
            }
        });


      return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //fetch books and populate views
             reloadBooks();
        } else {
            //idk the invisible case
        }
    }

    private Book[] cleanBooksArray(Book[] booksArray){

        File pdfFile;
        ArrayList<Book> newBookList = new ArrayList<Book>();
        if(booksArray ==null){
            return null;
        }
        for(Book book : booksArray) {
                     pdfFile = new File(Utility.pdfDirectory + book.pdfFileURL);
                     if (!book.isDownloading) {
                         if (!pdfFile.exists()) {
                             new MyBooksDB(context).deleteBook(book);
                         } else {
                       newBookList.add(book);
                         }
                     } else {
                         Long fileSize=(long)0;
                         try {
                             fileSize = Long.parseLong(book.fileSize);
                         }catch ( Exception e){

                         }
                        if(pdfFile.exists() && Utility.fileIsApproxSame(fileSize, pdfFile.length())){
                          newBookList.add(book);
                          new MyBooksDB(context).deleteBook(book);
                    }
              }
             }
    return newBookList.toArray(new Book[newBookList.size()]);
    }

    private void freshReload(){

        Book[] fetchedBooks = Utility.getAllDownloadedBook(getActivity().getApplicationContext());
        fetchedBooks = cleanBooksArray(fetchedBooks);
        if(fetchedBooks.length == 0){
            gridView.setVisibility(View.GONE);
            noBooksNotice.setVisibility(View.VISIBLE);
            return;
        }
        this.myBooks = fetchedBooks;
        myBookAdapter.books = fetchedBooks;
        myBookAdapter.notifyDataSetChanged();

        noBooksNotice.setVisibility(View.GONE);
        gridView.setVisibility(View.VISIBLE);
    }
    private void reloadBooks(){
        if(gridView == null){
            //layout is not inflated so nothing to do
            return;
        }

        Book[] allBooks = Utility.getAllDownloadedBook(getActivity().getApplicationContext());
//        if( myBooks ==null ) {
//            if(gridView!=null)
//                gridView.setVisibility(View.GONE);
//            if(noBooksNotice!=null)
//                noBooksNotice.setVisibility(View.VISIBLE);
//            return;
//        }

        if( myBooks!=null && this.myBooks.length == allBooks.length){
            return;
        }

        Book[] fetchedBooks = allBooks;
        fetchedBooks = cleanBooksArray(fetchedBooks);
        if(fetchedBooks==null || fetchedBooks.length == 0){
            gridView.setVisibility(View.GONE);
            noBooksNotice.setVisibility(View.VISIBLE);
            return;
        }
        this.myBooks = fetchedBooks;

        if(myBookAdapter == null){
            myBookAdapter = new MyBooksGridAdapter(getActivity().getApplicationContext(),myBooks);
            gridView.setAdapter(myBookAdapter);
            myBookAdapter.notifyDataSetChanged();
            registerForContextMenu(gridView);
        }

        myBookAdapter.books = fetchedBooks;
        myBookAdapter.notifyDataSetChanged();

            noBooksNotice.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);

        Tracker t = ((PustakalayApp) getActivity().getApplication()).getTracker(
                PustakalayApp.TrackerName.APP_TRACKER);

        t.send(new HitBuilders.EventBuilder()
            .setCategory("Clicks")  // category i.e. Player Buttons
            .setAction("populatedBooks")    // action i.e.  Play
            .setLabel("populatedBooks clicked myBook "+ myBooks.length)    // label i.e.  any meta-data
            .build());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.editors_pick_gridview){
            GridView gv = (GridView) v;
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

            selectedBook = myBooks[info.position];
            Log.v("info", "********" + info.position);

            Tracker t = ((PustakalayApp) getActivity().getApplication()).getTracker(
                    PustakalayApp.TrackerName.APP_TRACKER);

            t.send(new HitBuilders.EventBuilder()
                    .setCategory("Clicks")  // category i.e. Player Buttons
                    .setAction("Longpress")    // action i.e.  Play
                    .setLabel("Longress clicked myBook")    // label i.e.  any meta-data
                    .build());

            menu.add(0, 1, 0, "Read Book");
            menu.add(0, 3, 0, "Delete");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
//        Toast.makeText(getActivity().getApplicationContext(), " " + item.getItemId(), Toast.LENGTH_SHORT).show();
        switch (item.getItemId()){
            case 1:
                readBook(selectedBook);
                break;
            case 3:
                deleteBook(selectedBook);
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void readBook(Book book){
        File pdfFile = new File(Utility.pdfDirectory+book.pdfFileURL);
        Tracker t = ((PustakalayApp) getActivity().getApplication()).getTracker(
                PustakalayApp.TrackerName.APP_TRACKER);
        t.send(new HitBuilders.EventBuilder()
                .setCategory("Clicks")  // category i.e. Player Buttons
                .setAction("read")    // action i.e.  Play
                .setLabel("read clicked myBook")    // label i.e.  any meta-data
                .build());

        if (pdfFile.exists()){
            Uri uriPath = Uri.fromFile(pdfFile);
            Log.v("path",""+uriPath);
            openPDFfile(uriPath);
        } else {
            Toast.makeText(context,"Oh! We can't find the file. Please reload the app and download the book again.",Toast.LENGTH_SHORT).show();
        }

    }

    public void openPDFfile(final Uri path){

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(path, "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    if( Utility.isIntentAvailable(getActivity().getApplicationContext(),intent)) {
                        startActivity(intent);
                    } else {
                        Log.v(TAG,"Intent Not avaliable");
                    Utility.showNoPDFReaderAlert(getActivity());
                    }

    }

    /*function to show the book details for selected ones*/
    void presentBookDetailFragment(Book book){
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.presentBookDetailFragment(mainActivity.getApplicationContext(),book);
    }

    private void deleteBook(Book book){
        Tracker t = ((PustakalayApp) getActivity().getApplication()).getTracker(
                PustakalayApp.TrackerName.APP_TRACKER);

        t.send(new HitBuilders.EventBuilder()
                .setCategory("Clicks")  // category i.e. Player Buttons
                .setAction("delete")    // action i.e.  Play
                .setLabel("delete clicked from mybook")    // label i.e.  any meta-data
                .build());

//        new MyBooksDB(context).deleteBook(book.pid);

        File imageFile = new File(Utility.pdfDirectory+book.coverImageURL);
        imageFile.delete();
        File pdfFile = new File(Utility.pdfDirectory+book.pdfFileURL);
        boolean deleted = pdfFile.delete();
        if (deleted){
            Toast.makeText(getActivity().getApplicationContext(), "Book Deleted", Toast.LENGTH_SHORT).show();
//            getFragmentManager().beginTransaction().remove(ListMyBooks.this).commit();
            ArrayList<Book> newArray = new ArrayList<Book>(Arrays.asList(this.myBooks));
            newArray.remove(book);
            this.myBooks = newArray.toArray(new Book[newArray.size()]);
            freshReload();
        }
    }
}
