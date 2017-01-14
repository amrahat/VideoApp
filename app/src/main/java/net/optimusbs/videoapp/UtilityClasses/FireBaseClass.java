package net.optimusbs.videoapp.UtilityClasses;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.optimusbs.videoapp.Classes.Video;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Santo on 1/3/2017.
 */

public class FireBaseClass {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference homeBannerRef, tagRef,videoRef;
    private String baseUrl = "https://videoapp-32254.firebaseio.com/";
    Context context;
    Gson gson;
    public FireBaseClass(Context context) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        homeBannerRef = firebaseDatabase.getReference(Constants.HOME_BANNER_REF);
        tagRef = firebaseDatabase.getReference(Constants.TAG_REF);
        videoRef = firebaseDatabase.getReference(Constants.VIDEO_REF);
        this.context = context;
        gson = new Gson();

    }

    public void addVideoToDatabase(String videoId){
       // videoRef.child(videoId)
    }

    public void addVideoToDatabase(String videoId, String key,String value){
        videoRef.child(videoId).child(key).setValue(value);
    }
}
