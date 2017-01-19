package com.ole.epustakalaya.interfacesAndAdaptors;

import com.ole.epustakalaya.models.Book;

/**
 * Created by bishnu on 11/10/14.
 */

public interface BookFetchListener {
    int BookTypeEditorsPicks = 101;
    int BookTypeLatestBooksFirstLoad = 102;
    int BookTypeLatestBooksLoadMore = 103;

    void onFetchedBookReceived(Book[] books,int BooksType);
}
