package com.ole.epustakalaya.models;

/**
 * Created by bishnu on 9/22/14.
 */
public class Section {

    public String title;
    public String pid;
    public int subSectionCount;
    public SubSection[] subSections;

    public  Section(){

    }

    public Section(String title,String pid, int subSectionCount){
        this.title  = title;
        this.pid = pid;
        this.subSectionCount = subSectionCount;
    }


}
