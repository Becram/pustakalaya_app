package com.ole.epustakalaya;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ole.epustakalaya.helper.Preference;
import com.ole.epustakalaya.models.Book;

import static android.graphics.Color.parseColor;


public class HomepageFragment extends Fragment /*implements ActionBar.TabListener*/ {
    public static String TAG="HomepageFragment";

    public Book[] editorspicks;
    public Book[] latestBooks;
    public Book[] ownBooks;
    Preference prefs;
    private String serverAddress="";
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * three primary sections of the app. We use a {@link android.support.v4.app.FragmentPagerAdapter}
     * derivative, which will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    AppSectionsPagerAdapter mAppSectionsPagerAdapter;

    /**
     * The {@link android.support.v4.view.ViewPager} that will display the three primary sections of the app, one at a
     * time.
     */
    ViewPager mViewPager;
    PagerTabStrip mPagerTabStrip;
    public FragmentManager supportFragmentManager;
    public LinearLayout splash = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle argument = getArguments();
        if (argument!=null) {
            if (argument.getString("splash") != null) {
                splash = (LinearLayout) getActivity().findViewById(R.id.splash);
            }
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

//        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.main_page_layout, container, false);

        prefs = new Preference(getActivity().getApplicationContext());
        serverAddress = prefs.getServer();

//        customize pager tab strip on our main page
        mPagerTabStrip = (PagerTabStrip) rootView.findViewById(R.id.pager_tab_strip);
        mPagerTabStrip.setDrawFullUnderline(false);
        mPagerTabStrip.setTextSpacing(0);
        mPagerTabStrip.setTabIndicatorColor(parseColor("#388E3C"));

        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getChildFragmentManager());

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) rootView.findViewById(R.id.pager);

        mViewPager.setAdapter(mAppSectionsPagerAdapter);


        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                prefs.setMyLastTab(position);
                Log.v(TAG,"Page selected "+position);

            }
        });

        if (splash!=null){

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    splash.setVisibility(View.GONE);
                    ((ActionBarActivity)getActivity()).getSupportActionBar().show();
                    splash = null;
                }
            }, 2000);

        }

        //Make my book the default page if at least one book is downloaded
//        if(ownBooks!=null && ownBooks.length>0){
        mViewPager.setCurrentItem(prefs.getMyLastTab());

//        }
        return mViewPager;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }



    @Override
    public void onResume() {
        super.onResume();
//        set the title to epustakalaya and empty the subtitle
        ((ActionBarActivity)getActivity()).getSupportActionBar().setTitle(R.string.app_name);
        ((ActionBarActivity)getActivity()).getSupportActionBar().setSubtitle(null);
        if(serverAddress.length()>0){
            if(serverAddress.equalsIgnoreCase(prefs.getServer())){
                //same server no change
            } else {
                //reload data
                Log.v(TAG,"Server Address Change Detected");
                ((MainActivity)getActivity()).homeClicked(null);
                serverAddress = prefs.getServer();
            }
        }
    }


    /**
     * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public class AppSectionsPagerAdapter extends FragmentPagerAdapter {


        public AppSectionsPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int i) {
            String thisFragment;
            switch (i) {
                case 0:
                    // The first page Editors Picks
                    EditorsPicksFragment editorsPicksFragment = new EditorsPicksFragment();
                    editorsPicksFragment.editorspicks = editorspicks;
                    return editorsPicksFragment;
                case 1:
                    // third page Latest Books
                    MyAudioBooksFragment audioBooksFragment1 = new MyAudioBooksFragment();
//                    Log.e("latestBooks","**"+latestBooks);
//                    audioBooksFragment1.setLatestBooks(latestBooks);
                    return audioBooksFragment1;

                case 2:
                    // third page Latest Books
                    LatestBooksFragment latestBooksFragment1 = new LatestBooksFragment();
                    Log.e("latestBooks","**"+latestBooks);
                    latestBooksFragment1.setLatestBooks(latestBooks);
                    return latestBooksFragment1;
                case 3:
                    // second page My Books
                    MyBooksFragment myBooks = new MyBooksFragment();
                    myBooks.myBooks = ownBooks ;
                    return myBooks;


                default:
                    return null;

                // The other sections of the app are dummy placeholders.
                    /*Fragment fragment = new DummySectionFragment();
                    Bundle args = new Bundle();
                    args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, i + 1);
                    fragment.setArguments(args);
                    return fragment;*/
            }

        }

        @Override
        public int getCount() {
            return 4; // return the number of pages required
        }

        @Override
        public CharSequence getPageTitle(int position) {
//            return "this is the section" + (position + 1);
            CharSequence tabTitle = null;
            switch (position) {
                case 0:
                    tabTitle = "Editor's Picks";
                    break;
                case 1:
                    tabTitle = "Audio Books";

                    break;
                case 2:
                    tabTitle = "Latest Books";
                    break;
                case 3:
                    tabTitle = "My Books";
                    break;
                default:
                    tabTitle = "return 4 in getcount";
                    break;
            }
            return tabTitle;
        }
    }

}



