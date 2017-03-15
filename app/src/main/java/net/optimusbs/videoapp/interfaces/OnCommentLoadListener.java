package net.optimusbs.videoapp.interfaces;

import net.optimusbs.videoapp.facebookmodels.CommentsResponse;
import net.optimusbs.videoapp.facebookmodels.FacebookComment;

import java.util.ArrayList;

/**
 * Created by AMRahat on 3/2/2017.
 */

public interface OnCommentLoadListener {
    void onCommentLoad(CommentsResponse commentsResponse);
}
