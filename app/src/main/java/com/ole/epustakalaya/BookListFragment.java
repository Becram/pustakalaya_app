package com.ole.epustakalaya;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ole.epustakalaya.helper.Preference;
import com.ole.epustakalaya.interfacesAndAdaptors.BookListAdapter;
import com.ole.epustakalaya.models.Book;

/**
 * Created by mjt-ole on 9/18/14.
 */
public class BookListFragment extends Fragment {

    private static String TAG="ListBookFragment";

    private ListView listView;
    private BookListAdapter myBookAdapter;
    public Book[] books;
    private String listbook_last_subtitle;
    public boolean isFromSearch;
    Menu menu;

    int lastSelectedSubMenuId=R.id.author_ascending;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	setHasOptionsMenu(true);
        /*roshan on*/
//       following block of code keeps track of the last subtitle for this fragment and on resume sets the last subtitle
        String thisFragSubtitle = (String) ((ActionBarActivity)getActivity()).getSupportActionBar().getSubtitle();
        listbook_last_subtitle = thisFragSubtitle;
//        Toast.makeText(getActivity().getApplicationContext(), thisFragSubtitle, LENGTH_LONG).show();
        /*roshan off*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_book,container,false);

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView = (ListView) getView().findViewById(R.id.bookList);
        myBookAdapter = new BookListAdapter(getActivity(),this.books);
        myBookAdapter.notifyDataSetChanged();
        listView.setAdapter(myBookAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.presentBookDetailFragment(mainActivity.getApplicationContext(),books[position]);
            }
        });

    }

    @Override
    public void onPause(){
        super.onPause();
        listbook_last_subtitle = (String) ((ActionBarActivity) getActivity()).getSupportActionBar().getSubtitle();
    }

    /*roshan on*/
    @Override
    public void onResume(){
        super.onResume();
//        set the title to epustakalaya and empty the subtitle
//        getActivity().getActionBar().setSubtitle(listbook_last_subtitle);

            ((ActionBarActivity) getActivity()).getSupportActionBar().setSubtitle(listbook_last_subtitle);

    }
    /*roshan off*/
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(isFromSearch){
            return;
        }
        inflater.inflate(R.menu.sort_book_list, menu);
        this.menu = menu;
        updateMenuAccordingToSelection();
    }

    void updateMenuAccordingToSelection(){

        // All this stuffs is done to display user the selection what he have done last
        int selectedMenuId, selectedSubMenuId;

        Preference pref = new Preference(getActivity().getApplicationContext());
        String sortField = pref.getSortField();
        String sortOrder = pref.getSortOrder();

        selectedMenuId = R.id.author_sort;
        selectedSubMenuId = R.id.author_ascending;

        if(sortField.equalsIgnoreCase("author")){
            selectedMenuId = R.id.author_sort;
            selectedSubMenuId = sortOrder.equalsIgnoreCase("asc")?R.id.author_ascending:R.id.author_descending;
        } else if(sortField.equalsIgnoreCase("title")){
            selectedMenuId = R.id.title_sort;
            selectedSubMenuId = sortOrder.equalsIgnoreCase("asc")?R.id.title_ascending:R.id.title_descending;
        }else if(sortField.equalsIgnoreCase("date")) {
            selectedMenuId = R.id.date_sort;
            selectedSubMenuId = sortOrder.equalsIgnoreCase("asc")?R.id.date_ascending:R.id.date_descending;
        }

        try {
            MenuItem lastSubMenu = menu.findItem(lastSelectedSubMenuId);
            lastSubMenu.setChecked(false);

            MenuItem selectedMenu = menu.findItem(selectedMenuId);
            MenuItem selectedSubMenu = menu.findItem(selectedSubMenuId);

            selectedMenu.setChecked(true);
            selectedSubMenu.setChecked(true);

            lastSelectedSubMenuId = selectedSubMenuId;
        } catch (Exception e){
            e.printStackTrace();
            Log.v(TAG,"No more overuse");
        }
    }

    public void updateTheList(Book[] books, String title, String subTitle){
        //remove the sort menu if present
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActivity().invalidateOptionsMenu();
        }
        this.books = books;
        myBookAdapter.books = books;
        myBookAdapter.notifyDataSetChanged();
        listView.setSelection(0);
        Log.v("ListBook", "data updated");

        ((ActionBarActivity)getActivity()).getSupportActionBar().setTitle(title);
        ((ActionBarActivity)getActivity()).getSupportActionBar().setSubtitle(subTitle);

        if(!isFromSearch) {
            updateMenuAccordingToSelection();
        }
    }
}

