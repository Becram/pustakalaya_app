package com.ole.epustakalaya.models;

/**
 * Created by bishnu on 9/18/15.
 */
public class AudioBook {

    public static class Chapter {
        public String id;
        public String fileURL;
        public String title;
    }

    public String id;
    public String title;
    public String coverImageURL;
    public String lang;
    public String author;
    public int views;
    Chapter[] chapters;
}

