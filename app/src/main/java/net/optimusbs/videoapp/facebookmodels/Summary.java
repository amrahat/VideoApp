
package net.optimusbs.videoapp.facebookmodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Summary implements Serializable{

    @SerializedName("can_comment")
    private Boolean mCanComment;
    @SerializedName("order")
    private String mOrder;
    @SerializedName("total_count")
    private Long mTotalCount;

    public Boolean getCanComment() {
        return mCanComment;
    }

    public void setCanComment(Boolean canComment) {
        mCanComment = canComment;
    }

    public String getOrder() {
        return mOrder;
    }

    public void setOrder(String order) {
        mOrder = order;
    }

    public Long getTotalCount() {
        return mTotalCount;
    }

    public void setTotalCount(Long totalCount) {
        mTotalCount = totalCount;
    }

}
