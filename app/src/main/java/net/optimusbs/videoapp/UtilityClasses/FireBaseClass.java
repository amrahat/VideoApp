package net.optimusbs.videoapp.UtilityClasses;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

/**
 * Created by Santo on 1/3/2017.
 */

public class FireBaseClass {
    private final DatabaseReference favouriteVidRef;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference homeBannerRef, tagRef,videoRef;
    private String baseUrl = "https://videoapp-32254.firebaseio.com/";
    Context context;
    Gson gson;
    private DatabaseReference favouriteTagRef,commentRef,likeRef,notificationRef;

    public FireBaseClass(Context context) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        homeBannerRef = firebaseDatabase.getReference(Constants.HOME_BANNER_REF);
        tagRef = firebaseDatabase.getReference(Constants.TAG_REF);
        videoRef = firebaseDatabase.getReference(Constants.VIDEO_REF);
        favouriteVidRef = firebaseDatabase.getReference(Constants.FAVOURITE_REF);
        favouriteTagRef = firebaseDatabase.getReference(Constants.FAVOURITE_TAG_REF);
        commentRef = firebaseDatabase.getReference(Constants.COMMENT_REF);
        likeRef = firebaseDatabase.getReference(Constants.LIKE_REF);
        notificationRef = firebaseDatabase.getReference(Constants.NOTIFICATION_REF);
        this.context = context;
        gson = new Gson();

    }

    public DatabaseReference getFavouriteVidRef() {
        return favouriteVidRef;
    }
    public DatabaseReference getFavouriteTagRef() {
        return favouriteTagRef;
    }

    public void addVideoToDatabase(String videoId){
       // videoRef.child(videoId)
    }

    public void addVideoToDatabase(String videoId, String key,String value){
       if(value!=null && !value.isEmpty()){
           videoRef.child(videoId).child(key).setValue(value);
       }
    }

    public DatabaseReference getCommentRef() {
        return commentRef;
    }

    public void setCommentRef(DatabaseReference commentRef) {
        this.commentRef = commentRef;
    }

    public DatabaseReference getLikeRef() {
        return likeRef;
    }

    public void setLikeRef(DatabaseReference likeRef) {
        this.likeRef = likeRef;
    }

    public DatabaseReference getVideoRef() {
        return videoRef;
    }

    public DatabaseReference getTagRef() {
        return tagRef;
    }

    public DatabaseReference getNotificationRef() {
        return notificationRef;
    }
}
