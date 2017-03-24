
package net.optimusbs.videoapp.facebookmodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Paging implements Serializable {

    @SerializedName("cursors")
    private Cursors mCursors;
    @SerializedName("next")
    private String mNext;

    public Cursors getCursors() {
        return mCursors;
    }

    public void setCursors(Cursors cursors) {
        mCursors = cursors;
    }

    public String getNext() {
        return mNext;
    }

    public void setNext(String next) {
        mNext = next;
    }

}
