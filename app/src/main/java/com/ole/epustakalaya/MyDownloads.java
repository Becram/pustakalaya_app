package com.ole.epustakalaya;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ole.epustakalaya.helper.MyAudioBooksDB;

/**
 * Created by bikram on 3/29/16.
 */
public class MyDownloads extends Fragment {

    private MyBooksFragment mybooksdownload ;
    private MyAudioDownnloads myaudiodownloads;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        view = inflater.inflate(R.layout.downloads, container, false);
        mybooksdownload = new MyBooksFragment();
        myaudiodownloads = new MyAudioDownnloads();
        FragmentManager manager=getActivity().getSupportFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();//create an instance of Fragment-transaction

        transaction.add(R.id.readingbooks_fragment, mybooksdownload);
        transaction.add(R.id.audiobooks_fragment, myaudiodownloads);

        transaction.commit();

        return view;
    }



}
