package net.optimusbs.videoapp.models;

import java.io.Serializable;

/**
 * Created by Santo on 1/2/2017.
 */

public class Video implements Serializable {
    String id,title,description,publishedAt,viewCount,likeCount,commentCount,thumbnail;

    public Video() {
    }

    public Video(String id, String title, String description, String publishedAt, String viewCount, String likeCount, String commentCount, String thumbnail) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.publishedAt = publishedAt;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.thumbnail = thumbnail;

    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }


    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Video){
            Video video = (Video) obj;
            return this.getId().equals(video.getId());
        }
        return false;
    }



    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.getId() != null  ? this.getId().hashCode() : 0);
        return hash;
    }
}
