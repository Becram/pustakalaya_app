package com.ole.epustakalaya.models;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bikram on 1/22/16.
 */
public class AllAudioBooks {

    private String type;
    private Integer count;
    private List<Content> content = new ArrayList<Content>();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The type
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     * The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return
     * The count
     */
    public Integer getCount() {
        return count;
    }

    /**
     *
     * @param count
     * The count
     */
    public void setCount(Integer count) {
        this.count = count;
    }

    /**
     *
     * @return
     * The content
     */
    public List<Content> getContent() {
        return content;
    }

    /**
     *
     * @param content
     * The content
     */
    public void setContent(List<Content> content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }


    public class Content {

        private String id;
        private String title;
        private String cover;
        private String lang;
        private String author;
        private String views;
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        /**
         *
         * @return
         * The id
         */
        public String getId() {
            return id;
        }

        /**
         *
         * @param id
         * The id
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         *
         * @return
         * The title
         */
        public String getTitle() {
            return title;
        }

        /**
         *
         * @param title
         * The title
         */
        public void setTitle(String title) {
            this.title = title;
        }

        /**
         *
         * @return
         * The cover
         */
        public String getCover() {
            return cover;
        }

        /**
         *
         * @param cover
         * The cover
         */
        public void setCover(String cover) {
            this.cover = cover;
        }

        /**
         *
         * @return
         * The lang
         */
        public String getLang() {
            return lang;
        }

        /**
         *
         * @param lang
         * The lang
         */
        public void setLang(String lang) {
            this.lang = lang;
        }

        /**
         *
         * @return
         * The author
         */
        public String getAuthor() {
            return author;
        }

        /**
         *
         * @param author
         * The author
         */
        public void setAuthor(String author) {
            this.author = author;
        }

        /**
         *
         * @return
         * The views
         */
        public String getViews() {
            return views;
        }

        /**
         *
         * @param views
         * The views
         */
        public void setViews(String views) {
            this.views = views;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }

        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }

    }


}
