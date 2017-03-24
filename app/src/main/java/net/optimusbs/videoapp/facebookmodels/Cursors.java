
package net.optimusbs.videoapp.facebookmodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Cursors implements Serializable {

    @SerializedName("after")
    private String mAfter;
    @SerializedName("before")
    private String mBefore;

    public String getAfter() {
        return mAfter;
    }

    public void setAfter(String after) {
        mAfter = after;
    }

    public String getBefore() {
        return mBefore;
    }

    public void setBefore(String before) {
        mBefore = before;
    }

}
