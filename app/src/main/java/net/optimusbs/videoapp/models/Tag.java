package net.optimusbs.videoapp.models;

/**
 * Created by Santo on 1/4/2017.
 */

public class Tag {
    private String tagName;
    private int videoCount;
    private long favouriteCount;
    private boolean isFavouriteByCurrentUser;

    public Tag(String tagName, int videoCount) {
        this.tagName = tagName;
        this.videoCount = videoCount;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public int getVideoCount() {
        return videoCount;
    }

    public void setVideoCount(int videoCount) {
        this.videoCount = videoCount;
    }

    public long getFavouriteCount() {
        return favouriteCount;
    }

    public void setFavouriteCount(long favouriteCount) {
        this.favouriteCount = favouriteCount;
    }

    public boolean isFavouriteByCurrentUser() {
        return isFavouriteByCurrentUser;
    }

    public void setFavouriteByCurrentUser(boolean favouriteByCurrentUser) {
        isFavouriteByCurrentUser = favouriteByCurrentUser;
    }
}
