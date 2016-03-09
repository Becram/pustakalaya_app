package com.ole.epustakalaya.interfacesAndAdaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ole.epustakalaya.R;
import com.ole.epustakalaya.helper.ServerSideHelper;
import com.ole.epustakalaya.models.Book;
import com.squareup.picasso.Picasso;

public class BookListAdapter extends BaseAdapter {
    private Context context;
    int[] placeholder = {R.drawable.book_not_available};
    public Book[] books;
    /*ImageLoader imageLoader;*/
//    String base_url;

    public BookListAdapter(Context context, Book[] books){
        this.context = context;
        this.books = books;
        /*imageLoader = new ImageLoader(context);*/
//        base_url = new ServerSideHelper(context).getBase_URL();
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
        ViewHolder vh;
        if (convertView==null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.custom_row_books, parent,false);
            vh = new ViewHolder();


            vh.mainImge = (ImageView) convertView.findViewById(R.id.imageViewCustomBook);
            vh.title = (TextView) convertView.findViewById(R.id.bookListTitle);
            vh.authorName = (TextView) convertView.findViewById(R.id.bookListAuthorName);
            vh.viewsNum = (TextView) convertView.findViewById(R.id.bookListViewsNum);
            vh.downloadNum = (TextView) convertView.findViewById(R.id.bookListDownloadNum);

            convertView.setTag(vh);
        }
        else {
            vh = (ViewHolder)convertView.getTag();
        }
            vh.title.setText(books[position].title);
            vh.mainImge.setImageResource(placeholder[0]);

            vh.title.setText(books[position].title);
//            new ImgLoad(context,mainImg).execute(books[position].coverImageURL);
            /*ImageLoader imageLoader = new ImageLoader(context);*/
            /*imageLoader.DisplayImage(new ServerSideHelper(context).getBase_URL()+ServerSideHelper.BOOK_COVER_PIC+books[position].coverImageURL,vh.mainImge,null);*/
                Picasso.with(context) //
                .load(new ServerSideHelper(context).getBase_URL()+ServerSideHelper.BOOK_COVER_PIC+books[position].coverImageURL) //
                .placeholder(placeholder[0]) //
                .fit() //
                .tag(context) //
                .into(vh.mainImge);

            vh.authorName.setText(books[position].author);
            vh.viewsNum.setText(""+books[position].views);
            vh.downloadNum.setText("" + books[position].downloads);

        return convertView;
    }

    private class ViewHolder {
        ImageView mainImge;
        TextView title, authorName, viewsNum, downloadNum;
    }

}
