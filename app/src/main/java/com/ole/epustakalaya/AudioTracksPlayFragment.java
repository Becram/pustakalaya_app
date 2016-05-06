package com.ole.epustakalaya;


import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;

import android.content.SharedPreferences;
import android.media.AudioManager;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ole.epustakalaya.helper.Utility;
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
import java.util.concurrent.TimeUnit;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by bikram on 3/10/16.
 */
public class AudioTracksPlayFragment extends Fragment implements Callback<ModelAudioBookDetails>,View.OnClickListener, MediaPlayer.OnBufferingUpdateListener, View.OnTouchListener, MediaPlayer.OnCompletionListener {

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
    private final Handler handler = new Handler();
    private View toolbar_view;
    private static int a=0;
    private BroadcastReceiver downloadCompleteBroadcastReceiver;
    private Context context;

    private TelephonyManager telManager;
    public static String book_id;
    public static String book_title;
    public static String book_author;
    public static String book_image;
    public SharedPreferences pref;
    public  SharedPreferences.Editor editor;
    public ImageView mPlayerNext;
    public  TextView mStartTime;
    public  TextView mEndtime;
    private CountDownTimer mCountDownTimer;
    public int i;
    private TextView mTitle;
    private int mediaFileLengthInMilliseconds;

//    private Context context;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TelephonyManager mgr = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        if(mgr != null) {
            mgr.listen(PhoneStatus, PhoneStateListener.LISTEN_CALL_STATE);
        }


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

    @Override
    public void onDestroy() {
        super.onDestroy();
//        seekHandler.removeCallbacks(run);
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
        initViews(v);
        return  v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        seekHandler.removeCallbacks(run);
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void initViews(View v){

        mSelectedTrackTitle = (TextView) v.findViewById(R.id.selected_track_title);
        mSelectedTrackChapter = (TextView) v.findViewById(R.id.selected_chapter);
        mSelectedTrackStatus = (TextView) v.findViewById(R.id.selected_playpause_status);
        mSelectedTrackImage = (ImageView) v.findViewById(R.id.selected_track_image);
        mPlayerControl = (ImageView) v.findViewById(R.id.player_control);
        mSeekBar= (SeekBar) v.findViewById(R.id.seekBar);
        audioRecyclerView = (RecyclerView) v.findViewById(R.id.track_recycler_view);
        audioRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        audioRecyclerView.setLayoutManager(mLayoutManager);
//        mSeekBar.setOnSeekBarChangeListener(this);
        mSeekBar.setMax(99);
        toolbar_view=v.findViewById(R.id.view);
        mStartTime= (TextView)  v.findViewById(R.id.my_start);
        mEndtime=   (TextView)  v.findViewById(R.id.my_end);
        mTitle=   (TextView)  v.findViewById(R.id.title_play);




    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.pustakalaya.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        APIInterface = retrofit.create(PustakalayaApiInterface.class);
        Call<ModelAudioBookDetails> call = APIInterface.getAudioBooksDetails(AudioAllAMainDetails.get_bookid);
        book_title=AudioAllAMainDetails.get_booktitle;
        book_id=AudioAllAMainDetails.get_bookid;
        book_author=AudioAllAMainDetails.get_author;
        book_image=AudioAllAMainDetails.get_image;



        call.enqueue(this);


        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnBufferingUpdateListener(this);

        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                togglePlayPause();
//            }
//        });




        int total = mMediaPlayer.getDuration();
        mediaFileLengthInMilliseconds =total;
        Log.d("track", String.valueOf(total));

    }

//    Runnable run = new Runnable() {
//        @Override
//        public void run() {
////            primarySeekBarProgressUpdater();
//            SeekUpdation();
//
//        }
//
//
//
//    };

    private void primarySeekBarProgressUpdater() {
        mSeekBar.setProgress((int) (((float) mMediaPlayer.getCurrentPosition() / mediaFileLengthInMilliseconds) * 100)); // This math construction give a percentage of "was playing"/"song length"
        if (mMediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    primarySeekBarProgressUpdater();
                }
            };
            handler.postDelayed(notification, 1000);
        }
    }

//    public void SeekUpdation() {
////        mMediaPlayer.getCurrentPosition()=a;
//
//        seekHandler.removeCallbacks(run);
//        try {
//            if (mMediaPlayer != null)
//                mSeekBar.setMax(mMediaPlayer.getDuration());
//
//                mSeekBar.setProgress(mMediaPlayer.getCurrentPosition());
//
//                 mStartTime.setText(Utility.getConvertedTimeFromMS(mMediaPlayer.getCurrentPosition()));
//                 Log.d("raw1",Utility.getConvertedTimeFromMS(mMediaPlayer.getCurrentPosition()));
//                 Log.d("raw2",String.valueOf(mMediaPlayer.getCurrentPosition()));
//
//            Log.d("max", String.valueOf(mMediaPlayer.getDuration()));
//
//
//            seekHandler.postDelayed(run, 1000);
//
//        }catch (InstantiationException e){
//
//            Log.d("max", e.toString());
//        }
//
//    }


