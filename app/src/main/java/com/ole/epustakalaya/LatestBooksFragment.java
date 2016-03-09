package com.ole.epustakalaya;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ole.epustakalaya.helper.Utility;
import com.ole.epustakalaya.interfacesAndAdaptors.BookFetchListener;
import com.ole.epustakalaya.interfacesAndAdaptors.BooksGridAdapter;
import com.ole.epustakalaya.interfacesAndAdaptors.GetBooksAsync;
import com.ole.epustakalaya.libs.EndlessScrollListener;
import com.ole.epustakalaya.models.Book;
import com.ole.epustakalaya.models.PustakalayApp;

/**
 * Created by roshan on 17/10/14.
 */
public class LatestBooksFragment extends Fragment implements BookFetchListener {

    public static final String TAG = "Latest Books Frag";
    public Book[] latestBooks = null;
    private Context context;

    private static int latestCount = 1;
    private GridView gvLatestBooksGrid;
    private LinearLayout llProgressContainer;
    private TextView tvNoConnection;
    private BooksGridAdapter latestBooksGridAdapter;
    private static String noConnectionMsg = "No internet connection. Please check you internet connective or adjust the server address on setting page.";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        Tracker t = ((PustakalayApp) getActivity().getApplication()).getTracker(
                PustakalayApp.TrackerName.APP_TRACKER);

        // Set screen name.
        t.setScreenName("latest book");

        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());


        this.context = getActivity().getApplicationContext();
        /*if latest books is not empty populate the latest books page*/
        View rootView;
        rootView = inflater.inflate(R.layout.latest_books, container, false);
        gvLatestBooksGrid = (GridView) rootView.findViewById(R.id.latest_book_gridview);
        llProgressContainer = (LinearLayout)rootView.findViewById(R.id.pbLoadingProgressBar);
        tvNoConnection = (TextView) rootView.findViewById(R.id.tvNoBooksNotice);
        tvNoConnection.setText(noConnectionMsg);


        gvLatestBooksGrid.setOnScrollListener(new EndlessScrollListener() {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    loadMoreLatestBook();
                }
            });
        gvLatestBooksGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Book book = latestBooks[i];
                presentBookDetailFragment(book);

            }
        });

        if(latestBooks!=null){
            latestBooksGridAdapter = new BooksGridAdapter(context,latestBooks);
            gvLatestBooksGrid.setAdapter(latestBooksGridAdapter);
            latestBooksGridAdapter.notifyDataSetChanged();

            llProgressContainer.setVisibility(View.GONE);
            tvNoConnection.setVisibility(View.GONE);
            gvLatestBooksGrid.setVisibility(View.VISIBLE);
        } else if( Utility.isConnected(context)) {
//            new GetLatestBooksAsync(context,this,BookTypeLatestBooksFirstLoad).execute(1);
            new GetBooksAsync(context,this,BookTypeLatestBooksFirstLoad,1).execute();
            llProgressContainer.setVisibility(View.VISIBLE);
            tvNoConnection.setVisibility(View.GONE);
            gvLatestBooksGrid.setVisibility(View.GONE);
        } else {
            //show the no conneciton msg
            llProgressContainer.setVisibility(View.GONE);
            tvNoConnection.setVisibility(View.VISIBLE);
            gvLatestBooksGrid.setVisibility(View.GONE);
        }

        return rootView;
    }

    public void setLatestBooks(Book[] latestBooks){
            this.latestBooks = latestBooks;
    }

    /*function to show the book details for selected ones*/
    void presentBookDetailFragment(Book book){
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.presentBookDetailFragment(mainActivity.getApplicationContext(), book);
    }

    void loadMoreLatestBook(){
        latestCount ++;
//        new GetLatestBooksAsync(context,this,BookTypeLatestBooksLoadMore).execute(latestCount);
        new GetBooksAsync(context,this,BookTypeLatestBooksLoadMore,latestCount).execute();

    }

    void loadMoreGrid(Book[] incomingBooks){
        if(incomingBooks == null){
            return;
        }
        if (latestBooks==null){
            latestCount=1;
        } else {
            int currentIndex = latestBooks.length;

            Book[] newBooks = incomingBooks;


            int len = newBooks.length + currentIndex;
            Book[] mergedArray = new Book[len];
            int i = 0;
            for (Book book : latestBooks) {
                mergedArray[i] = latestBooks[i++];
            }
            int j = 0;
            for (Book book : newBooks) {
                mergedArray[i++] = newBooks[j++];
            }
//            pbLoadMoreProgressBar.setVisibility(View.GONE);
            llProgressContainer.setVisibility(View.GONE);
            tvNoConnection.setVisibility(View.GONE);
            gvLatestBooksGrid.setVisibility(View.VISIBLE);

            this.latestBooks = mergedArray;
            Log.e(TAG,"**"+latestBooks);
            latestBooksGridAdapter.books = latestBooks;
            latestBooksGridAdapter.notifyDataSetChanged();

        }

    }

    void populateLatestBooksGridView(final Book[] latestBooks){

        if(latestBooks == null){
            return;
        }
        this.latestBooks = latestBooks;

        llProgressContainer.setVisibility(View.GONE);
        tvNoConnection.setVisibility(View.GONE);
        gvLatestBooksGrid.setVisibility(View.VISIBLE);

        latestBooksGridAdapter = new BooksGridAdapter(context,latestBooks);
        gvLatestBooksGrid.setAdapter(latestBooksGridAdapter);

        latestBooksGridAdapter.books = latestBooks;
        latestBooksGridAdapter.notifyDataSetChanged();


    }

    @Override
    public void onFetchedBookReceived(Book[] books, int BooksType) {
        if(books == null){
//            Toast.makeText(context,"Seems like you  are not connected to Internet.",Toast.LENGTH_SHORT).show();
            if(latestBooks==null) {
                llProgressContainer.setVisibility(View.GONE);
                tvNoConnection.setVisibility(View.VISIBLE);
                gvLatestBooksGrid.setVisibility(View.GONE);
            }
            return;
        }
        // do the population stuffs

        if(BooksType == BookTypeLatestBooksFirstLoad){
            populateLatestBooksGridView(books);
        } else if(BooksType == BookTypeLatestBooksLoadMore ){
            loadMoreGrid(books);
        }
        Log.v(TAG, "I am not expected to reach here");
    }
}

