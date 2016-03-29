package com.ole.epustakalaya.models;

/**
 * Created by bikram on 3/25/16.
 */
public class AudioBookDB {


    public String pid;
    public String title;
    public String author;
    public String coverImageURL;
    public String trackURL;

    public int views;

    public String getPID(){ return pid; }
    public String getTitle(){ return title; }
    public String getAuthor(){ return author; }
    public String getCover(){ return coverImageURL; }
    public String getURL(){ return trackURL; }


    public void setPID(String pid){this.pid=pid;}
    public void setTitle(String title){this.title=title;}
    public void setAuthor(String author){this.author=author;}
    public void setCover(String coverImageURL){this.coverImageURL=coverImageURL;}
    public void setURL(String trackURL){this.trackURL=trackURL;}

}