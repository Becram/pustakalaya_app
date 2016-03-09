package com.ole.epustakalaya;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.ole.epustakalaya.models.Book;
import com.ole.epustakalaya.models.PustakalayApp;

/**
 * Created by roshan on 17/10/14.
 */
public class EditorsPicksFragment extends Fragment implements BookFetchListener
{
    public Book[] editorspicks;
    GetBooksAsync getBooksAsync;
    LinearLayout llLoadingContainer;
    TextView tvNoConnectionNotice;
    GridView gvEditorsPicks;
    BooksGridAdapter editorsBookAdapter;
    private static String noConnectionMsg = "No internet connection. Please check you internet connective or adjust the server address on setting page.";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        Tracker t = ((PustakalayApp) getActivity().getApplication()).getTracker(
                PustakalayApp.TrackerName.APP_TRACKER);

        // Set screen name.
        t.setScreenName("editor picks");
        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());

        /*if editorspicks is not empty populate the editorspicks books page*/
        getBooksAsync = new GetBooksAsync(getActivity().getApplicationContext(), this, BookTypeEditorsPicks);

        View rootView = inflater.inflate(R.layout.editors_pick, container, false);
        gvEditorsPicks = (GridView) rootView.findViewById(R.id.editors_pick_gridview);
        llLoadingContainer = (LinearLayout) rootView.findViewById(R.id.pbLoadingProgressBar);
        tvNoConnectionNotice = (TextView) rootView.findViewById(R.id.tvNoBooksNotice);
        tvNoConnectionNotice.setText(noConnectionMsg);

        gvEditorsPicks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
    //                Toast.makeText(getActivity().getApplicationContext(), " " + position, Toast.LENGTH_SHORT).show();
                        if(Utility.isConnected(getActivity().getApplicationContext())){
                            Book book = editorspicks[position];
                            presentBookDetailFragment(book);
                        } else{
                            Toast.makeText(getActivity().getApplicationContext(), "No Connection...", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            if(editorspicks != null) {
                editorsBookAdapter = new BooksGridAdapter(getActivity().getApplicationContext(),editorspicks);
                gvEditorsPicks.setAdapter(editorsBookAdapter);
                editorsBookAdapter.notifyDataSetChanged();
                llLoadingContainer.setVisibility(View.GONE);
                tvNoConnectionNotice.setVisibility(View.GONE);
                gvEditorsPicks.setVisibility(View.VISIBLE);
            } else if(Utility.isConnected(getActivity().getApplicationContext())){
                getBooksAsync.execute();
                llLoadingContainer.setVisibility(View.VISIBLE);
                gvEditorsPicks.setVisibility(View.GONE);
                tvNoConnectionNotice.setVisibility(View.GONE);
                //gridAdaptor setting is don on callback
            } else {
                //Display No connection msg
                gvEditorsPicks.setVisibility(View.GONE);
                llLoadingContainer.setVisibility(View.GONE);
                tvNoConnectionNotice.setVisibility(View.VISIBLE);
            }

        return rootView;
    }

    /*function to show the book details for selected ones*/
    void presentBookDetailFragment(Book book){
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.presentBookDetailFragment(mainActivity.getApplicationContext(), book);
    }


    @Override
    public void onFetchedBookReceived(Book[] books, int BooksType) {
        if(books == null){
//            Toast.makeText(getActivity().getApplicationContext(),"Seems like you  are not connected to Internet.",Toast.LENGTH_SHORT).show();
            if(editorspicks==null) {
                gvEditorsPicks.setVisibility(View.GONE);
                llLoadingContainer.setVisibility(View.GONE);
                tvNoConnectionNotice.setVisibility(View.VISIBLE);
            }
            return;
        }
        // do the population task here
        this.editorspicks = books;
        editorsBookAdapter = new BooksGridAdapter(getActivity().getApplicationContext(),editorspicks);
        gvEditorsPicks.setAdapter(editorsBookAdapter);
        editorsBookAdapter.notifyDataSetChanged();

        llLoadingContainer.setVisibility(View.GONE);
        tvNoConnectionNotice.setVisibility(View.GONE);
        gvEditorsPicks.setVisibility(View.VISIBLE);
    }
}

