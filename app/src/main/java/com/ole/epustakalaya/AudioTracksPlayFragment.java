package com.ole.epustakalaya;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ole.epustakalaya.interfacesAndAdaptors.AudioTracksAdapter;
import com.ole.epustakalaya.interfacesAndAdaptors.ItemClickSupport;
import com.ole.epustakalaya.models.ModelAudioBookDetails;
import com.ole.epustakalaya.models.Track;
import com.ole.epustakalaya.retrofit_api.PustakalayaApiInterface;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by bikram on 3/10/16.
 */
public class AudioTracksPlayFragment extends Fragment implements Callback<ModelAudioBookDetails>, SeekBar.OnSeekBarChangeListener{

    private List<Track> mListItems;
    private AudioTracksAdapter mAdapter;
    private PustakalayaApiInterface APIInterface;
    public static String AudioDirectory = Environment.getExternalStorageDirectory()+"/Epustakalaya/AudioBooks/";
    public ListView listView;
    protected String BASE_URL="http://www.pustakalaya.org";
    private boolean pause_status=false;

    private TextView mSelectedTrackTitle;


    private MediaPlayer mMediaPlayer;
    private ImageView mPlayerControl;
    private RecyclerView audioRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ImageView mSelectedTrackImage;
    private TextView mSelectedTrackChapter;
    private TextView mSelectedTrackStatus;
    private SeekBar mSeekBar;
     DownloadManager downloadManger;
    Handler seekHandler = new Handler();
    private View toolbar_view;
    private static int a=0;
    private BroadcastReceiver downloadCompleteBroadcastReceiver;
    private Context context;
    public static String book_title;
//    private Context context;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    public static boolean createAudioDir(){
        boolean ret = true;

        File file = new File(AudioDirectory);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e("CreatDir", "Problem creating Audio folder");
                ret = false;
            }
        }
        return ret;
    }

//    public void AudioDownloader(String book_title,String downloadFileUrl){
//
//        Uri localFileUrl = Uri.fromFile(new File(AudioDirectory + book_title));
//        DownloadManager.Request request = new DownloadManager.Request(downloadFileUrl);
//        request.setDescription("Downloading ...")
//                .setTitle(book.title);
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            // only for honeycomb
//            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        }
//
//        //The server need to send the content-length on http response
//        //http://stackoverflow.com/questions/26401069/android-download-manager-always-displays-indefinite-progress-bar-and-not-progres
//        request.setDestinationUri(localFileUrl);
//        request.setVisibleInDownloadsUi(true);
//
//    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        seekHandler.removeCallbacks(run);
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.tab_play_details, container, false);

        mSelectedTrackTitle = (TextView) v.findViewById(R.id.selected_track_title);
        mSelectedTrackChapter = (TextView) v.findViewById(R.id.selected_chapter);
        mSelectedTrackStatus = (TextView) v.findViewById(R.id.selected_playpause_status);
//        mSelectedTrackTitle = (TextView) v.findViewById(R.id.selected_track_title);
        mSelectedTrackImage = (ImageView) v.findViewById(R.id.selected_track_image);
        mPlayerControl = (ImageView) v.findViewById(R.id.player_control);
        mSeekBar= (SeekBar) v.findViewById(R.id.seekBar);

        audioRecyclerView = (RecyclerView) v.findViewById(R.id.track_recycler_view);
        audioRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        audioRecyclerView.setLayoutManager(mLayoutManager);
//        mSeekBar.setMax(mMediaPlayer.getDuration());
        mSeekBar.setOnSeekBarChangeListener(this);
        toolbar_view=v.findViewById(R.id.view);





        return  v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        seekHandler.removeCallbacks(run);
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        mListItems = new ArrayList<Track>();
//
//        mAdapter = new AudioTracksAdapter(getContext(), mListItems);
//        listView.setAdapter(mAdapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.pustakalaya.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        APIInterface = retrofit.create(PustakalayaApiInterface.class);
        Call<ModelAudioBookDetails> call = APIInterface.getAudioBooksDetails(AudioAllAMainDetails.get_bookid);
        book_title=AudioAllAMainDetails.get_booktitle;


        call.enqueue(this);


        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                togglePlayPause();
            }
        });
        mPlayerControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlayPause();

            }
        });


        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlayerControl.setImageResource(R.drawable.button_reload);
            }
        });
        int startPosition = 0;
        int total = mMediaPlayer.getDuration();
        Log.d("track", String.valueOf(total));


