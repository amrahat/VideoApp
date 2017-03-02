package net.optimusbs.videoapp.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.optimusbs.videoapp.adapters.VideoListByTagAdapter;
import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.UtilityClasses.Constants;
import net.optimusbs.videoapp.UtilityClasses.SetUpToolbar;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyVideosFragment extends Fragment implements FacebookCallback<LoginResult>{

    RecyclerView recyclerView;
    int indicatorSmallColor;
    int indicatorLargeColor;
    LinearLayout notLoggedInLayout;
    CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;
    LoginButton loginButton;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userDb;
    public MyVideosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_videos, container, false);
        initializeView(view);
        initializeFireBase();
        setBackGroundColor();
        getFavVideos();
        SetUpToolbar.setTitle("My Videos",getActivity());
        return view;
    }

    private void setBackGroundColor() {
        indicatorSmallColor = ContextCompat.getColor(getContext(), R.color.topbar_color);
        indicatorLargeColor = ContextCompat.getColor(getContext(), R.color.toolbar_color);
        getActivity().findViewById(R.id.my_videos).setBackgroundColor(indicatorLargeColor);
        getActivity().findViewById(R.id.home).setBackgroundColor(indicatorSmallColor);
        getActivity().findViewById(R.id.all_videos).setBackgroundColor(indicatorSmallColor);
        getActivity().findViewById(R.id.notification).setBackgroundColor(indicatorSmallColor);

    }
    private void initializeFireBase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        userDb = firebaseDatabase.getReference(Constants.USERDB);
    }
    private void getFavVideos() {
        if(AccessToken.getCurrentAccessToken()!=null){
            String loggedInUserId = Profile.getCurrentProfile().getId();
            FirebaseDatabase.getInstance().getReference(Constants.USERDB).child(loggedInUserId).child(Constants.FAVOURITE).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                    ArrayList<String> videoIds = new ArrayList<String>();
                    while (iterator.hasNext()){
                        DataSnapshot snapshot = iterator.next();
                        String videoId = snapshot.getKey();
                        videoIds.add(videoId);
                    }

                    if(videoIds.size()!=0){
                        setUpRecyclerView(videoIds);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else {
            Log.d("here","here");
            notLoggedInLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setUpRecyclerView(ArrayList<String> videoIds) {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        VideoListByTagAdapter videoListByTagAdapter = new VideoListByTagAdapter(videoIds, getActivity(), "");
        recyclerView.setAdapter(videoListByTagAdapter);
    }

    private void initializeView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        notLoggedInLayout = (LinearLayout) view.findViewById(R.id.not_logged_in);
        loginButton = (LoginButton) view.findViewById(R.id.login_button_other);

        initializeLoginCallback();

    }

    private void initializeLoginCallback() {
        loginButton.setFragment(this);

        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager,this);

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
//                Log.d("here",oldAccessToken.getToken()+currentAccessToken.getToken());


            }
        };
    }


    @Override
    public void onSuccess(LoginResult loginResult) {
        Profile profile = Profile.getCurrentProfile();
        //  User user = new User(profile.getId(),profile.getName(),profile.getProfilePictureUri(500,500).toString());
        Log.d("idaslkdfjal",profile.getFirstName());
        userDb.child(profile.getId()).child(Constants.USER_IMAGE).setValue(profile.getProfilePictureUri(500,500).toString());
        userDb.child(profile.getId()).child(Constants.USER_NAME).setValue(profile.getName());
        notLoggedInLayout.setVisibility(View.GONE);
        getFavVideos();
    }

    @Override
    public void onCancel() {
        Log.d("here","cancel");

    }

    @Override
    public void onError(FacebookException error) {
        Log.d("here","error"+error.toString());

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);


    }
}
