package com.ole.epustakalaya.interfacesAndAdaptors;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.ole.epustakalaya.R;


/**
 * Created by bikram on 2/4/16.
 */
public class ProgressAudio extends RecyclerView.ViewHolder {
    public ProgressBar progressAudio;

    public ProgressAudio(View v) {
        super(v);
        progressAudio = (ProgressBar) v.findViewById(R.id.progress_audiolist);
    }
}