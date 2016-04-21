package com.ole.epustakalaya.interfacesAndAdaptors;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ole.epustakalaya.R;

/**
 * Created by bikram on 3/10/16.
 */
public class MyAudioTracksViewHolder extends RecyclerView.ViewHolder {

private final TextView mFileSize;
private final TextView mDuration;

private  TextView mAudioTitle;

private  ImageView mPicture;
private  TextView mAuthor;

public MyAudioTracksViewHolder(View itemView) {
        super(itemView);
//        mPicture = (ImageView) itemView.findViewById(R.id.icon_recycle);
        mAudioTitle = (TextView) itemView.findViewById(R.id.track_title);
        mFileSize = (TextView) itemView.findViewById(R.id.track_size);
        mDuration = (TextView) itemView.findViewById(R.id.track_duration);

//        itemView.setOnClickListener(this);

//        mDownload.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                        Log.d("download clicked","cool");
//
//                }
//        });


        }

public  TextView getTextViewAudioTitle() {
        return mAudioTitle;
        }

public  ImageView getPicture() {
        return mPicture;
        }
public TextView getFileSize() {
        return mFileSize;
        }
public TextView getDuration() {
        return mDuration;
        }





}
