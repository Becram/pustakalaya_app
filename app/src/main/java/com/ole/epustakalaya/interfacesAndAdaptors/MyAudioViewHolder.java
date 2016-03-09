package com.ole.epustakalaya.interfacesAndAdaptors;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ole.epustakalaya.R;

/**
 * Created by bikram on 2/12/16.
 */
public class MyAudioViewHolder extends RecyclerView.ViewHolder{

    private TextView mName;

    private ImageView mPicture;
    private  TextView mAuthor;

    public MyAudioViewHolder(View itemView) {
        super(itemView);
        mPicture = (ImageView) itemView.findViewById(R.id.icon_recycle);
        mName = (TextView) itemView.findViewById(R.id.firstLine);
        mAuthor = (TextView) itemView.findViewById(R.id.secondLine);


    }

    public  TextView getTextViewName() {
        return mName;
    }

    public  ImageView getPicture() {
        return mPicture;
    }
    public  TextView getAuthorViewName() {
        return mAuthor;
    }
}
