package com.ole.epustakalaya.interfacesAndAdaptors;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ole.epustakalaya.MainActivity;
import com.ole.epustakalaya.R;
import com.ole.epustakalaya.models.RecycleItem;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by bikram on 2/12/16.
 */
public class AudioBookAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

//    private List<RecycleItem> mDataset;

    // The minimum amount of items to have below your current scroll position before loading more.
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    protected String BASE_URL="http://www.pustakalaya.org";




    private Context mContext;
    //    private ArrayList<String> mDataset;
    private List<RecycleItem> mBookList;
    private Typeface face;


    public AudioBookAdapter(List<RecycleItem> BookList , RecyclerView recyclerView,Context con) {
        this.mBookList = BookList;
        this.mContext=con;
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

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
//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_row, parent, false);
//
//        return new MyViewHolder(v);

        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycle_row, parent, false);

            vh = new MyAudioViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.progressbar_item, parent, false);

            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

//        face= Typeface.createFromAsset(mContext.getAssets(), "fonts/Kelson Sans Light.otf");

        final RecycleItem Book=mBookList.get(position);
        if (holder instanceof MyAudioViewHolder) {
            ((MyAudioViewHolder) holder).getTextViewName().setText(Book.title);
//            ((MyAudioViewHolder) holder).getTextViewName().setTypeface(face);
            ((MyAudioViewHolder) holder).getAuthorViewName().setText(Book.Author);
//            ((MyAudioViewHolder) holder).getAuthorViewName().setTypeface(face);
            Picasso.with(mContext).load(BASE_URL + Book.image)
                    .into(((MyAudioViewHolder) holder).getPicture());

        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mBookList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public void setLoaded() {
        loading = false;
    }



    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }
//
//    @Override
//    public void onBindViewHolder(MyViewHolder holder, int position) {
//        final RecycleItem Book=mBookList.get(position);
//
//        MyViewHolder.getTextViewName().setText(Book.title);
//        MyViewHolder.getAuthorViewName().setText(String.valueOf(position));
//
//
//        Log.d("book id", Book.id);
//
//        Picasso.with(mContext).load(BASE_URL+Book.image)
//                .into(MyViewHolder.getPicture());
//
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Toast.makeText(this,Book.title,Toast.LENGTH_LONG).show();
//                Log.d("clicked",Book.id);
//                Intent i=new Intent(mContext, AudioDetails.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                i.putExtra("bookID",Book.id);
//                mContext.startActivity(i);
//            }
//        });
//
//
//
//
//    }


    @Override
    public int getItemCount() {
        return mBookList.size();

    }





}
