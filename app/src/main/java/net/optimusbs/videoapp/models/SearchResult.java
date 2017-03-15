package net.optimusbs.videoapp.models;

import java.io.Serializable;

/**
 * Created by AMRahat on 3/16/2017.
 */

public class SearchResult implements Serializable {
    private String id;
    private boolean isTag;
    private String title;

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
}