//    private void togglePlayPause() {
//
//            /** ImageButton onClick event handler. Method which start/pause mediaplayer playing */
//            try {
//               // setup song from http://www.hrupin.com/wp-content/uploads/mp3/testsong_20_sec.mp3 URL to mediaplayer data source
//                mMediaPlayer.prepare(); // you must call this method after setup the datasource in setDataSource method. After calling prepare() the instance of MediaPlayer starts load data from URL to internal buffer.
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            mediaFileLengthInMilliseconds = mMediaPlayer.getDuration(); // gets the song length in milliseconds from URL
//
//            if (!mMediaPlayer.isPlaying()) {
//                mMediaPlayer.start();
//                mPlayerControl.setImageResource(R.drawable.button_pause);
//            } else {
//                mMediaPlayer.pause();
//                mPlayerControl.setImageResource(R.drawable.button_play);
//            }
//
//            primarySeekBarProgressUpdater();
//
//    }

    @Override
    public void onResponse(final Response<ModelAudioBookDetails> response, Retrofit retrofit) {
        mListItems=createList(response);
        Log.d("list",mListItems.get(0).getTitle());
        mTitle.setText(response.body().getContent().getTitle());
        mAdapter = new AudioTracksAdapter(createList(response),audioRecyclerView ,getActivity());
        audioRecyclerView.clearFocus();
        audioRecyclerView.setAdapter(mAdapter);
        final ArrayList<String> myTracks=getAllTracks(response);

        ItemClickSupport.addTo(audioRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                Track track = mListItems.get(position);

                mSelectedTrackTitle.setText(response.body().getContent().getTitle());




                mSelectedTrackChapter.setText(track.getTitle());
                mStartTime.setText(Utility.getConvertedTime(track.getTrackDuration()));
                mEndtime.setText(Utility.getConvertedTime(track.getTrackDuration()));

                Picasso.with(getContext()).load(BASE_URL + response.body().getContent().getImage()).into(mSelectedTrackImage);

                Log.d("item", String.valueOf(position));

                playpauseToggle();
//                if (mMediaPlayer.isPlaying()) {
//                    mMediaPlayer.stop();
//                    mMediaPlayer.reset();
//                }else {
//
//                    Log.d("paused","from inside");
//                    mMediaPlayer.stop();
//                    mMediaPlayer.reset();
//                }

                try {


                    Log.d("audio list",myTracks.toString());
                    String audio_url = getTrack(myTracks,position);
//                    String audio_url = BASE_URL + createList(response).get(position).getTrackURL();
                    String fixedUrl = audio_url.replaceAll("\\s", "%20");
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
    public ArrayList<String> getAllTracks(Response<ModelAudioBookDetails> response){

        ArrayList<String> audioList=new ArrayList<String>();
        audioList.clear();
        for (int i = 0; i <  response.body().getContent().getChapters().size(); i++){

            audioList.add(BASE_URL + createList(response).get(i).getTrackURL());
        }


        return audioList;
    }
    public String getTrack(ArrayList<String> trackList,int position){

        String trackURL=trackList.get(position);

        return trackURL;
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







    PhoneStateListener PhoneStatus=new PhoneStateListener(){

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                mMediaPlayer.pause();
                Log.d("call status","phone recieved");

                //Incoming call: Pause music
            } else if(state == TelephonyManager.CALL_STATE_IDLE) {
                //Not in call: Play music
            } else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {
//                mMediaPlayer.start();
                //A call is dialing, active or on hold
            }
            super.onCallStateChanged(state, incomingNumber);
        }


    };


    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        mSeekBar.setSecondaryProgress(percent);
        Log.d("buffering",String.valueOf(percent));

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.seekBar) {
            /** Seekbar onTouch event handler. Method which seeks MediaPlayer to seekBar primary progress position*/
            if (mMediaPlayer.isPlaying()) {
                SeekBar sb = (SeekBar) v;
                int playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100) * sb.getProgress();
                mMediaPlayer.seekTo(playPositionInMillisecconds);
            }
        }
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mPlayerControl.setImageResource(R.drawable.button_play);

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.player_control) {
            playpauseToggle();

        }
    }
    public void playpauseToggle(){

        /** ImageButton onClick event handler. Method which start/pause mediaplayer playing */
        try {
            // setup song from http://www.hrupin.com/wp-content/uploads/mp3/testsong_20_sec.mp3 URL to mediaplayer data source
            mMediaPlayer.prepare(); // you must call this method after setup the datasource in setDataSource method. After calling prepare() the instance of MediaPlayer starts load data from URL to internal buffer.
        } catch (Exception e) {
            e.printStackTrace();
        }

        mediaFileLengthInMilliseconds = mMediaPlayer.getDuration(); // gets the song length in milliseconds from URL

        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
            mPlayerControl.setImageResource(R.drawable.button_pause);
        } else {
            mMediaPlayer.pause();
            mPlayerControl.setImageResource(R.drawable.button_play);
        }
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mSeekBar.setOnTouchListener(this);

        primarySeekBarProgressUpdater();
    }
}
