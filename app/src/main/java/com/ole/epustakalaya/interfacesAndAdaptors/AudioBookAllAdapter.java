package com.ole.epustakalaya.interfacesAndAdaptors;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ole.epustakalaya.R;
import com.ole.epustakalaya.models.AudioDetails;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by bikram on 2/12/16.
 */
public class AudioBookAllAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private final List<AudioDetails> mBooklist;
    private final Context context;

//    private List<AudioDetails> mDataset;

    // The minimum amount of items to have below your current scroll position before loading more.
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    protected String BASE_URL="http://www.pustakalaya.org";





    //    private ArrayList<String> mDataset;

    private Typeface face;

    public AudioBookAllAdapter(Context context, List<AudioDetails> itemList) {
        this.mBooklist = itemList;
        this.context = context;
    }






    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_list, null);
        MyAudioAllViewHolder rcv = new MyAudioAllViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

//        ((MyAudioAllViewHolder) holder).getTextViewName().setText(mBooklist.get(position).getAuthor());
        ((MyAudioAllViewHolder) holder).getAuthorViewName().setText(mBooklist.get(position).getAuthor());
//        holder.mPicture.setImageResource(mBooklist.get(position).getImage());
        Picasso.with(context).load(BASE_URL + mBooklist.get(position).getImage())
                    .into(((MyAudioAllViewHolder) holder).getPicture());

    }

    @Override
    public int getItemCount() {
        return this.mBooklist.size();
    }





}
