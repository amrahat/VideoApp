package net.optimusbs.videoapp.facebookmodels;

import java.util.ArrayList;

/**
 * Created by AMRahat on 2/27/2017.
 */

public class CommentsResponse {
    private ArrayList<FacebookComment> data;
    private Paging paging;
    private Summary summary;

    public ArrayList<FacebookComment> getData() {
        return data;
    }

    public void setData(ArrayList<FacebookComment> data) {
        this.data = data;
    }

    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }

    public Summary getSummary() {
        return summary;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }
}
