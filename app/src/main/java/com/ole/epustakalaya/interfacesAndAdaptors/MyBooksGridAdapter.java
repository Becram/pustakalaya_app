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

import com.ole.epustakalaya.R;
import com.ole.epustakalaya.helper.ServerSideHelper;
import com.ole.epustakalaya.helper.Utility;
import com.ole.epustakalaya.models.Book;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by bishnu on 9/23/14.
 */
public class MyBooksGridAdapter extends BaseAdapter {

    private Context context;
    int placeholder = R.drawable.book_not_available;
    public Book[] books;
    /*ImageLoader imageLoader;*/

//    String baseUrl;

    public MyBooksGridAdapter(Context context, Book[] books){
        this.context = context;
        this.books = books;
        Log.w("bookgridadapter book count", String.valueOf(getCount()));
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
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.mainImg.setImageResource(placeholder);

//        new ImgLoad(context,mainImg,progressBar).execute(books[position].coverImageURL);
        File imageFile = new File(Utility.pdfDirectory+books[position].coverImageURL);
        if(imageFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            viewHolder.mainImg.setImageBitmap(myBitmap);
        } else {
            viewHolder.mainImg.setImageResource(placeholder);
        }
        return convertView;
    }

    private class ViewHolder {
        ImageView mainImg;
    }

}