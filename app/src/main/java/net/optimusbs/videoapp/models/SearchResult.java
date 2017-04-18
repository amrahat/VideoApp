package net.optimusbs.videoapp.models;

import java.io.Serializable;

/**
 * Created by AMRahat on 3/16/2017.
 */

public class SearchResult implements Serializable {
    private String id;
    private boolean isTag;
    private String title;
    private String thumbnail;
    private String viewCount;
    private long tagVideoCount;
    private long favouriteCount;
    private boolean favByCurrentUser;
    private String commentCount;
    private String likeCount;
    private String published_at;

    public SearchResult() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isTag() {
        return isTag;
    }

    public void setIsTag(boolean tag) {
        isTag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setThumbnail(String s) {
        this.thumbnail = s;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setViewCount(String s) {
        this.viewCount = s;
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setTagVideoCount(long childrenCount) {
        this.tagVideoCount = childrenCount;
    }

    public long getTagVideoCount() {
        return tagVideoCount;
    }

    public void setFavouriteCount(long childrenCount) {
        this.favouriteCount = childrenCount;
    }

    public long getFavouriteCount() {
        return favouriteCount;
    }

    public void setFavouriteByCurrentUser(boolean b) {
        this.favByCurrentUser = b;
    }

    public boolean isFavByCurrentUser() {
        return favByCurrentUser;
    }

    public void setCommentCount(String s) {
        this.commentCount = s;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setLikeCount(String s) {
        this.likeCount = s;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public String getPublished_at() {
        return published_at;
    }

    public void setPublished_at(String published_at) {
        this.published_at = published_at;
    }
}
