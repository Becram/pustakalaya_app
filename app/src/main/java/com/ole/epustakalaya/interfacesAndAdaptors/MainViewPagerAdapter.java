package com.ole.epustakalaya.interfacesAndAdaptors;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.ole.epustakalaya.EditorsPicksFragment;
import com.ole.epustakalaya.LatestBooksFragment;
import com.ole.epustakalaya.MyAudioBooksFragment;
import com.ole.epustakalaya.MyBooksFragment;
import com.ole.epustakalaya.models.Book;

/**
 * Created by bikram on 3/17/16.
 */
public class MainViewPagerAdapter extends FragmentStatePagerAdapter {

    public Book[] editorspicks;
    public Book[] latestBooks;
    public Book[] ownBooks;

    public MainViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        String thisFragment;
        switch (position) {
            case 0:
                // The first page Editors Picks
//                EditorsPicksFragment editorsPicksFragment = new EditorsPicksFragment();
//                editorsPicksFragment.editorspicks = editorspicks;
                return new EditorsPicksFragment();
            case 1:
                // third page Latest Books
//                MyAudioBooksFragment audioBooksFragment1 = new MyAudioBooksFragment();
//                    Log.e("latestBooks","**"+latestBooks);
//                    audioBooksFragment1.setLatestBooks(latestBooks);
                return new MyAudioBooksFragment();

            case 2:
                // third page Latest Books
//                LatestBooksFragment latestBooksFragment1 = new LatestBooksFragment();
//                Log.e("latestBooks", "**" + latestBooks);
//                latestBooksFragment1.setLatestBooks(latestBooks);
                return new LatestBooksFragment();
            case 3:
                // second page My Books
//                MyBooksFragment myBooks = new MyBooksFragment();
//                myBooks.myBooks = ownBooks ;
                return new MyBooksFragment();


            default:
                return null;

            // The other sections of the app are dummy placeholders.
                    /*Fragment fragment = new DummySectionFragment();
                    Bundle args = new Bundle();
                    args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, i + 1);
                    fragment.setArguments(args);
                    return fragment;*/
        }    // Which Fragment should be dislpayed by the viewpager for the given position
        // In my case we are showing up only one fragment in all the three tabs so we are
        // not worrying about the position and just returning the TabFragment
    }

    @Override
    public int getCount() {
        return 3;           // As there are only 3 Tabs
    }

}
