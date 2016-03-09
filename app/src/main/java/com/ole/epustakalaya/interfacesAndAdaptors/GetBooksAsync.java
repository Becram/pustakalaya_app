package com.ole.epustakalaya.interfacesAndAdaptors;

import android.content.Context;
import android.os.AsyncTask;

import com.ole.epustakalaya.helper.ServerSideHelper;
import com.ole.epustakalaya.models.Book;

/*get the book list for editors picks*/
public class GetBooksAsync extends AsyncTask<Void , Void, Void > {
    private Book[] books;
    private Context context;
    BookFetchListener bookFetchListener;
    int BooksType;
    public int page;

    public GetBooksAsync(Context context, BookFetchListener bookFetchListener1, int BooksType1) {
        this.context = context;
        this.bookFetchListener = bookFetchListener1;
        this.BooksType = BooksType1;
    }

    public GetBooksAsync(Context context, BookFetchListener bookFetchListener1, int BooksType1, int page) {
        this.context = context;
        this.bookFetchListener = bookFetchListener1;
        this.BooksType = BooksType1;
        this.page = page;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Void  doInBackground(Void... params) {
        /*roshan on*/
//            get the editors picks
        switch (BooksType){
            case BookFetchListener.BookTypeEditorsPicks:
                books = new ServerSideHelper(this.context).getEditorsPickBooks();
                break;
            case BookFetchListener.BookTypeLatestBooksFirstLoad:
            case BookFetchListener.BookTypeLatestBooksLoadMore:
                 books = new ServerSideHelper(this.context).getLatestBookList(page);
                break;
            default:
                books =null;
        }

        return null;
       /*roshan off*/
    }

    @Override
    protected void onPostExecute(Void results) {
        super.onPostExecute(results);
        bookFetchListener.onFetchedBookReceived(books,BooksType);
    }
}
