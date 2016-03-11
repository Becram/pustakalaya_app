package com.ole.epustakalaya.interfacesAndAdaptors;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ole.epustakalaya.R;

/**
 * Created by bikram on 3/10/16.
 */
public class MyAudioTracksViewHolder extends RecyclerView.ViewHolder {

private final TextView mFileSize;
private final TextView mDuration;
private final ImageView mDownload;
private  TextView mAudioTitle;

private  ImageView mPicture;
private  TextView mAuthor;

public MyAudioTracksViewHolder(View itemView) {
        super(itemView);
//        mPicture = (ImageView) itemView.findViewById(R.id.icon_recycle);
        mAudioTitle = (TextView) itemView.findViewById(R.id.track_title);
        mFileSize = (TextView) itemView.findViewById(R.id.size);
        mDuration = (TextView) itemView.findViewById(R.id.duration);
        mDownload = (ImageView) itemView.findViewById(R.id.downloader);
//        itemView.setOnClickListener(this);


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
public ImageView getDownloader() {
        return mDownload;
        }

//    @Override
//    public void onClick(View v) {
//
//        Track t=new Track();
//        Log.d("clicked", t.getTrackURL());
//
//    }
        }
