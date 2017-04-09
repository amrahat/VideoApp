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
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.UtilityClasses.Constants;
import net.optimusbs.videoapp.UtilityClasses.FireBaseClass;
import net.optimusbs.videoapp.UtilityClasses.SetUpToolbar;
import net.optimusbs.videoapp.adapters.VideoListByTagAdapter2;
import net.optimusbs.videoapp.models.Video;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyVideosFragment extends Fragment implements FacebookCallback<LoginResult> {

    RecyclerView recyclerView;
    int indicatorSmallColor;
    int indicatorLargeColor;
    LinearLayout notLoggedInLayout;
    CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;
    LoginButton loginButton;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userDb;
    private DatabaseReference videoRef;
    private ProfileTracker mProfileTracker;
    private ValueEventListener myFavVideoValueEventListener;
    private DatabaseReference databaseReference;
    private FireBaseClass fireBaseClass;

    public MyVideosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onPause() {
        if (databaseReference != null && myFavVideoValueEventListener != null) {
            databaseReference.removeEventListener(myFavVideoValueEventListener);
        }
        super.onPause();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_videos, container, false);
        initializeView(view);
        initializeFireBase();
        setBackGroundColor();
        getFavVideos(Profile.getCurrentProfile());
        SetUpToolbar.setTitle("My Videos", getActivity());
        return view;
    }

    private void setBackGroundColor() {
        indicatorSmallColor = ContextCompat.getColor(getContext(), R.color.topbar_color);
        indicatorLargeColor = ContextCompat.getColor(getContext(), R.color.toolbar_color);
        /*getActivity().findViewById(R.id.my_videos).setBackgroundColor(indicatorLargeColor);
        getActivity().findViewById(R.id.home).setBackgroundColor(indicatorSmallColor);
        getActivity().findViewById(R.id.all_videos).setBackgroundColor(indicatorSmallColor);
        getActivity().findViewById(R.id.notification).setBackgroundColor(indicatorSmallColor);*/

        getActivity().findViewById(R.id.my_videos_bar).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.all_videos_bar).setVisibility(View.GONE);
        getActivity().findViewById(R.id.notification_bar).setVisibility(View.GONE);
        getActivity().findViewById(R.id.recent_bar).setVisibility(View.GONE);

    }

    private void initializeFireBase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        userDb = firebaseDatabase.getReference(Constants.USERDB);
        videoRef = firebaseDatabase.getReference(Constants.VIDEO_REF);
        fireBaseClass = new FireBaseClass(getContext());
    }

    private void getFavVideos(Profile profile) {
        if (profile != null) {
            String loggedInUserId = Profile.getCurrentProfile().getId();
            myFavVideoValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                    final ArrayList<Video> videos = new ArrayList<Video>();
                    if (!iterator.hasNext()) {
                        Toast.makeText(getContext(), "No data found", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    while (iterator.hasNext()) {
                        DataSnapshot snapshot = iterator.next();
                        videoRef.child(snapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                final Video video = dataSnapshot.getValue(Video.class);
                                video.setId(dataSnapshot.getKey());
                                fireBaseClass.getCommentRef().child(video.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        video.setCommentCount(String.valueOf(dataSnapshot.getChildrenCount()));

                                        fireBaseClass.getLikeRef().child(video.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                video.setLikeCount(String.valueOf(dataSnapshot.getChildrenCount()));
                                                videos.add(video);

                                                if (!iterator.hasNext()) {
                                                    setUpRecyclerView(videos);

                                                }

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            databaseReference = FirebaseDatabase.getInstance().getReference(Constants.USERDB).child(loggedInUserId).child(Constants.FAVOURITE);
            databaseReference.addValueEventListener(myFavVideoValueEventListener);
        } else {
            Log.d("here", "here");
            notLoggedInLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setUpRecyclerView(ArrayList<Video> videoIds) {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        VideoListByTagAdapter2 videoListByTagAdapter = new VideoListByTagAdapter2(videoIds, getActivity(), "");
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
        loginButton.registerCallback(callbackManager, this);

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
//                Log.d("here",oldAccessToken.getToken()+currentAccessToken.getToken());


            }
        };
    }


    @Override
    public void onSuccess(LoginResult loginResult) {

        if (Profile.getCurrentProfile() == null) {
            mProfileTracker = new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                    // profile2 is the new profile
                    Log.d("facebook", profile2.getFirstName() + "asled");
                    mProfileTracker.stopTracking();
                    setProfileValueInFirebase(profile2);
                    getFavVideos(profile2);

                }
            };
            // no need to call startTracking() on mProfileTracker
            // because it is called by its constructor, internally.
        } else {
            Profile profile = Profile.getCurrentProfile();
            Log.d("facebook", profile.getFirstName());
            Log.d("idaslkdfjal", profile.getFirstName() + AccessToken.getCurrentAccessToken().getToken());

            setProfileValueInFirebase(profile);
            getFavVideos(profile);


        }


        notLoggedInLayout.setVisibility(View.GONE);
    }

    private void setProfileValueInFirebase(Profile profile) {
        userDb.child(profile.getId()).child(Constants.USER_IMAGE).setValue(profile.getProfilePictureUri(500, 500).toString());
        userDb.child(profile.getId()).child(Constants.USER_NAME).setValue(profile.getName());
    }

    @Override
    public void onCancel() {
        Log.d("here", "cancel");

    }

    @Override
    public void onError(FacebookException error) {
        Log.d("here", "error" + error.toString());

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
