package com.ole.epustakalaya;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.ole.epustakalaya.helper.MyAudioBooksDB;
import com.ole.epustakalaya.helper.Utility;
import com.ole.epustakalaya.interfacesAndAdaptors.MyAudioDownloadAdapter;
import com.ole.epustakalaya.models.AudioBookDB;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by bikram on 3/29/16.
 */
public class MyAudioDownnloads extends Fragment {

    private GridView gridView;
    private TextView noBooksNotice;
    private Context context;
    private ArrayList<AudioBookDB> bList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         View v=inflater.inflate(R.layout.editors_pick,container,false);
        gridView =      (GridView) v.findViewById(R.id.editors_pick_gridview);
        MyAudioBooksDB databaseHelper = new MyAudioBooksDB(getContext());
        bList = new ArrayList<AudioBookDB>();
//        for (int i=0;i<Utility.getAudioFilesFromDirs(Environment.getExternalStorageDirectory()
//                + "/Epustakalaya/audio").size();i++){
//dio").get(i));
//        }
        ArrayList myArray=Utility.getAudioFilesFromDirs(Environment.getExternalStorageDirectory()
                + "/Epustakalaya/audio");
//        Log.d("myloop", databaseHelper.getAudioFilesFromDirsComparewithSqlite(Utility.getAudioFilesFromDirs(Environment.getExternalStorageDirectory()
//                + "/Epustakalaya/audio")).get(0).getTitle());

//            Log.d("looper",Utility.getAudioFilesFromDirs(Environment.getExternalStorageDirectory()
//                    + "/Epustakalaya/au
        bList.clear();

        bList = databaseHelper.getAudioFilesFromDirsComparewithSqlite(Utility.getAudioFilesFromDirs(Environment.getExternalStorageDirectory()
                + "/Epustakalaya/audio"));
        MyAudioDownloadAdapter adapter = new MyAudioDownloadAdapter(getContext(),bList);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Log.d("grid",String.valueOf(bList.get(position).getPID()));
                Intent in=new Intent(v.getContext(),AudioDownloadedPlay.class);
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                in.putExtra("audiobookID", String.valueOf(bList.get(position).getPID()));
                startActivity(in);

                // DO something

            }
        });
         return v;
    }

//    public ArrayList<String> getAudioFilesFromDirs(String Dir){
//        ArrayList<String> PIDS = new ArrayList<String>();
//
//        Log.d("Files", "Path: " + Dir);
//        File f = new File(Dir);
//        File file[] = f.listFiles();
//        if(file != null){
//            Log.d("Files", "Size: "+ file.length);
//            for (int i=0; i < file.length; i++)
//            {    String s=file[i].getName();
//
//                Log.d("Files", "FileName:" + s);
//                String upToNCharacters = s.substring(0, Math.min(s.length(), 3));
//                PIDS.add(upToNCharacters);
//            }
//            Log.d("PIDS from Activity",String.valueOf(PIDS));
//
//        }
//        return PIDS;
//    }
}