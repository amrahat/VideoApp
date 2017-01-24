package net.optimusbs.videoapp.Activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.widget.IconTextView;

import net.optimusbs.videoapp.Adapters.VideoListByTagAdapter;
import net.optimusbs.videoapp.Classes.Video;
import net.optimusbs.videoapp.Fragments.LoginDialog;
import net.optimusbs.videoapp.R;
import net.optimusbs.videoapp.UtilityClasses.Constants;
import net.optimusbs.videoapp.UtilityClasses.FireBaseClass;
import net.optimusbs.videoapp.UtilityClasses.VolleyRequest;
import net.optimusbs.videoapp.interfaces.DialogDismissListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Santo on 1/2/2017.
 */

public class VideoPlayer extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener, View.OnClickListener {
    @InjectView(R.id.title)
    TextView title;

    @InjectView(R.id.likeCount)
    TextView likeCount;

    @InjectView(R.id.description)
    TextView description;

    @InjectView(R.id.viewCount)
    TextView viewCount;

    @InjectView(R.id.commentCount)
    TextView commentCount;


    @InjectView(R.id.indicator_description_visibility)
    IconTextView indicatorDescription;

    @InjectView(R.id.titleLayout)
    RelativeLayout titleLayout;

    @InjectView(R.id.related_videos_list)
    RecyclerView relatedVideosList;

    @InjectView(R.id.like)
    IconTextView like;
    @InjectView(R.id.comment)
    IconTextView comment;
    @InjectView(R.id.share)
    IconTextView share;
    @InjectView(R.id.favourite)
    IconTextView favourite;

    @InjectView(R.id.like_layout)
    LinearLayout likeLayout;
    @InjectView(R.id.comment_layout)
    LinearLayout commentLayout;
    @InjectView(R.id.share_layout)
    LinearLayout shareLayout;
    @InjectView(R.id.favourite_layout)
    LinearLayout favouriteLayout;
    FireBaseClass fireBaseClass;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userDb, videoDb;

    String videoId;
    Video video;

    boolean isLikedByCurrentUser = false;
    boolean isFavouriteByCurrentUser = false;
    boolean isUserLoggedIn;
    String loggedInUserId;

    boolean description_layout_visible = false;
    String tag;

    String favouriteIcon,nonFavouriteIcon;

    int iconNormalColor, iconSelectedColor;

    LoginDialog loginDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Iconify.with(new FontAwesomeModule());
        setContentView(R.layout.fragment_youtube_player);
        loginDialog = new LoginDialog();
        fireBaseClass = new FireBaseClass(this);
        iconNormalColor = ContextCompat.getColor(this, R.color.video_player_bottom_icon_color);
        iconSelectedColor = ContextCompat.getColor(this, R.color.video_player_bottom_icon_selected_color);
        favouriteIcon = getResources().getString(R.string.icon_favourite_full);
        nonFavouriteIcon =  getResources().getString(R.string.icon_favourite_empty);


        ButterKnife.inject(this);
        initializeFireBase();
        YouTubePlayerView playerView = (YouTubePlayerView) findViewById(R.id.player);
        playerView.initialize(Constants.API_KEY, this);
        initializeRecyclerView();
        getIntentData();

        if (AccessToken.getCurrentAccessToken() != null) {
            isUserLoggedIn = true;
            loggedInUserId = Profile.getCurrentProfile().getId();
            setIsLikedByCurrentUser();
            setIsFavouriteByCurrentUser();

        } else {
            isUserLoggedIn = false;
        }


