package net.optimusbs.videoapp.UtilityClasses;

import android.content.Context;

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
    private DatabaseReference homeBannerRef, tagRef;
    private String baseUrl = "https://videoapp-32254.firebaseio.com/";
    Context context;
    Gson gson;
    public FireBaseClass(Context context) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        homeBannerRef = firebaseDatabase.getReference(Constants.HOME_BANNER_REF);
        tagRef = firebaseDatabase.getReference(Constants.TAG_REF);
        this.context = context;
        gson = new Gson();

    }
    private void getVideoByTag(String tag, final ArrayList<String> videoIdList){
       /* String url = baseUrl+"tags/"+tag;


        VolleyRequest.sendRequestGet(context, url, new VolleyRequest.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                if(result!=null && !result.equals("null")){
                    Type type = new TypeToken<ArrayList<String>>(){}.getType();
                    videoIdList = gson.fromJson(result,type)
                }
            }
        });*/
     //   final ArrayList<String> videoIdList = new ArrayList<>();

        /*tagRef.child(tag).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                    DataSnapshot snapshot = iterator.next();
                    String videoId = snapshot.getKey();
                    videoIdList.add(videoId);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/


    }
}
