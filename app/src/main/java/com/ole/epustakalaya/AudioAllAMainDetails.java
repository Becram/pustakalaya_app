package com.ole.epustakalaya;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.ole.epustakalaya.retrofit_api.PustakalayaApiInterface;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by bikram on 3/10/16.
 */
public class AudioAllAMainDetails extends ActionBarActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PustakalayaApiInterface APIInterface;
    public static String get_bookid;
    public static String get_booktitle;
    public static String get_author;
    public static String get_image;
    public static String get_url;

    protected String BASE_URL="http://www.pustakalaya.org";


    private ImageView cover;
    private TextView text_author;
    private TextView text_language;
    private TextView text_title;
    private TextView text_views;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTheme(R.style.myTheme);
        setContentView(R.layout.audio_details);


//      get book id from

        get_bookid=getIntent().getStringExtra("bookID");
        get_booktitle=getIntent().getStringExtra("bookTitle");
        get_author=getIntent().getStringExtra("bookAuthor");
        get_image=getIntent().getStringExtra("bookImage");
//        get_url=getIntent().getStringExtra("bookURL");

//        Bundle bund=new Bundle();
//        bund.putString("bookid",bookid);
//
//        Log.d("book id MM",bookid);



//        Retrofit application for book details





        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
//        tabLayout.getTabAt(0).setIcon(R.drawable.about_us_background);
//        tabLayout.getTabAt(1).setIcon(R.drawable.custom_icon);


    }

    private void setupViewPager(ViewPager viewPager) {
        TabViewPagerAdapter adapter = new TabViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AudioDetailsFragment(), "Details");
        adapter.addFragment(new AudioTracksPlayFragment(), "Play");

        viewPager.setAdapter(adapter);
    }


    class TabViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public TabViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


}