        likeLayout.setOnClickListener(this);
        commentLayout.setOnClickListener(this);
        shareLayout.setOnClickListener(this);
        favouriteLayout.setOnClickListener(this);


    }

    private void setIsFavouriteByCurrentUser() {
        userDb.child(loggedInUserId).child(Constants.FAVOURITE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                isFavouriteByCurrentUser = dataSnapshot.hasChild(videoId);
                if (isFavouriteByCurrentUser) {
                    favourite.setTextColor(iconSelectedColor);
                    favourite.setText(favouriteIcon);
                } else {
                    favourite.setTextColor(iconNormalColor);
                    favourite.setText(nonFavouriteIcon);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setIsLikedByCurrentUser() {
        userDb.child(loggedInUserId).child(Constants.LIKED).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                isLikedByCurrentUser = dataSnapshot.hasChild(videoId);
                if (isLikedByCurrentUser) {
                    like.setTextColor(iconSelectedColor);
                } else {
                    like.setTextColor(iconNormalColor);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void initializeFireBase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        userDb = firebaseDatabase.getReference(Constants.USERDB);
        videoDb = firebaseDatabase.getReference(Constants.VIDEO_REF);
    }

    private void initializeRecyclerView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        relatedVideosList.setLayoutManager(mLayoutManager);
        relatedVideosList.setItemAnimator(new DefaultItemAnimator());
    }

    private void getIntentData() {
        Bundle bundle = getIntent().getBundleExtra("bundle");
        if (bundle.containsKey("video_id")) {
            videoId = getIntent().getBundleExtra("bundle").getString("video_id");
            getVideoData();
        } else if (bundle.containsKey("video")) {
            video = (Video) bundle.getSerializable("video");
            videoId = video.getId();
            setUpViews(video);
        }
        tag = bundle.getString("tag");

        if (tag != null && !tag.isEmpty()) {
            getRelatedVideosByTag(tag);
        } else {
            loadRelatedVideoFromYoutube(videoId);

        }


    }

    private void getRelatedVideosByTag(final String tag) {
        DatabaseReference tagRef = FirebaseDatabase.getInstance().getReference(Constants.TAG_REF);
        tagRef.child(tag).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> videoList = (ArrayList<String>) dataSnapshot.getValue();
                videoList.remove(videoId);
                //  Log.d("videoremove", String.valueOf(videoList.contains(video)));
                if (videoList.size() > 0) {
                    Log.d("sizebooro", "sizebooro");
                    VideoListByTagAdapter videoListByTagAdapter = new VideoListByTagAdapter(videoList, getApplicationContext(), tag);

                    relatedVideosList.setAdapter(videoListByTagAdapter);
                } else {
                    Log.d("sizebooro", "choto");

                    loadRelatedVideoFromYoutube(videoId);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadRelatedVideoFromYoutube(String videoId) {
        String relatedVideoUrl = Constants.getRelatedVideoUrl(videoId);
        VolleyRequest.sendRequestGet(getApplicationContext(), relatedVideoUrl, new VolleyRequest.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                ArrayList<String> videoList = new ArrayList<String>();

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray itemsArray = jsonObject.getJSONArray("items");
                    for (int i = 0; i < itemsArray.length(); i++) {
                        JSONObject item = itemsArray.getJSONObject(i);
                        String videoId = item.getJSONObject("id").getString("videoId");
                        videoList.add(videoId);
                    }
                    VideoListByTagAdapter videoListByTagAdapter = new VideoListByTagAdapter(videoList, getApplicationContext(), tag);

                    relatedVideosList.setAdapter(videoListByTagAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void getVideoData() {
        String url = Constants.getDataUrl(videoId);
        Log.d("url", url);
        VolleyRequest.sendRequestGet(this, url, new VolleyRequest.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                //Log.d("result", result);
                parseJson(result);
            }
        });
    }

    private void parseJson(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject itemsObject = jsonObject.getJSONArray("items").getJSONObject(0);
            JSONObject snippetObject = itemsObject.getJSONObject("snippet");
            JSONObject statisticsObject = itemsObject.getJSONObject("statistics");
            String videoId = itemsObject.getString("id");
            String thumbnail = snippetObject.getJSONObject("thumbnails").getJSONObject("default").getString("url");
            String publishedAt = snippetObject.getString("publishedAt");
            String title = snippetObject.getString("title");
            String description = snippetObject.getString("description");
            String viewCount = statisticsObject.getString("viewCount");

            String likeCount;
            if (statisticsObject.has("likeCount")) {
                likeCount = statisticsObject.getString("likeCount");
            } else {
                likeCount = "0";
            }
            String commentCount = statisticsObject.getString("commentCount");


            Video video = new Video(videoId, title, description, publishedAt, viewCount, likeCount, commentCount, thumbnail);

            setUpViews(video);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setUpViews(Video video) {
        title.setText(video.getTitle());
        description.setText(video.getDescription());

        titleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (description_layout_visible) {
                    description.setVisibility(View.GONE);
                    description_layout_visible = false;
                    indicatorDescription.setText("{fa-caret-down}");
                } else {
                    description.setVisibility(View.VISIBLE);
                    description_layout_visible = true;
                    indicatorDescription.setText("{fa-caret-up}");

                }
            }
        });

        viewCount.setText(video.getViewCount());
        likeCount.setText(video.getLikeCount());
        commentCount.setText(video.getCommentCount());

        fireBaseClass.addVideoToDatabase(videoId, Constants.VIDEO_TITLE, video.getTitle());
        fireBaseClass.addVideoToDatabase(videoId, Constants.VIDEO_THUMBNAIL, video.getThumbnail());
        fireBaseClass.addVideoToDatabase(videoId, Constants.VIDEO_DESCRIPTION, video.getDescription());
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.cueVideo(videoId);
        youTubePlayer.play();
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.like_layout:
                doLike();
                break;
            case R.id.share_layout:
                doShare();
                break;
            case R.id.comment_layout:
                break;
            case R.id.favourite_layout:
                doFavourite();
                break;
        }

    }

    private void doShare() {
    }

    private void doLike() {
        if (isUserLoggedIn) {
            if (isLikedByCurrentUser) {
                userDb.child(loggedInUserId).child(Constants.LIKED).child(videoId).removeValue();
                videoDb.child(videoId).child(Constants.USER_WHO_LIKED).child(loggedInUserId).removeValue();
                like.setTextColor(iconNormalColor);
                isLikedByCurrentUser = false;
            } else {
                userDb.child(loggedInUserId).child(Constants.LIKED).child(videoId).setValue(1);
                videoDb.child(videoId).child(Constants.USER_WHO_LIKED).child(loggedInUserId).setValue(1);

                like.setTextColor(iconSelectedColor);
                isLikedByCurrentUser = true;
            }
        } else {
            loginDialog.setDialogDismissListener(new DialogDismissListener() {
                @Override
                public void onDismiss(boolean success) {
                    if(success){
                        isUserLoggedIn = true;
                        loggedInUserId = Profile.getCurrentProfile().getId();
                        doLike();
                    }
                }
            });
            loginDialog.show(getFragmentManager(),"");

            //dologin
        }


    }

    private void doFavourite(){
        if (isUserLoggedIn) {
            if (isFavouriteByCurrentUser) {
                userDb.child(loggedInUserId).child(Constants.FAVOURITE).child(videoId).removeValue();
                videoDb.child(videoId).child(Constants.USER_WHO_FAVOURITE).child(loggedInUserId).removeValue();
                favourite.setTextColor(iconSelectedColor);
                favourite.setText(favouriteIcon);
                isFavouriteByCurrentUser = false;
            } else {
                userDb.child(loggedInUserId).child(Constants.FAVOURITE).child(videoId).setValue(1);
                videoDb.child(videoId).child(Constants.USER_WHO_FAVOURITE).child(loggedInUserId).setValue(1);

                favourite.setTextColor(iconSelectedColor);
                favourite.setText(favouriteIcon);

                isFavouriteByCurrentUser = true;
            }


        }else {
            loginDialog.setDialogDismissListener(new DialogDismissListener() {
                @Override
                public void onDismiss(boolean success) {
                    if(success){
                        isUserLoggedIn = true;
                        loggedInUserId = Profile.getCurrentProfile().getId();
                        doFavourite();
                    }
                }
            });
            loginDialog.show(getFragmentManager(),"");



        }
    }
}
