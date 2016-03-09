package com.ole.epustakalaya.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bikram on 1/25/16.
 */
public class ModelAudioBookDetails {


    private String type;
    private AudioContent content;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * @return The type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return The content
     */
    public AudioContent getContent() {
        return content;
    }

    /**
     * @param content The content
     */
    public void setContent(AudioContent content) {
        this.content = content;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }


    public class AudioContent {

        private String bookId;
        private String title;
        private String desc;
        private String author;
        private String reader;
        private String publisher;
        private String lang;
        private String genre;
        private String place;
        private String date;
        private String subj;
        private String notes;
        private String keyword;
        private String link;
        private String image;
        private String views;
        private Integer chapterCount;
        private List<Chapter> chapters = new ArrayList<Chapter>();
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        /**
         * @return The bookId
         */
        public String getBookId() {
            return bookId;
        }

        /**
         * @param bookId The bookId
         */
        public void setBookId(String bookId) {
            this.bookId = bookId;
        }

        /**
         * @return The title
         */
        public String getTitle() {
            return title;
        }

        /**
         * @param title The title
         */
        public void setTitle(String title) {
            this.title = title;
        }

        /**
         * @return The desc
         */
        public String getDesc() {
            return desc;
        }

        /**
         * @param desc The desc
         */
        public void setDesc(String desc) {
            this.desc = desc;
        }

        /**
         * @return The author
         */
        public String getAuthor() {
            return author;
        }

        /**
         * @param author The author
         */
        public void setAuthor(String author) {
            this.author = author;
        }

        /**
         * @return The reader
         */
        public String getReader() {
            return reader;
        }

        /**
         * @param reader The reader
         */
        public void setReader(String reader) {
            this.reader = reader;
        }

        /**
         * @return The publisher
         */
        public String getPublisher() {
            return publisher;
        }

        /**
         * @param publisher The publisher
         */
        public void setPublisher(String publisher) {
            this.publisher = publisher;
        }

        /**
         * @return The lang
         */
        public String getLang() {
            return lang;
        }

        /**
         * @param lang The lang
         */
        public void setLang(String lang) {
            this.lang = lang;
        }

        /**
         * @return The genre
         */
        public String getGenre() {
            return genre;
        }

        /**
         * @param genre The genre
         */
        public void setGenre(String genre) {
            this.genre = genre;
        }

        /**
         * @return The place
         */
        public String getPlace() {
            return place;
        }

        /**
         * @param place The place
         */
        public void setPlace(String place) {
            this.place = place;
        }

        /**
         * @return The date
         */
        public String getDate() {
            return date;
        }

        /**
         * @param date The date
         */
        public void setDate(String date) {
            this.date = date;
        }

        /**
         * @return The subj
         */
        public String getSubj() {
            return subj;
        }

        /**
         * @param subj The subj
         */
        public void setSubj(String subj) {
            this.subj = subj;
        }

        /**
         * @return The notes
         */
        public String getNotes() {
            return notes;
        }

        /**
         * @param notes The notes
         */
        public void setNotes(String notes) {
            this.notes = notes;
        }

        /**
         * @return The keyword
         */
        public String getKeyword() {
            return keyword;
        }

        /**
         * @param keyword The keyword
         */
        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }

        /**
         * @return The link
         */
        public String getLink() {
            return link;
        }

        /**
         * @param link The link
         */
        public void setLink(String link) {
            this.link = link;
        }

        /**
         * @return The image
         */
        public String getImage() {
            return image;
        }

        /**
         * @param image The image
         */
        public void setImage(String image) {
            this.image = image;
        }

        /**
         * @return The views
         */
        public String getViews() {
            return views;
        }

        /**
         * @param views The views
         */
        public void setViews(String views) {
            this.views = views;
        }

        /**
         * @return The chapterCount
         */
        public String getChapterCount() {
            return String.valueOf(chapterCount);
        }

        /**
         * @param chapterCount The chapter_count
         */
        public void setChapterCount(Integer chapterCount) {
            this.chapterCount = chapterCount;
        }

        /**
         * @return The chapters
         */
        public List<Chapter> getChapters() {
            return chapters;
        }

        /**
         * @param chapters The chapters
         */
        public void setChapters(List<Chapter> chapters) {
            this.chapters = chapters;
        }

        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }

    }

    public class Chapter {

        private String id;
        private String file;
        private String chapter;
        private Integer duration;
        private Integer size;

        /**
         * @return The id
         */
        public String getId() {
            return id;
        }

        /**
         * @param id The id
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * @return The file
         */
        public String getFile() {
            return file;
        }

        /**
         * @param file The file
         */
        public void setFile(String file) {
            this.file = file;
        }

        /**
         * @return The chapter
         */
        public String getChapter() {
            return chapter;
        }

        /**
         * @param chapter The chapter
         */
        public void setChapter(String chapter) {
            this.chapter = chapter;
        }

        /**
         * @return The duration
         */
        public Integer getDuration() {
            return duration;
        }

        /**
         * @param duration The duration
         */
        public void setDuration(Integer duration) {
            this.duration = duration;
        }

        /**
         * @return The size
         */
        public Integer getSize() {
            return size;
        }

        /**
         * @param size The size
         */
        public void setSize(Integer size) {
            this.size = size;
        }

    }
}
