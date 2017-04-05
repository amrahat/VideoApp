package net.optimusbs.videoapp.interfaces;

import net.optimusbs.videoapp.models.FirebaseComment;

import java.util.ArrayList;

/**
 * Created by AMRahat on 3/23/2017.
 */

public interface OnFirebaseCommentLoadListener {
    void onFirebaseCommentLoad(ArrayList<FirebaseComment> firebaseComments, String currentTimestamp);
}
