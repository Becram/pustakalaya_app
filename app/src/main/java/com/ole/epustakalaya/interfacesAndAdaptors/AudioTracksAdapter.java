package com.ole.epustakalaya.interfacesAndAdaptors;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ole.epustakalaya.AudioTracksPlayFragment;
import com.ole.epustakalaya.MainActivity;
import com.ole.epustakalaya.R;
import com.ole.epustakalaya.helper.MyAudioBooksDB;
import com.ole.epustakalaya.helper.MyBooksDB;
import com.ole.epustakalaya.interfacesAndAdaptors.MyAudioAllViewHolder;
import com.ole.epustakalaya.interfacesAndAdaptors.MyAudioTracksViewHolder;
import com.ole.epustakalaya.interfacesAndAdaptors.OnLoadMoreListener;

import com.ole.epustakalaya.models.AudioBook;
import com.ole.epustakalaya.models.Book;
import com.ole.epustakalaya.models.Track;

import java.io.File;
import java.util.List;

/**
 * Created by bikram on 3/10/16.
 */
public class AudioTracksAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    protected String BASE_URL="http://www.pustakalaya.org";
    public static String AudioDirectory = Environment.getExternalStorageDirectory()+"/Epustakalaya/AudioBooks/";

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;


    private Context myContext;
    private AudioTracksPlayFragment audioTracksActivity;
    private int lastVisibleItem, totalItemCount;
    private int visibleThreshold = 2;
    private DownloadManager downloadmanager;
    private AudioBook aBook;

    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    private Context mContext;
    //    private ArrayList<String> mDataset;
    private List<Track> mTrackList;
    private Typeface face;
    private long myDownloadReference;
    private Book book;


    public AudioTracksAdapter(List<Track> BookList, RecyclerView recyclerV, Context m) {
        this.mTrackList = BookList;
        this.myContext=m;
        if(recyclerV.getLayoutManager() instanceof LinearLayoutManager){
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerV.getLayoutManager();
            recyclerV.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerV, int dx, int dy) {
                    super.onScrolled(recyclerV, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        // End has been reached
                        // Do something
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });





        }

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vholder;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.audio_list_row, parent, false);

            vholder = new MyAudioTracksViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.progressbar_item, parent, false);

            vholder = new ProgressAudio(v);
        }
        return vholder;
    }




    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder HOLDER, final int position) {
//        face= Typeface.createFromAsset(myContext.getAssets(), "fonts/Kelson Sans Light.otf");


        final Track mTrack=mTrackList.get(position);
        if (HOLDER instanceof MyAudioTracksViewHolder) {
            ((MyAudioTracksViewHolder) HOLDER).getTextViewAudioTitle().setText(mTrack.track_title);
            Log.d("title_test", mTrack.getTitle());

            ((MyAudioTracksViewHolder) HOLDER).getFileSize().setText(humanReadableByteCount(mTrack.track_size, true));
            ((MyAudioTracksViewHolder) HOLDER).getDuration().setText(getConvertedTime(mTrack.track_duration));
        //    ((MyAudioTracksViewHolder) HOLDER).getTextViewAudioTitle().setTypeface(face);f

            Log.d("regex",StringRegx(AudioTracksPlayFragment.book_title));
            ((MyAudioTracksViewHolder) HOLDER).getDownloader().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (Build.VERSION.SDK_INT >= 23) {
//
//                        Log.d("permission", "seems like permission needs to be granted");
////                        ActivityCompat.requestPermissions(audioTracksActivity.getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//
//                    } else {

                        Toast.makeText(v.getContext(), "downloader at " + StringRegx(AudioTracksPlayFragment.book_title) + mTrack.getTrackURL(), Toast.LENGTH_LONG).show();
                        DownloadManager downloadmanager = (DownloadManager) myContext.getSystemService(Context.DOWNLOAD_SERVICE);
                        Uri uri = Uri
                                .parse(BASE_URL + mTrack.getTrackURL());

                        File direct = new File(Environment.getExternalStorageDirectory()
                                + "/Epustakalaya/audio");

                        if (!direct.exists()) {
                            direct.mkdirs();
                        }

                        DownloadManager.Request request = new DownloadManager.Request(uri);
                        request.setAllowedNetworkTypes(
                                DownloadManager.Request.NETWORK_WIFI
                                        | DownloadManager.Request.NETWORK_MOBILE)
                                .setAllowedOverRoaming(false).setTitle(mTrack.getTitle())
                                .setDescription("Downloading ...")
                                .setDestinationInExternalPublicDir("/Epustakalaya/audio/", AudioTracksPlayFragment.book_id + "_" + StringRegx(AudioTracksPlayFragment.book_title) + mTrack.getTrackURL());
//                            .setDestinationInExternalPublicDir("/Epustakalaya/audio/",StringRegx(AudioTracksPlayFragment.book_title)+ mTrack.getTrackURL());


                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            // only for honeycomb
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        }
//                    File mydownload = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ "/AudioBooks");
//                    request.setDestinationInExternalPublicDir(mydownload.getAbsolutePath(),mTrack.getTitle());

                        dbAudioBookWrite();
                        downloadmanager.enqueue(request);


                    }
//                }
            });


//            Log.d("book chap", mTrack.track_title);

//            dbWrite(true);
        } else {
            Log.d("loading", "binder");
            ((ProgressAudio) HOLDER).progressAudio.setIndeterminate(true);
//            ((ProgressAudio) HOLDER).progressAudio.setIndeterminate(true);
        }


    }

    public  String StringRegx(String s) {

        String regexedString=s.replaceAll("[-+.^:,'()]", " ").trim();

        return regexedString.replaceAll("\\s","");


    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
    public void dbAudioBookWrite(){
        MyAudioBooksDB db = new MyAudioBooksDB(myContext);
        Log.d("DB","witing db");
//        book.isDownloading = isDownloading;
        db.addAudioBook("123","testTile","ramesh","cover","url");
    }

    public static String getConvertedTime(double value) {
        String output;

        long min = (long) (value / 60);
        long second = (long) (value % 60);
        if (min < 60){
            output = min + ":" + (second > 10 ? "" : "0") + second;
        }else{
            long hr=min/60;
            output=hr+":"+(min-hr*60) + ":" + (second > 10 ? "" : "0") + second;

        }
        return output;


    }




    @Override
    public int getItemCount() {
        return mTrackList.size();

    }

    @Override
    public int getItemViewType(int position) {
        return mTrackList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public void setLoaded() {
        loading = false;
    }



    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }


    public void add(int position, String item) {

    }

    public void remove(String item) {
//        int position = mDataset.indexOf(item);
//        mDataset.remove(position);
//        notifyItemRemoved(position);
    }



    public List<Track> getRepoList() {
        return mTrackList;
    }



}