package com.ole.epustakalaya.interfacesAndAdaptors;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ole.epustakalaya.R;
import com.ole.epustakalaya.models.AudioBookDB;

import java.util.ArrayList;

/**
 * Created by bikram on 3/31/16.
 */
public class MyAudioDownloadAdapter extends BaseAdapter {

    Context context;
    ArrayList<AudioBookDB> bookList;
    private static LayoutInflater inflater = null;
    private RecyclerView.ViewHolder viewHolder;

    public MyAudioDownloadAdapter(Context context, ArrayList<AudioBookDB> empList) {
        this.context = context;
        this.bookList = empList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return bookList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inflater.inflate(R.layout.book_grid_cell, null);

//        ImageView Cover = (ImageView) convertView.findViewById(R.id.ivCoverImage);
        TextView Tilte = (TextView) convertView.findViewById(R.id.ivBookTitle);
//        TextView Tilte = (TextView) convertView.findViewById(R.id.ivBookTitle);

//        TextView codeTextView = (TextView) convertView.findViewById(R.id.tv_emp_id);
//        TextView nameTextView = (TextView) convertView.findViewById(R.id.tv_emp_name);
//        TextView emailTextView = (TextView) convertView.findViewById(R.id.tv_emp_email);
//        TextView addressTextView = (TextView) convertView.findViewById(R.id.tv_emp_address);

        AudioBookDB e = new AudioBookDB();
        e = bookList.get(position);
        Tilte.setText("Title:"+ e.getTitle());
//        nameTextView.setText("Name: " + e.getName());
//        emailTextView.setText("Email: " + e.getEmail());
//        addressTextView.setText("Address: " + e.getAddress());
        return convertView;
    }
}
