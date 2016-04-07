package com.ole.epustakalaya.interfacesAndAdaptors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ole.epustakalaya.R;
import com.ole.epustakalaya.helper.ServerSideHelper;
import com.ole.epustakalaya.models.Book;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by bishnu on 9/23/14.
 */
public class BooksGridAdapter extends BaseAdapter {

    private Context context;
    int placeholder = R.drawable.book_not_available;
    String book_placeholder="N/A";
    public Book[] books;
    /*ImageLoader imageLoader;*/

    public boolean isCoverOnLocalStorage;

//    String baseUrl;

    public BooksGridAdapter(Context context,Book[] books){
        this.context = context;
        this.books = books;
        Log.w("bookgridadapter book count", String.valueOf(getCount()));
//        Log.w("book Title", String.valueOf(getItem(0)));

        /*imageLoader = new ImageLoader(context);*/
//        baseUrl = new ServerSideHelper(context).getBase_URL();
    }

    @Override
    public int getCount() {
        return books.length;
    }

    @Override
    public Object getItem(int position) {
        return books[position].title;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView==null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.book_grid_cell, parent,false);
            viewHolder = new ViewHolder();
            viewHolder.mainImg = (ImageView) convertView.findViewById(R.id.ivCoverImage);
            viewHolder.mainTitle = (TextView) convertView.findViewById(R.id.ivBookTitle);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.mainImg.setImageResource(placeholder);
        viewHolder.mainTitle.setText(book_placeholder);

//        new ImgLoad(context,mainImg,progressBar).execute(books[position].coverImageURL);
//        ImageLoader imageLoader = new ImageLoader(context);
        String imageUrl;
        if(isCoverOnLocalStorage){
           File file = new File(Environment.getExternalStorageDirectory(),"/Epustakalaya/pdf/"+books[position].coverImageURL);
            if(file.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//                viewHolder.mainImg.setImageBitmap(myBitmap);
                Log.v("ImageSize","ImageLoaded form local storage");
            } else {
                Log.d("book from gride view","not local");
                loadImageFromUrl(books[position].coverImageURL, viewHolder.mainImg);
//                viewHolder.mainTitle.setText(books[position].title);

            }
        } else {
            loadImageFromUrl(books[position].coverImageURL,viewHolder.mainImg);
            viewHolder.mainTitle.setText(books[position].title);
//            Log.d("title",books[position].title);
       }
        return convertView;
    }

    private void loadImageFromUrl(String url, final ImageView imgView){
        /*imageLoader.DisplayImage(new ServerSideHelper(context).getBase_URL()+ServerSideHelper.BOOK_COVER_PIC+books[position].coverImageURL,viewHolder.mainImg,viewHolder.progressBar);*/
        Picasso.with(context) //
                .load(new ServerSideHelper(context).getBase_URL()+ServerSideHelper.BOOK_COVER_PIC+url) //
                .placeholder(placeholder) //
             // .fit() //fit will stretch the image
                .tag(context) //
                .into(imgView, new Callback() {

                    @Override
                    public void onSuccess() {
                        Log.v("ImageSize","Successed?");

                    }

                    @Override
                    public void onError() {
                        Log.v("ImageSize","Error? Why?");
                    }
                });

    }

    private class ViewHolder {
        ImageView mainImg;
        TextView mainTitle;
    }

}