//        downloadManger = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);



    }

    Runnable run = new Runnable() {
        @Override
        public void run() {
            SeekUpdation();
        }


    };

    public void SeekUpdation() {
//        mMediaPlayer.getCurrentPosition()=a;

        seekHandler.removeCallbacks(run);
        try {
            if (mMediaPlayer != null)
                mSeekBar.setMax(mMediaPlayer.getDuration());

            mSeekBar.setProgress(mMediaPlayer.getCurrentPosition());




            Log.d("max", String.valueOf(mMediaPlayer.getDuration()));


            seekHandler.postDelayed(run, 1000);

        }catch (InstantiationException e){

            Log.d("max", e.toString());
        }

    }

    private void togglePlayPause() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            boolean pause=true;
            mPlayerControl.setImageResource(R.drawable.play_new);
            toolbar_view.setVisibility(View.VISIBLE);


//            mSeekBar.setProgress(mMediaPlayer.getCurrentPosition());
            mSelectedTrackStatus.setText(R.string.paused);
        }

        else if(pause_status){

            mMediaPlayer.start();
            mPlayerControl.setImageResource(R.drawable.play_new);
            toolbar_view.setVisibility(View.VISIBLE);


//            mSeekBar.setProgress(mMediaPlayer.getCurrentPosition());
            mSelectedTrackStatus.setText(R.string.paused);


            }
         else {
            mMediaPlayer.start();
            SeekUpdation();
//            mSeekBar.setMax(mMediaPlayer.getDuration());
            mPlayerControl.setImageResource(R.drawable.pause_new);
            mSelectedTrackStatus.setText(R.string.playing);
            toolbar_view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResponse(final Response<ModelAudioBookDetails> response, Retrofit retrofit) {
        mListItems=createList(response);
        Log.d("list",mListItems.get(0).getTitle());

        mAdapter = new AudioTracksAdapter(createList(response),audioRecyclerView ,getActivity());

        audioRecyclerView.clearFocus();

        audioRecyclerView.setAdapter(mAdapter);

        ItemClickSupport.addTo(audioRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                Track track = mListItems.get(position);

                mSelectedTrackTitle.setText(response.body().getContent().getTitle());



                mSelectedTrackChapter.setText(track.getTitle());

                Picasso.with(getContext()).load(BASE_URL + response.body().getContent().getImage()).into(mSelectedTrackImage);

                Log.d("item", String.valueOf(position));
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                    mMediaPlayer.reset();
                }else {
                    Log.d("paused","from inside");
                    mMediaPlayer.stop();
                    mMediaPlayer.reset();
                }

                try {

                    String audio_url = BASE_URL + createList(response).get(position).getTrackURL();
                    String fixedUrl = audio_url.replaceAll("\\s", "%20");
//

                    mMediaPlayer.setDataSource(fixedUrl);
                    Log.d("track url", fixedUrl);
                    mMediaPlayer.prepareAsync();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });



        mAdapter.notifyDataSetChanged();




    }

    @Override
    public void onFailure(Throwable t) {

        Log.d("API failed","failed to fetch data");


    }


    private List<Track> createList(Response<ModelAudioBookDetails> t) {

        List<Track> result = new ArrayList<>();
        for (int i = 0; i <  t.body().getContent().getChapters().size(); i++) {
            Track ci = new Track();
            ci.track_title = t.body().getContent().getChapters().get(i).getChapter();
            ci.track_url=t.body().getContent().getChapters().get(i).getFile();
            ci.track_size=t.body().getContent().getChapters().get(i).getSize();
            ci.track_duration=t.body().getContent().getChapters().get(i).getDuration();
            Log.d("get count", String.valueOf(t.body().getContent().getTitle())) ;


            result.add(ci);

        }

        return result;


    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser) {
            mMediaPlayer.seekTo(progress);
//            mSeekBar.setMax(mMediaPlayer.getDuration());
            mSeekBar.setProgress(progress);
        }
        Log.d("Seek progeress ", String.valueOf(progress));


    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mSeekBar.setProgress(mMediaPlayer.getCurrentPosition());

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }




}
