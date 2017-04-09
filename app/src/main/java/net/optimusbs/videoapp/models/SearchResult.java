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
}
