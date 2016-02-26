package com.ole.epustakalaya.models;

/**
 * Created by bishnu on 9/22/14.
 */
public class SubSection {

    public String title;
    public String pid;
    public int bookCount;

    public SubSection(){}

    public SubSection(String title, String pid, int bookCount){
        this.title = title;
        this.pid = pid;
        this.bookCount =  bookCount;
    }

}
