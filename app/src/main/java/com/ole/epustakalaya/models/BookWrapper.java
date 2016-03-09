package com.ole.epustakalaya.models;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bikram on 1/21/16.
 */
public class BookWrapper {

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

    /**
     * Created by bikram on 1/21/16.
     */
    public static class Content {

        private String pid;
        private String bookCover;
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        /**
         *
         * @return
         * The pid
         */
        public String getPid() {
            return pid;
        }

        /**
         *
         * @param pid
         * The pid
         */
        public void setPid(String pid) {
            this.pid = pid;
        }

        /**
         *
         * @return
         * The bookCover
         */
        public String getBookCover() {
            return bookCover;
        }

        /**
         *
         * @param bookCover
         * The bookCover
         */
        public void setBookCover(String bookCover) {
            this.bookCover = bookCover;
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
