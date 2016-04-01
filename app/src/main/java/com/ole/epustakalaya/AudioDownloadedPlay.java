package com.ole.epustakalaya;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.ole.epustakalaya.helper.MyAudioBooksDB;

/**
 * Created by bikram on 3/31/16.
 */
public class AudioDownloadedPlay extends AppCompatActivity {

    private String get_audiobookid;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.d_audio_play);
        get_audiobookid=getIntent().getStringExtra("audiobookID");
        MyAudioBooksDB db =new MyAudioBooksDB(getApplication());
        Log.d("aid",get_audiobookid);
        Log.d("aid_titile",db.getBook(get_audiobookid).getTitle());
        Log.d("aid_auther",db.getBook(get_audiobookid).getAuthor());
        TextView title= (TextView) findViewById(R.id.dTitle);
        TextView author= (TextView) findViewById(R.id.dAuthor);
        title.setText(db.getBook(get_audiobookid).getTitle());
        author.setText(db.getBook(get_audiobookid).getAuthor());
    }
}
