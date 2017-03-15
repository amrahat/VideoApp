
package net.optimusbs.videoapp.facebookmodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FacebookComment implements Serializable {

    @SerializedName("created_time")
    private String mCreatedTime;
    @SerializedName("from")
    private From mFrom;
    @SerializedName("id")
    private String mId;
    @SerializedName("message")
    private String mMessage;

    public String getCreatedTime() {
        return mCreatedTime;
    }

    public void setCreatedTime(String createdTime) {
        mCreatedTime = createdTime;
    }

    public From getFrom() {
        return mFrom;
    }

    public void setFrom(From from) {
        mFrom = from;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

}
