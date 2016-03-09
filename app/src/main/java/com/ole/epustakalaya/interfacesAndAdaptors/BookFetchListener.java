package com.ole.epustakalaya.interfacesAndAdaptors;

import com.ole.epustakalaya.models.Book;

/**
 * Created by bishnu on 11/10/14.
 */

public interface BookFetchListener {
    public static int BookTypeEditorsPicks = 101;
    public static int BookTypeLatestBooksFirstLoad = 102;
    public static int BookTypeLatestBooksLoadMore = 103;

    void onFetchedBookReceived(Book[] books,int BooksType);
}
